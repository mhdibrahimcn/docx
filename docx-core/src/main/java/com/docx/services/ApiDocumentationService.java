package com.docx.services;

import com.docx.models.ApiDocumentation;
import com.docx.models.ControllerDoc;
import com.docx.models.EndpointDoc;
import com.docx.models.ParameterDoc;
import com.docx.models.ResponseDoc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for enhancing API documentation with additional features
 * and formatting required for the modern HTML template.
 */
@Service
public class ApiDocumentationService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Converts API documentation to a template-ready format with proper
     * request body examples and enhanced parameter information.
     */
    public Map<String, Object> convertToTemplateFormat(ApiDocumentation apiDoc) {
        Map<String, Object> templateData = new HashMap<>();
        
        templateData.put("title", apiDoc.getTitle());
        templateData.put("version", apiDoc.getVersion());
        templateData.put("description", apiDoc.getDescription());
        templateData.put("baseUrl", apiDoc.getBaseUrl());
        
        if (apiDoc.getControllers() != null) {
            List<Map<String, Object>> controllers = apiDoc.getControllers().stream()
                    .map(this::convertControllerToTemplateFormat)
                    .collect(Collectors.toList());
            templateData.put("controllers", controllers);
        }
        
        return templateData;
    }

    private Map<String, Object> convertControllerToTemplateFormat(ControllerDoc controller) {
        Map<String, Object> controllerMap = new HashMap<>();
        
        controllerMap.put("name", controller.getName());
        controllerMap.put("className", controller.getClassName());
        controllerMap.put("description", controller.getDescription());
        controllerMap.put("baseUrl", controller.getBaseUrl());
        controllerMap.put("author", controller.getAuthor());
        controllerMap.put("since", controller.getSince());
        controllerMap.put("version", controller.getVersion());
        controllerMap.put("tags", controller.getTags());
        
        if (controller.getEndpoints() != null) {
            List<Map<String, Object>> endpoints = controller.getEndpoints().stream()
                    .map(this::convertEndpointToTemplateFormat)
                    .collect(Collectors.toList());
            controllerMap.put("endpoints", endpoints);
        }
        
        return controllerMap;
    }

    private Map<String, Object> convertEndpointToTemplateFormat(EndpointDoc endpoint) {
        Map<String, Object> endpointMap = new HashMap<>();
        
        endpointMap.put("name", endpoint.getName());
        endpointMap.put("description", endpoint.getDescription());
        endpointMap.put("httpMethod", endpoint.getHttpMethod());
        endpointMap.put("url", endpoint.getUrl());
        
        // Convert path variables
        if (endpoint.getPathVariables() != null) {
            List<Map<String, Object>> pathVars = endpoint.getPathVariables().stream()
                    .map(this::convertParameterToTemplateFormat)
                    .collect(Collectors.toList());
            endpointMap.put("pathVariables", pathVars);
        }
        
        // Convert query parameters
        if (endpoint.getQueryParameters() != null) {
            List<Map<String, Object>> queryParams = endpoint.getQueryParameters().stream()
                    .map(this::convertParameterToTemplateFormat)
                    .collect(Collectors.toList());
            endpointMap.put("queryParameters", queryParams);
        }
        
        // Convert request body with example
        if (endpoint.getRequestBody() != null) {
            Map<String, Object> requestBodyMap = convertRequestBodyToTemplateFormat(endpoint.getRequestBody());
            endpointMap.put("requestBody", requestBodyMap);
        }
        
        // Convert responses
        if (endpoint.getResponses() != null) {
            List<Map<String, Object>> responses = endpoint.getResponses().stream()
                    .map(this::convertResponseToTemplateFormat)
                    .collect(Collectors.toList());
            endpointMap.put("responses", responses);
        }
        
        endpointMap.put("examples", endpoint.getExamples());
        endpointMap.put("tags", endpoint.getTags());
        endpointMap.put("deprecated", endpoint.isDeprecated());
        endpointMap.put("apiNote", endpoint.getApiNote());
        endpointMap.put("apiDescription", endpoint.getApiDescription());
        
        return endpointMap;
    }

    private Map<String, Object> convertParameterToTemplateFormat(ParameterDoc param) {
        Map<String, Object> paramMap = new HashMap<>();
        
        paramMap.put("name", param.getName());
        paramMap.put("type", param.getType());
        paramMap.put("description", param.getDescription());
        paramMap.put("required", param.isRequired());
        paramMap.put("defaultValue", param.getDefaultValue());
        paramMap.put("example", param.getExample());
        
        return paramMap;
    }

    private Map<String, Object> convertRequestBodyToTemplateFormat(ParameterDoc requestBody) {
        Map<String, Object> bodyMap = new HashMap<>();
        
        bodyMap.put("type", requestBody.getType());
        bodyMap.put("description", requestBody.getDescription());
        bodyMap.put("required", requestBody.isRequired());
        
        // Generate a proper JSON example based on the type and existing example
        String example = generateRequestBodyExample(requestBody);
        bodyMap.put("example", example);
        
        return bodyMap;
    }

    private Map<String, Object> convertResponseToTemplateFormat(ResponseDoc response) {
        Map<String, Object> responseMap = new HashMap<>();
        
        responseMap.put("statusCode", response.getStatusCode());
        responseMap.put("description", response.getDescription());
        responseMap.put("mediaType", response.getMediaType());
        responseMap.put("example", response.getExample());
        
        return responseMap;
    }

    private String generateRequestBodyExample(ParameterDoc requestBody) {
        if (requestBody.getExample() != null && !requestBody.getExample().trim().isEmpty()) {
            // Try to format existing example as proper JSON
            try {
                Object parsedJson = objectMapper.readValue(requestBody.getExample(), Object.class);
                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(parsedJson);
            } catch (Exception e) {
                // Return as-is if not valid JSON
                return requestBody.getExample();
            }
        }
        
        // Generate basic example based on type
        String type = requestBody.getType();
        if (type != null) {
            return generateExampleByType(type, requestBody.getName());
        }
        
        return "{}";
    }

    private String generateExampleByType(String type, String fieldName) {
        try {
            Map<String, Object> example = new HashMap<>();
            
            if (type.contains("Product")) {
                example.put("name", "New Product");
                example.put("price", 99.99);
                example.put("category", "Electronics");
                example.put("available", true);
                example.put("description", "Product description");
                example.put("sku", "PROD-001");
                example.put("stockQuantity", 100);
            } else if (type.contains("User")) {
                example.put("username", "john_doe");
                example.put("email", "john@example.com");
                example.put("firstName", "John");
                example.put("lastName", "Doe");
                example.put("active", true);
            } else {
                // Generic example
                example.put("id", 1);
                example.put("name", "Example Item");
                example.put("active", true);
            }
            
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(example);
        } catch (Exception e) {
            return "{}";
        }
    }

    /**
     * Generates sample data for testing the API documentation template.
     */
    public ApiDocumentation generateSampleDocumentation() {
        ApiDocumentation apiDoc = new ApiDocumentation("Sample API Documentation", "1.0.0", "A comprehensive API for managing products and users");
        apiDoc.setBaseUrl("http://localhost:8080");
        
        // This would be populated by the actual scanning process
        // but useful for testing the template
        return apiDoc;
    }
}