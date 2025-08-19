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
            context.put("controllers", apiDocumentation.getControllers());
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
}