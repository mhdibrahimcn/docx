package com.docx.generators;

import com.docx.models.ApiDocumentation;
import com.docx.models.ControllerDoc;
import com.docx.models.EndpointDoc;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class DocumentationGenerator {

    private final Handlebars handlebars;
    private final HtmlTemplateEngine templateEngine;

    public DocumentationGenerator() {
        this.handlebars = new Handlebars();
        this.templateEngine = new HtmlTemplateEngine();
    }

    public String generateIndexHtml(ApiDocumentation apiDocumentation) {
        try {
            Map<String, Object> context = new HashMap<>();
            context.put("title", apiDocumentation.getTitle());
            context.put("version", apiDocumentation.getVersion());
            context.put("description", apiDocumentation.getDescription());
            context.put("controllers", convertControllersToMaps(apiDocumentation.getControllers()));
            context.put("theme", "auto"); // Would come from properties
            context.put("brandingColor", "#3B82F6");
            
            return templateEngine.generateIndexPage(context);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate index HTML", e);
        }
    }

    public String generateControllerHtml(ControllerDoc controller) {
        try {
            Map<String, Object> context = new HashMap<>();
            context.put("controller", controller);
            context.put("endpoints", controller.getEndpoints());
            context.put("theme", "auto");
            context.put("brandingColor", "#3B82F6");
            
            return templateEngine.generateControllerPage(context);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate controller HTML", e);
        }
    }

    public String generateEndpointHtml(EndpointDoc endpoint, ControllerDoc controller) {
        try {
            Map<String, Object> context = new HashMap<>();
            context.put("endpoint", endpoint);
            context.put("controller", controller);
            context.put("theme", "auto");
            context.put("brandingColor", "#3B82F6");
            
            return templateEngine.generateEndpointPage(context);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate endpoint HTML", e);
        }
    }

    public Map<String, String> generateAllDocumentation(ApiDocumentation apiDocumentation) {
        Map<String, String> generatedFiles = new HashMap<>();
        
        // Generate index page
        generatedFiles.put("index.html", generateIndexHtml(apiDocumentation));
        
        // Generate controller pages
        if (apiDocumentation.getControllers() != null) {
            for (ControllerDoc controller : apiDocumentation.getControllers()) {
                String controllerFileName = "controllers/" + controller.getName().toLowerCase() + ".html";
                generatedFiles.put(controllerFileName, generateControllerHtml(controller));
                
                // Generate individual endpoint pages
                if (controller.getEndpoints() != null) {
                    for (EndpointDoc endpoint : controller.getEndpoints()) {
                        String endpointFileName = String.format("endpoints/%s-%s.html", 
                            endpoint.getHttpMethod().toLowerCase(),
                            endpoint.getName().toLowerCase());
                        generatedFiles.put(endpointFileName, generateEndpointHtml(endpoint, controller));
                    }
                }
            }
        }
        
        return generatedFiles;
    }

    private String loadTemplate(String templateName) throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/templates/" + templateName)) {
            if (is == null) {
                throw new IOException("Template not found: " + templateName);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
    
    private java.util.List<Map<String, Object>> convertControllersToMaps(java.util.List<ControllerDoc> controllers) {
        if (controllers == null) {
            return java.util.Collections.emptyList();
        }
        
        return controllers.stream()
                .map(this::convertControllerToMap)
                .collect(java.util.stream.Collectors.toList());
    }
    
    private Map<String, Object> convertControllerToMap(ControllerDoc controller) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", controller.getName());
        map.put("className", controller.getClassName());
        map.put("description", controller.getDescription());
        map.put("baseUrl", controller.getBaseUrl());
        map.put("author", controller.getAuthor());
        map.put("since", controller.getSince());
        map.put("version", controller.getVersion());
        map.put("tags", controller.getTags());
        
        // Convert endpoints
        if (controller.getEndpoints() != null) {
            java.util.List<Map<String, Object>> endpointMaps = controller.getEndpoints().stream()
                    .map(this::convertEndpointToMap)
                    .collect(java.util.stream.Collectors.toList());
            map.put("endpoints", endpointMaps);
        } else {
            map.put("endpoints", java.util.Collections.emptyList());
        }
        
        return map;
    }
    
    private Map<String, Object> convertEndpointToMap(EndpointDoc endpoint) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", endpoint.getName());
        map.put("description", endpoint.getDescription());
        map.put("httpMethod", endpoint.getHttpMethod());
        map.put("url", endpoint.getUrl());
        map.put("parameters", endpoint.getParameters());
        map.put("pathVariables", endpoint.getPathVariables());
        map.put("queryParameters", endpoint.getQueryParameters());
        map.put("requestBody", endpoint.getRequestBody());
        map.put("responseBody", endpoint.getResponseBody());
        map.put("responses", endpoint.getResponses());
        map.put("examples", endpoint.getExamples());
        map.put("tags", endpoint.getTags());
        map.put("deprecated", endpoint.isDeprecated());
        map.put("apiNote", endpoint.getApiNote());
        map.put("apiDescription", endpoint.getApiDescription());
        return map;
    }
}