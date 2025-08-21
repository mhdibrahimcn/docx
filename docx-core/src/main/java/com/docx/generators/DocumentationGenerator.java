package com.docx.generators;

import com.docx.models.ApiDocumentation;
import com.docx.services.ApiDocumentationService;
import java.util.HashMap;
import java.util.Map;

public class DocumentationGenerator {

    private final HtmlTemplateEngine templateEngine;
    private final ApiDocumentationService apiDocumentationService;

    public DocumentationGenerator() {
        this.templateEngine = new HtmlTemplateEngine();
        this.apiDocumentationService = new ApiDocumentationService();
    }

    public DocumentationGenerator(ApiDocumentationService apiDocumentationService) {
        this.templateEngine = new HtmlTemplateEngine();
        this.apiDocumentationService = apiDocumentationService;
    }

    public String generateIndexHtml(ApiDocumentation apiDocumentation) {
        try {
            // Use the service to convert to template-ready format
            Map<String, Object> context = apiDocumentationService.convertToTemplateFormat(apiDocumentation);
            
            return templateEngine.generate(context);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate index HTML", e);
        }
    }

    public Map<String, String> generateAllDocumentation(ApiDocumentation apiDocumentation) {
        Map<String, String> generatedFiles = new HashMap<>();
        generatedFiles.put("index.html", generateIndexHtml(apiDocumentation));
        return generatedFiles;
    }
}
