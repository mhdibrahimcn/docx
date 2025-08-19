package com.docx.processors;

import com.docx.models.ApiDocumentation;
import com.docx.models.ControllerDoc;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

public class ControllerScanner implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private final SpringAnnotationProcessor annotationProcessor;
    private final Object properties; // Would be DocxProperties in real implementation

    public ControllerScanner(Object properties) {
        this.properties = properties;
        this.annotationProcessor = new SpringAnnotationProcessor();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public ApiDocumentation scanAndGenerateDocumentation() {
        Set<Class<?>> controllerClasses = findAllControllers();
        
        List<ControllerDoc> controllers = controllerClasses.stream()
                .filter(this::shouldIncludeController)
                .map(annotationProcessor::processController)
                .collect(Collectors.toList());

        ApiDocumentation apiDoc = new ApiDocumentation();
        apiDoc.setTitle("API Documentation"); // Would come from properties
        apiDoc.setVersion("1.0.0");
        apiDoc.setDescription("Generated API Documentation");
        apiDoc.setControllers(controllers);

        return apiDoc;
    }

    private Set<Class<?>> findAllControllers() {
        Set<Class<?>> controllers = new HashSet<>();
        
        if (applicationContext != null) {
            // Scan using Spring application context
            String[] basePackages = determineBasePackages();
            
            for (String basePackage : basePackages) {
                Reflections reflections = new Reflections(basePackage, Scanners.TypesAnnotated);
                controllers.addAll(reflections.getTypesAnnotatedWith(RestController.class));
                controllers.addAll(reflections.getTypesAnnotatedWith(Controller.class));
            }
        }
        
        return controllers;
    }

    private String[] determineBasePackages() {
        // Get the main application class
        Map<String, Object> mainBeans = applicationContext.getBeansWithAnnotation(SpringBootApplication.class);
        
        if (!mainBeans.isEmpty()) {
            Object mainBean = mainBeans.values().iterator().next();
            String mainPackage = mainBean.getClass().getPackage().getName();
            return new String[]{mainPackage};
        }
        
        // Fallback to common base packages
        return new String[]{"com", "org"};
    }

    private boolean shouldIncludeController(Class<?> controllerClass) {
        String packageName = controllerClass.getPackage().getName();
        
        // Skip test packages by default
        if (packageName.contains(".test.") || packageName.endsWith(".test")) {
            return false; // Would check properties.getScan().isIncludeTestControllers()
        }
        
        // Check exclude patterns (would implement based on properties)
        return true;
    }

    public Set<Class<?>> getControllerClasses() {
        return findAllControllers();
    }
}