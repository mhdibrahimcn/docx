package com.docx.config;

import com.docx.controllers.DocxController;
import com.docx.generators.DocumentationGenerator;
import com.docx.processors.ControllerScanner;
import com.docx.properties.DocxProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AutoConfiguration
@ConditionalOnClass({DocxController.class})
@ConditionalOnProperty(prefix = "docx", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(DocxProperties.class)
@Import(DocxConfiguration.class)
public class DocxAutoConfiguration implements WebMvcConfigurer {

    private final DocxProperties properties;

    public DocxAutoConfiguration(DocxProperties properties) {
        this.properties = properties;
    }

    @Bean
    public DocxController docxController(DocumentationGenerator documentationGenerator, 
                                       ControllerScanner controllerScanner) {
        return new DocxController(documentationGenerator, controllerScanner, properties);
    }

    @Bean
    public ControllerScanner controllerScanner() {
        return new ControllerScanner(properties);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String basePath = properties.getBasePath();
        if (!basePath.endsWith("/")) {
            basePath += "/";
        }
        
        registry.addResourceHandler(basePath + "assets/**")
                .addResourceLocations("classpath:/docx-assets/");
    }
}