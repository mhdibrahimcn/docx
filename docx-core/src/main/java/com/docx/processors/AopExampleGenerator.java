package com.docx.processors;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class AopExampleGenerator {

    private final Map<String, List<String>> generatedExamples = new ConcurrentHashMap<>();
    private final Map<String, Integer> exampleCounters = new ConcurrentHashMap<>();

    @Before("@within(org.springframework.web.bind.annotation.RestController) || " +
            "@within(org.springframework.stereotype.Controller)")
    public void captureControllerMethodCall(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        if (isEndpointMethod(method)) {
            String methodKey = generateMethodKey(method);
            String example = generateExample(method, joinPoint.getArgs());
            
            addExample(methodKey, example);
        }
    }

    private boolean isEndpointMethod(Method method) {
        return method.isAnnotationPresent(RequestMapping.class) ||
               method.isAnnotationPresent(GetMapping.class) ||
               method.isAnnotationPresent(PostMapping.class) ||
               method.isAnnotationPresent(PutMapping.class) ||
               method.isAnnotationPresent(DeleteMapping.class) ||
               method.isAnnotationPresent(PatchMapping.class);
    }

    private String generateMethodKey(Method method) {
        return method.getDeclaringClass().getName() + "#" + method.getName();
    }

    private String generateExample(Method method, Object[] args) {
        StringBuilder example = new StringBuilder();
        
        // Get HTTP method
        String httpMethod = extractHttpMethod(method);
        example.append(httpMethod).append(" ");
        
        // Get base URL from class
        String baseUrl = extractBaseUrl(method.getDeclaringClass());
        
        // Get endpoint URL from method
        String endpointUrl = extractEndpointUrl(method);
        
        // Combine URLs
        String fullUrl = combineUrls(baseUrl, endpointUrl);
        
        // Replace path variables with actual values
        fullUrl = replacePathVariables(fullUrl, method, args);
        
        example.append(fullUrl);
        
        // Add query parameters if any
        String queryParams = extractQueryParameters(method, args);
        if (!queryParams.isEmpty()) {
            example.append("?").append(queryParams);
        }
        
        return example.toString();
    }

    private String extractHttpMethod(Method method) {
        if (method.isAnnotationPresent(GetMapping.class)) return "GET";
        if (method.isAnnotationPresent(PostMapping.class)) return "POST";
        if (method.isAnnotationPresent(PutMapping.class)) return "PUT";
        if (method.isAnnotationPresent(DeleteMapping.class)) return "DELETE";
        if (method.isAnnotationPresent(PatchMapping.class)) return "PATCH";
        
        if (method.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping mapping = method.getAnnotation(RequestMapping.class);
            if (mapping.method().length > 0) {
                return mapping.method()[0].name();
            }
        }
        
        return "GET";
    }

    private String extractBaseUrl(Class<?> controllerClass) {
        RequestMapping classMapping = controllerClass.getAnnotation(RequestMapping.class);
        if (classMapping != null) {
            if (classMapping.value().length > 0) return classMapping.value()[0];
            if (classMapping.path().length > 0) return classMapping.path()[0];
        }
        return "";
    }

    private String extractEndpointUrl(Method method) {
        String[] urls = null;
        
        if (method.isAnnotationPresent(GetMapping.class)) {
            GetMapping mapping = method.getAnnotation(GetMapping.class);
            urls = mapping.value().length > 0 ? mapping.value() : mapping.path();
        } else if (method.isAnnotationPresent(PostMapping.class)) {
            PostMapping mapping = method.getAnnotation(PostMapping.class);
            urls = mapping.value().length > 0 ? mapping.value() : mapping.path();
        } else if (method.isAnnotationPresent(PutMapping.class)) {
            PutMapping mapping = method.getAnnotation(PutMapping.class);
            urls = mapping.value().length > 0 ? mapping.value() : mapping.path();
        } else if (method.isAnnotationPresent(DeleteMapping.class)) {
            DeleteMapping mapping = method.getAnnotation(DeleteMapping.class);
            urls = mapping.value().length > 0 ? mapping.value() : mapping.path();
        } else if (method.isAnnotationPresent(PatchMapping.class)) {
            PatchMapping mapping = method.getAnnotation(PatchMapping.class);
            urls = mapping.value().length > 0 ? mapping.value() : mapping.path();
        } else if (method.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping mapping = method.getAnnotation(RequestMapping.class);
            urls = mapping.value().length > 0 ? mapping.value() : mapping.path();
        }
        
        return (urls != null && urls.length > 0) ? urls[0] : "";
    }

    private String combineUrls(String baseUrl, String endpointUrl) {
        if (baseUrl.isEmpty()) return endpointUrl;
        if (endpointUrl.isEmpty()) return baseUrl;
        
        StringBuilder combined = new StringBuilder(baseUrl);
        if (!baseUrl.endsWith("/") && !endpointUrl.startsWith("/")) {
            combined.append("/");
        }
        if (baseUrl.endsWith("/") && endpointUrl.startsWith("/")) {
            combined.deleteCharAt(combined.length() - 1);
        }
        combined.append(endpointUrl);
        
        return combined.toString();
    }

    private String replacePathVariables(String url, Method method, Object[] args) {
        Parameter[] parameters = method.getParameters();
        
        for (int i = 0; i < parameters.length && i < args.length; i++) {
            Parameter param = parameters[i];
            PathVariable pathVar = param.getAnnotation(PathVariable.class);
            
            if (pathVar != null && args[i] != null) {
                String paramName = pathVar.value().isEmpty() ? param.getName() : pathVar.value();
                String placeholder = "{" + paramName + "}";
                String value = generateExampleValue(args[i]);
                url = url.replace(placeholder, value);
            }
        }
        
        return url;
    }

    private String extractQueryParameters(Method method, Object[] args) {
        List<String> queryParams = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        
        for (int i = 0; i < parameters.length && i < args.length; i++) {
            Parameter param = parameters[i];
            RequestParam requestParam = param.getAnnotation(RequestParam.class);
            
            if (requestParam != null && args[i] != null) {
                String paramName = requestParam.value().isEmpty() ? param.getName() : requestParam.value();
                String value = generateExampleValue(args[i]);
                queryParams.add(paramName + "=" + value);
            }
        }
        
        return String.join("&", queryParams);
    }

    private String generateExampleValue(Object value) {
        if (value == null) return "null";
        
        // For demonstration purposes, use actual values with some sanitation
        String stringValue = value.toString();
        
        // Sanitize sensitive information
        if (stringValue.toLowerCase().contains("password") || 
            stringValue.toLowerCase().contains("secret") ||
            stringValue.toLowerCase().contains("token")) {
            return "***";
        }
        
        // For numeric types, use the actual value or generate a reasonable example
        if (value instanceof Number) {
            return stringValue;
        }
        
        // For strings, truncate if too long
        if (stringValue.length() > 50) {
            return stringValue.substring(0, 47) + "...";
        }
        
        return stringValue;
    }

    private void addExample(String methodKey, String example) {
        generatedExamples.computeIfAbsent(methodKey, k -> new ArrayList<>()).add(example);
        
        // Limit number of examples per method to avoid memory issues
        List<String> examples = generatedExamples.get(methodKey);
        if (examples.size() > 10) {
            examples.remove(0); // Remove oldest example
        }
        
        // Track example count
        exampleCounters.merge(methodKey, 1, Integer::sum);
    }

    public List<String> getExamplesForMethod(Method method) {
        String methodKey = generateMethodKey(method);
        return generatedExamples.getOrDefault(methodKey, new ArrayList<>());
    }

    public List<String> getExamplesForMethod(String className, String methodName) {
        String methodKey = className + "#" + methodName;
        return generatedExamples.getOrDefault(methodKey, new ArrayList<>());
    }

    public Map<String, List<String>> getAllExamples() {
        return new HashMap<>(generatedExamples);
    }

    public void clearExamples() {
        generatedExamples.clear();
        exampleCounters.clear();
    }

    public void clearExamplesForMethod(Method method) {
        String methodKey = generateMethodKey(method);
        generatedExamples.remove(methodKey);
        exampleCounters.remove(methodKey);
    }

    public int getExampleCount(String className, String methodName) {
        String methodKey = className + "#" + methodName;
        return exampleCounters.getOrDefault(methodKey, 0);
    }
}