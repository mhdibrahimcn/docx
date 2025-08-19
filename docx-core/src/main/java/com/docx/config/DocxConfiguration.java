package com.docx.config;

import com.docx.generators.DocumentationGenerator;
import com.docx.parsers.JavaDocParser;
import com.docx.processors.SpringAnnotationProcessor;
import com.docx.processors.ValidationConstraintParser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.docx")
public class DocxConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public JavaDocParser javaDocParser() {
        return new JavaDocParser();
    }

    @Bean
    @ConditionalOnMissingBean
    public ValidationConstraintParser validationConstraintParser() {
        return new ValidationConstraintParser();
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringAnnotationProcessor springAnnotationProcessor() {
        return new SpringAnnotationProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public DocumentationGenerator documentationGenerator() {
        return new DocumentationGenerator();
    }
}