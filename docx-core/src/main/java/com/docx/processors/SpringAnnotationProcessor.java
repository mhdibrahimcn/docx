package com.docx.processors;

import com.docx.models.*;
import com.docx.parsers.JavaDocParser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

@org.springframework.stereotype.Component
public class SpringAnnotationProcessor {

    private final JavaDocParser javaDocParser;
    private final ValidationConstraintParser validationParser;

    public SpringAnnotationProcessor() {
        this.javaDocParser = new JavaDocParser();
        this.validationParser = new ValidationConstraintParser();
    }

    public boolean isController(Class<?> clazz) {
        return clazz.isAnnotationPresent(RestController.class) || 
               clazz.isAnnotationPresent(Controller.class);
    }

    public ControllerDoc processController(Class<?> controllerClass) {
        String className = controllerClass.getSimpleName();
        String fullClassName = controllerClass.getName();
        
        ControllerDoc controllerDoc = new ControllerDoc(className, fullClassName);
        
        // Extract base URL from RequestMapping
        String baseUrl = extractBaseUrl(controllerClass);
        controllerDoc.setBaseUrl(baseUrl);
        
        // Process JavaDoc from class
        String classJavaDoc = extractJavaDocFromClass(controllerClass);
        if (classJavaDoc != null) {
            JavaDocParser.ParsedJavaDoc parsedJavaDoc = javaDocParser.parseJavaDoc(classJavaDoc);
            controllerDoc.setDescription(parsedJavaDoc.getDescription());
            controllerDoc.setAuthor(parsedJavaDoc.getAuthor());
            controllerDoc.setSince(parsedJavaDoc.getSince());
            controllerDoc.setVersion(parsedJavaDoc.getVersion());
        }
        
        // Process endpoints
        List<EndpointDoc> endpoints = Arrays.stream(controllerClass.getDeclaredMethods())
            .filter(this::isEndpointMethod)
            .map(method -> processEndpoint(method, baseUrl))
            .collect(Collectors.toList());
        
        controllerDoc.setEndpoints(endpoints);
        
        return controllerDoc;
    }

    private String extractBaseUrl(Class<?> controllerClass) {
        RequestMapping requestMapping = controllerClass.getAnnotation(RequestMapping.class);
        if (requestMapping != null && requestMapping.value().length > 0) {
            return requestMapping.value()[0];
        }
        if (requestMapping != null && requestMapping.path().length > 0) {
            return requestMapping.path()[0];
        }
        return "";
    }

    private boolean isEndpointMethod(Method method) {
        return method.isAnnotationPresent(RequestMapping.class) ||
               method.isAnnotationPresent(GetMapping.class) ||
               method.isAnnotationPresent(PostMapping.class) ||
               method.isAnnotationPresent(PutMapping.class) ||
               method.isAnnotationPresent(DeleteMapping.class) ||
               method.isAnnotationPresent(PatchMapping.class);
    }

    private EndpointDoc processEndpoint(Method method, String baseUrl) {
        String methodName = method.getName();
        
        // Extract HTTP method and URL
        HttpMethodInfo httpMethodInfo = extractHttpMethodInfo(method);
        String fullUrl = combineUrls(baseUrl, httpMethodInfo.url);
        
        EndpointDoc endpointDoc = new EndpointDoc(methodName, httpMethodInfo.method, fullUrl);
        
        // Process JavaDoc
        String methodJavaDoc = extractJavaDocFromMethod(method);
        if (methodJavaDoc != null) {
            JavaDocParser.ParsedJavaDoc parsedJavaDoc = javaDocParser.parseJavaDoc(methodJavaDoc);
            endpointDoc.setDescription(parsedJavaDoc.getDescription());
            endpointDoc.setApiNote(parsedJavaDoc.getApiNote());
            endpointDoc.setApiDescription(parsedJavaDoc.getApiDescription());
            
            // Set manual responses from JavaDoc
            List<ResponseDoc> responses = new ArrayList<>();
            responses.addAll(parsedJavaDoc.getApiResponses());
            responses.addAll(parsedJavaDoc.getApiErrors());
            endpointDoc.setResponses(responses);
            
            endpointDoc.setExamples(parsedJavaDoc.getApiExamples());
            endpointDoc.setDeprecated(parsedJavaDoc.getDeprecated() != null);
        }
        
        // Process parameters
        processParameters(method, endpointDoc);
        
        // Process response type
        processResponseType(method, endpointDoc);
        
        // Auto-generate response codes if not manually specified
        if (endpointDoc.getResponses() == null || endpointDoc.getResponses().isEmpty()) {
            generateDefaultResponses(endpointDoc, httpMethodInfo.method);
        }
        
        return endpointDoc;
    }

    private HttpMethodInfo extractHttpMethodInfo(Method method) {
        if (method.isAnnotationPresent(GetMapping.class)) {
            GetMapping mapping = method.getAnnotation(GetMapping.class);
            String url = getFirstValue(mapping.value(), mapping.path());
            return new HttpMethodInfo("GET", url);
        }
        if (method.isAnnotationPresent(PostMapping.class)) {
            PostMapping mapping = method.getAnnotation(PostMapping.class);
            String url = getFirstValue(mapping.value(), mapping.path());
            return new HttpMethodInfo("POST", url);
        }
        if (method.isAnnotationPresent(PutMapping.class)) {
            PutMapping mapping = method.getAnnotation(PutMapping.class);
            String url = getFirstValue(mapping.value(), mapping.path());
            return new HttpMethodInfo("PUT", url);
        }
        if (method.isAnnotationPresent(DeleteMapping.class)) {
            DeleteMapping mapping = method.getAnnotation(DeleteMapping.class);
            String url = getFirstValue(mapping.value(), mapping.path());
            return new HttpMethodInfo("DELETE", url);
        }
        if (method.isAnnotationPresent(PatchMapping.class)) {
            PatchMapping mapping = method.getAnnotation(PatchMapping.class);
            String url = getFirstValue(mapping.value(), mapping.path());
            return new HttpMethodInfo("PATCH", url);
        }
        if (method.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping mapping = method.getAnnotation(RequestMapping.class);
            String httpMethod = mapping.method().length > 0 ? 
                mapping.method()[0].name() : "GET";
            String url = getFirstValue(mapping.value(), mapping.path());
            return new HttpMethodInfo(httpMethod, url);
        }
        
        return new HttpMethodInfo("GET", "");
    }

    private String getFirstValue(String[] values, String[] paths) {
        if (values.length > 0) return values[0];
        if (paths.length > 0) return paths[0];
        return "";
    }

    private String combineUrls(String baseUrl, String endpointUrl) {
        if (baseUrl.isEmpty()) return endpointUrl;
        if (endpointUrl.isEmpty()) return baseUrl;
        
        String combined = baseUrl;
        if (!baseUrl.endsWith("/") && !endpointUrl.startsWith("/")) {
            combined += "/";
        }
        combined += endpointUrl;
        
        return combined;
    }

    private void processParameters(Method method, EndpointDoc endpointDoc) {
        Parameter[] parameters = method.getParameters();
        List<ParameterDoc> pathVariables = new ArrayList<>();
        List<ParameterDoc> queryParameters = new ArrayList<>();
        List<ParameterDoc> allParameters = new ArrayList<>();
        
        for (Parameter parameter : parameters) {
            ParameterDoc paramDoc = createParameterDoc(parameter);
            allParameters.add(paramDoc);
            
            if (parameter.isAnnotationPresent(PathVariable.class)) {
                paramDoc.setPathVariable(true);
                pathVariables.add(paramDoc);
            } else if (parameter.isAnnotationPresent(RequestParam.class)) {
                paramDoc.setRequestParam(true);
                queryParameters.add(paramDoc);
            } else if (parameter.isAnnotationPresent(RequestBody.class)) {
                paramDoc.setRequestBody(true);
                endpointDoc.setRequestBody(paramDoc);
            }
        }
        
        endpointDoc.setParameters(allParameters);
        endpointDoc.setPathVariables(pathVariables);
        endpointDoc.setQueryParameters(queryParameters);
    }

    private ParameterDoc createParameterDoc(Parameter parameter) {
        String name = parameter.getName();
        String type = parameter.getType().getSimpleName();
        
        ParameterDoc paramDoc = new ParameterDoc(name, type, "");
        
        // Check validation constraints
        List<ValidationConstraint> constraints = validationParser.parseConstraints(parameter);
        paramDoc.setConstraints(constraints);
        
        // Check if required based on annotations
        if (parameter.isAnnotationPresent(RequestParam.class)) {
            RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
            paramDoc.setRequired(requestParam.required());
            if (!requestParam.defaultValue().equals(ValueConstants.DEFAULT_NONE)) {
                paramDoc.setDefaultValue(requestParam.defaultValue());
            }
        }
        
        return paramDoc;
    }

    private void processResponseType(Method method, EndpointDoc endpointDoc) {
        Type returnType = method.getGenericReturnType();
        String responseType = getTypeDescription(returnType);
        
        ResponseDoc responseDoc = new ResponseDoc(200, "Success");
        responseDoc.setType(responseType);
        endpointDoc.setResponseBody(responseDoc);
    }

    private String getTypeDescription(Type type) {
        if (type instanceof Class<?>) {
            return ((Class<?>) type).getSimpleName();
        } else if (type instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) type;
            Type rawType = paramType.getRawType();
            Type[] typeArgs = paramType.getActualTypeArguments();
            
            StringBuilder sb = new StringBuilder();
            sb.append(((Class<?>) rawType).getSimpleName());
            if (typeArgs.length > 0) {
                sb.append("<");
                for (int i = 0; i < typeArgs.length; i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(getTypeDescription(typeArgs[i]));
                }
                sb.append(">");
            }
            return sb.toString();
        }
        return type.toString();
    }

    private void generateDefaultResponses(EndpointDoc endpointDoc, String httpMethod) {
        List<ResponseDoc> responses = new ArrayList<>();
        
        switch (httpMethod.toUpperCase()) {
            case "GET":
                responses.add(new ResponseDoc(200, "Successfully retrieved data"));
                responses.add(new ResponseDoc(404, "Resource not found"));
                break;
            case "POST":
                responses.add(new ResponseDoc(201, "Successfully created resource"));
                responses.add(new ResponseDoc(400, "Invalid request data"));
                break;
            case "PUT":
                responses.add(new ResponseDoc(200, "Successfully updated resource"));
                responses.add(new ResponseDoc(404, "Resource not found"));
                responses.add(new ResponseDoc(400, "Invalid request data"));
                break;
            case "DELETE":
                responses.add(new ResponseDoc(204, "Successfully deleted resource"));
                responses.add(new ResponseDoc(404, "Resource not found"));
                break;
            case "PATCH":
                responses.add(new ResponseDoc(200, "Successfully updated resource"));
                responses.add(new ResponseDoc(404, "Resource not found"));
                responses.add(new ResponseDoc(400, "Invalid request data"));
                break;
        }
        
        responses.add(new ResponseDoc(500, "Internal server error"));
        endpointDoc.setResponses(responses);
    }

    // Helper methods for extracting JavaDoc (would need actual implementation)
    private String extractJavaDocFromClass(Class<?> clazz) {
        // This would require access to source code or pre-processed JavaDoc
        // For now, return null - in real implementation, this would use
        // annotation processors or source code analysis
        return null;
    }

    private String extractJavaDocFromMethod(Method method) {
        // This would require access to source code or pre-processed JavaDoc
        // For now, return null - in real implementation, this would use
        // annotation processors or source code analysis
        return null;
    }

    private static class HttpMethodInfo {
        final String method;
        final String url;
        
        HttpMethodInfo(String method, String url) {
            this.method = method;
            this.url = url;
        }
    }
}