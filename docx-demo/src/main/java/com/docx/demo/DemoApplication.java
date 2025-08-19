package com.docx.demo;

import com.docx.annotations.EnableDocx;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Demo application for Docx API documentation generator.
 * 
 * This application showcases the capabilities of Docx, including:
 * - Automatic controller scanning
 * - JavaDoc parsing with custom tags
 * - Validation constraint documentation
 * - AOP-based example generation
 * - Beautiful Laravel Scramble-inspired UI
 * 
 * @author Docx Team
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootApplication
@EnableDocx(
    autoScanControllers = true,
    includeValidation = true,
    autoGenerateExamples = true
)
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}