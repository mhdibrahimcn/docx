package com.docx.maven;

import com.docx.generators.DocumentationGenerator;
import com.docx.models.ApiDocumentation;
import com.docx.models.ControllerDoc;
import com.docx.processors.SpringAnnotationProcessor;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.PREPARE_PACKAGE, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class GenerateDocumentationMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(property = "docx.outputDirectory", defaultValue = "${project.build.directory}/docx")
    private File outputDirectory;

    @Parameter(property = "docx.autoScanControllers", defaultValue = "true")
    private boolean autoScanControllers;

    @Parameter(property = "docx.excludePackages")
    private List<String> excludePackages = new ArrayList<>();

    @Parameter(property = "docx.includePackages")
    private List<String> includePackages = new ArrayList<>();

    @Parameter(property = "docx.theme", defaultValue = "auto")
    private String theme;

    @Parameter(property = "docx.brandingColor", defaultValue = "#3B82F6")
    private String brandingColor;

    @Parameter(property = "docx.title", defaultValue = "API Documentation")
    private String title;

    @Parameter(property = "docx.version", defaultValue = "1.0.0")
    private String version;

    @Parameter(property = "docx.description", defaultValue = "Generated API Documentation")
    private String description;

    @Parameter(property = "docx.autoGenerateExamples", defaultValue = "true")
    private boolean autoGenerateExamples;

    @Parameter(property = "docx.manualResponseDocs", defaultValue = "true")
    private boolean manualResponseDocs;

    private DocumentationGenerator documentationGenerator;
    private SpringAnnotationProcessor annotationProcessor;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Generating Docx API documentation...");

        try {
            // Initialize components
            documentationGenerator = new DocumentationGenerator();
            annotationProcessor = new SpringAnnotationProcessor();

            // Create output directory
            if (!outputDirectory.exists()) {
                outputDirectory.mkdirs();
            }

            // Scan for controllers
            Set<Class<?>> controllerClasses = scanForControllers();
            getLog().info("Found " + controllerClasses.size() + " controller classes");

            // Generate documentation
            ApiDocumentation apiDoc = generateApiDocumentation(controllerClasses);

            // Generate HTML files
            Map<String, String> generatedFiles = documentationGenerator.generateAllDocumentation(apiDoc);

            // Write files to disk
            writeGeneratedFiles(generatedFiles);

            // Copy static assets
            copyStaticAssets();

            getLog().info("Documentation generated successfully in: " + outputDirectory.getAbsolutePath());

        } catch (Exception e) {
            throw new MojoExecutionException("Failed to generate documentation", e);
        }
    }

    private Set<Class<?>> scanForControllers() throws MojoExecutionException {
        if (!autoScanControllers) {
            getLog().info("Auto-scanning disabled, returning empty controller set");
            return new HashSet<>();
        }

        try {
            // Build classpath for scanning
            List<String> classpathElements = project.getCompileClasspathElements();
            classpathElements.add(project.getBuild().getOutputDirectory());

            URL[] urls = classpathElements.stream()
                    .map(element -> {
                        try {
                            return new File(element).toURI().toURL();
                        } catch (MalformedURLException e) {
                            getLog().warn("Invalid classpath element: " + element);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toArray(URL[]::new);

            URLClassLoader classLoader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());

            // Determine packages to scan
            Set<String> packagesToScan = determinePackagesToScan();
            getLog().info("Scanning packages: " + packagesToScan);

            Set<Class<?>> controllers = new HashSet<>();

            for (String packageName : packagesToScan) {
                try {
                    Reflections reflections = new Reflections(
                            new ConfigurationBuilder()
                                    .setUrls(urls)
                                    .forPackage(packageName, classLoader)
                                    .setScanners(Scanners.TypesAnnotated)
                    );

                    controllers.addAll(reflections.getTypesAnnotatedWith(RestController.class));
                    controllers.addAll(reflections.getTypesAnnotatedWith(Controller.class));

                } catch (Exception e) {
                    getLog().warn("Failed to scan package: " + packageName + " - " + e.getMessage());
                }
            }

            // Filter out excluded packages
            controllers = controllers.stream()
                    .filter(this::shouldIncludeController)
                    .collect(Collectors.toSet());

            return controllers;

        } catch (Exception e) {
            throw new MojoExecutionException("Failed to scan for controllers", e);
        }
    }

    private Set<String> determinePackagesToScan() {
        Set<String> packagesToScan = new HashSet<>();

        // If specific packages are included, use only those
        if (!includePackages.isEmpty()) {
            packagesToScan.addAll(includePackages);
        } else {
            // Try to determine base package from project structure
            String groupId = project.getGroupId();
            if (groupId != null) {
                packagesToScan.add(groupId);
            }

            // Add common base packages as fallback
            packagesToScan.addAll(Arrays.asList("com", "org", "io", "net"));
        }

        return packagesToScan;
    }

    private boolean shouldIncludeController(Class<?> controllerClass) {
        String packageName = controllerClass.getPackage().getName();

        // Check exclude patterns
        for (String excludePattern : excludePackages) {
            if (packageName.startsWith(excludePattern)) {
                return false;
            }
        }

        // Skip test packages by default
        if (packageName.contains(".test.") || packageName.endsWith(".test")) {
            return false;
        }

        return true;
    }

    private ApiDocumentation generateApiDocumentation(Set<Class<?>> controllerClasses) {
        ApiDocumentation apiDoc = new ApiDocumentation(title, version, description);

        List<ControllerDoc> controllers = controllerClasses.stream()
                .map(annotationProcessor::processController)
                .collect(Collectors.toList());

        apiDoc.setControllers(controllers);

        return apiDoc;
    }

    private void writeGeneratedFiles(Map<String, String> generatedFiles) throws IOException {
        for (Map.Entry<String, String> entry : generatedFiles.entrySet()) {
            String fileName = entry.getKey();
            String content = entry.getValue();

            Path filePath = Paths.get(outputDirectory.getAbsolutePath(), fileName);
            
            // Create parent directories if they don't exist
            Files.createDirectories(filePath.getParent());

            // Write content to file
            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                writer.write(content);
            }

            getLog().debug("Generated: " + fileName);
        }
    }

    private void copyStaticAssets() {
        // Create assets directory
        Path assetsDir = Paths.get(outputDirectory.getAbsolutePath(), "assets");
        try {
            Files.createDirectories(assetsDir);
            Files.createDirectories(assetsDir.resolve("css"));
            Files.createDirectories(assetsDir.resolve("js"));
            Files.createDirectories(assetsDir.resolve("images"));
        } catch (IOException e) {
            getLog().warn("Failed to create assets directories", e);
        }

        // In a real implementation, you would copy CSS, JS, and image files
        // from resources to the assets directory
        getLog().info("Static assets directory created: " + assetsDir);
    }
}