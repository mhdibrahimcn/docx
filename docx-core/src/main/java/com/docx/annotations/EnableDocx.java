package com.docx.annotations;

import com.docx.config.DocxConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(DocxConfiguration.class)
public @interface EnableDocx {
    
    /**
     * Base packages to scan for controllers.
     * If empty, will scan all packages starting from the application's main class package.
     */
    String[] basePackages() default {};
    
    /**
     * Base package classes to scan for controllers.
     * Alternative to basePackages() using class references for type safety.
     */
    Class<?>[] basePackageClasses() default {};
    
    /**
     * Whether to enable automatic controller scanning.
     */
    boolean autoScanControllers() default true;
    
    /**
     * Whether to include validation constraints in documentation.
     */
    boolean includeValidation() default true;
    
    /**
     * Whether to generate examples automatically using AOP.
     */
    boolean autoGenerateExamples() default true;
}