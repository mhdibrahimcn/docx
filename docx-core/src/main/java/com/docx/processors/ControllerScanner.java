package com.docx.processors;

import com.docx.models.ApiDocumentation;
import com.docx.models.ControllerDoc;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

public class ControllerScanner implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(ControllerScanner.class);
    
    private ApplicationContext applicationContext;
    private final SpringAnnotationProcessor annotationProcessor;
    private final Object properties; // Would be DocxProperties in real implementation

    public ControllerScanner(Object properties) {
        this.properties = properties;
        this.annotationProcessor = new SpringAnnotationProcessor();
        logger.debug("ControllerScanner initialized with properties: {}", properties);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        logger.debug("ApplicationContext set: {}", applicationContext != null ? applicationContext.getClass().getSimpleName() : "null");
    }

    public ApiDocumentation scanAndGenerateDocumentation() {
        logger.info("Starting controller scanning and documentation generation...");
        
        Set<Class<?>> controllerClasses = findAllControllers();
        logger.info("Found {} controller classes: {}", controllerClasses.size(), 
                controllerClasses.stream().map(Class::getSimpleName).collect(Collectors.toList()));
        
        List<ControllerDoc> controllers = controllerClasses.stream()
                .filter(this::shouldIncludeController)
                .map(controllerClass -> {
                    logger.debug("Processing controller: {}", controllerClass.getName());
                    ControllerDoc doc = annotationProcessor.processController(controllerClass);
                    logger.debug("Processed controller {} -> {} endpoints", controllerClass.getSimpleName(), 
                            doc != null && doc.getEndpoints() != null ? doc.getEndpoints().size() : 0);
                    return doc;
                })
                .collect(Collectors.toList());

        logger.info("Successfully processed {} controllers for documentation", controllers.size());

        ApiDocumentation apiDoc = new ApiDocumentation();
        apiDoc.setTitle("API Documentation"); // Would come from properties
        apiDoc.setVersion("1.0.0");
        apiDoc.setDescription("Generated API Documentation");
        apiDoc.setControllers(controllers);

        logger.info("Documentation generation complete. Total controllers: {}", controllers.size());
        return apiDoc;
    }

    private Set<Class<?>> findAllControllers() {
        Set<Class<?>> controllers = new HashSet<>();
        
        logger.debug("Finding all controllers...");
        if (applicationContext != null) {
            logger.debug("ApplicationContext is available, proceeding with scanning");
            
            // Scan using Spring application context
            String[] basePackages = determineBasePackages();
            logger.info("Base packages for scanning: {}", Arrays.toString(basePackages));
            
            for (String basePackage : basePackages) {
                logger.debug("Scanning package: {}", basePackage);
                try {
                    Reflections reflections = new Reflections(basePackage, Scanners.TypesAnnotated);
                    
                    Set<Class<?>> restControllers = reflections.getTypesAnnotatedWith(RestController.class);
                    Set<Class<?>> regularControllers = reflections.getTypesAnnotatedWith(Controller.class);
                    
                    logger.debug("Found {} @RestController classes in package {}: {}", 
                            restControllers.size(), basePackage, 
                            restControllers.stream().map(Class::getSimpleName).collect(Collectors.toList()));
                    logger.debug("Found {} @Controller classes in package {}: {}", 
                            regularControllers.size(), basePackage,
                            regularControllers.stream().map(Class::getSimpleName).collect(Collectors.toList()));
                    
                    controllers.addAll(restControllers);
                    controllers.addAll(regularControllers);
                } catch (Exception e) {
                    logger.error("Error scanning package {}: {}", basePackage, e.getMessage(), e);
                }
            }
        } else {
            logger.warn("ApplicationContext is null, cannot scan for controllers");
        }
        
        logger.info("Total controllers found across all packages: {}", controllers.size());
        return controllers;
    }

    private String[] determineBasePackages() {
        logger.debug("Determining base packages for scanning...");
        
        // Get the main application class
        Map<String, Object> mainBeans = applicationContext.getBeansWithAnnotation(SpringBootApplication.class);
        logger.debug("Found {} beans with @SpringBootApplication annotation", mainBeans.size());
        
        if (!mainBeans.isEmpty()) {
            Object mainBean = mainBeans.values().iterator().next();
            String mainPackage = mainBean.getClass().getPackage().getName();
            logger.info("Using main application package as base: {} (from class {})", 
                    mainPackage, mainBean.getClass().getSimpleName());
            return new String[]{mainPackage};
        }
        
        // Fallback to common base packages
        logger.warn("No @SpringBootApplication found, falling back to common packages: com, org");
        return new String[]{"com", "org"};
    }

    private boolean shouldIncludeController(Class<?> controllerClass) {
        String packageName = controllerClass.getPackage().getName();
        logger.debug("Checking if controller should be included: {} (package: {})", 
                controllerClass.getSimpleName(), packageName);
        
        // Skip test packages by default
        if (packageName.contains(".test.") || packageName.endsWith(".test")) {
            logger.debug("Excluding controller {} - test package", controllerClass.getSimpleName());
            return false; // Would check properties.getScan().isIncludeTestControllers()
        }
        
        // Check exclude patterns (would implement based on properties)
        logger.debug("Including controller: {}", controllerClass.getSimpleName());
        return true;
    }

    public Set<Class<?>> getControllerClasses() {
        return findAllControllers();
    }
}