package com.docx.controllers;

import com.docx.generators.DocumentationGenerator;
import com.docx.models.ApiDocumentation;
import com.docx.processors.ControllerScanner;
import com.docx.properties.DocxProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("${docx.base-path:/docx}")
public class DocxController {

    private static final Logger logger = LoggerFactory.getLogger(DocxController.class);

    private final DocumentationGenerator documentationGenerator;
    private final ControllerScanner controllerScanner;
    private final DocxProperties properties;
    private ApiDocumentation cachedDocumentation;

    public DocxController(DocumentationGenerator documentationGenerator, 
                         ControllerScanner controllerScanner, 
                         DocxProperties properties) {
        this.documentationGenerator = documentationGenerator;
        this.controllerScanner = controllerScanner;
        this.properties = properties;
        logger.info("DocxController initialized with properties: {}", properties);
    }

    @GetMapping(value = {"", "/"}, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> index() {
        logger.info("DocX index page requested");
        ApiDocumentation apiDoc = getOrGenerateDocumentation();
        logger.info("Generating HTML for {} controllers", apiDoc.getControllers().size());
        String html = documentationGenerator.generateIndexHtml(apiDoc);
        logger.debug("Generated HTML length: {} characters", html.length());
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }

    @GetMapping(value = "/index.html", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> indexHtml() {
        return index();
    }

    @GetMapping(value = "/controllers/{controllerName}.html", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> controllerPage(@PathVariable String controllerName) {
        ApiDocumentation apiDoc = getOrGenerateDocumentation();
        
        return apiDoc.getControllers().stream()
                .filter(c -> c.getName().equalsIgnoreCase(controllerName))
                .findFirst()
                .map(controller -> {
                    String html = documentationGenerator.generateControllerHtml(controller);
                    return ResponseEntity.ok()
                            .contentType(MediaType.TEXT_HTML)
                            .body(html);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/endpoints/{httpMethod}-{endpointName}.html", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> endpointPage(@PathVariable String httpMethod, 
                                             @PathVariable String endpointName) {
        ApiDocumentation apiDoc = getOrGenerateDocumentation();
        
        for (var controller : apiDoc.getControllers()) {
            var endpoint = controller.getEndpoints().stream()
                    .filter(e -> e.getHttpMethod().equalsIgnoreCase(httpMethod) && 
                               e.getName().equalsIgnoreCase(endpointName))
                    .findFirst();
            
            if (endpoint.isPresent()) {
                String html = documentationGenerator.generateEndpointHtml(endpoint.get(), controller);
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_HTML)
                        .body(html);
            }
        }
        
        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/api/documentation.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiDocumentation> apiDocumentation() {
        logger.info("API documentation JSON requested");
        ApiDocumentation apiDoc = getOrGenerateDocumentation();
        logger.info("Returning API documentation with {} controllers", apiDoc.getControllers().size());
        return ResponseEntity.ok(apiDoc);
    }

    @GetMapping(value = "/api/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> refresh() {
        logger.info("Documentation refresh requested");
        cachedDocumentation = null; // Clear cache
        getOrGenerateDocumentation(); // Regenerate
        return ResponseEntity.ok(Map.of("status", "refreshed", "timestamp", String.valueOf(System.currentTimeMillis())));
    }

    @GetMapping(value = "/openapi.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> openApiSpec() {
        if (!properties.getFeatures().isExportOpenapi()) {
            return ResponseEntity.notFound().build();
        }
        
        ApiDocumentation apiDoc = getOrGenerateDocumentation();
        Map<String, Object> openApiSpec = convertToOpenApiSpec(apiDoc);
        return ResponseEntity.ok(openApiSpec);
    }

    private ApiDocumentation getOrGenerateDocumentation() {
        if (cachedDocumentation == null) {
            logger.info("No cached documentation found, generating new documentation...");
            cachedDocumentation = controllerScanner.scanAndGenerateDocumentation();
            logger.info("Documentation generated with {} controllers", 
                    cachedDocumentation != null ? cachedDocumentation.getControllers().size() : 0);
            if (cachedDocumentation != null && cachedDocumentation.getControllers() != null) {
                cachedDocumentation.getControllers().forEach(controller -> 
                    logger.debug("Controller found: {} with {} endpoints", 
                            controller.getName(), 
                            controller.getEndpoints() != null ? controller.getEndpoints().size() : 0));
            }
        } else {
            logger.debug("Using cached documentation with {} controllers", 
                    cachedDocumentation.getControllers().size());
        }
        return cachedDocumentation;
    }

    private Map<String, Object> convertToOpenApiSpec(ApiDocumentation apiDoc) {
        // Basic OpenAPI 3.0 structure
        return Map.of(
            "openapi", "3.0.3",
            "info", Map.of(
                "title", apiDoc.getTitle(),
                "version", apiDoc.getVersion(),
                "description", apiDoc.getDescription()
            ),
            "paths", Map.of(),
            "components", Map.of()
        );
    }
}