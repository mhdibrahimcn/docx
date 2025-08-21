package com.docx.generators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HtmlTemplateEngine {

    private static final Logger logger = LoggerFactory.getLogger(HtmlTemplateEngine.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateIndexPage(Map<String, Object> context) {
        String title = (String) context.get("title");
        String version = (String) context.get("version");
        String description = (String) context.get("description");
        
        logger.debug("Generating index page with context keys: {}", context.keySet());
        logger.debug("Controllers in context: {}", context.get("controllers"));
        
        return buildModernHtmlPage(context);
    }

    public String generateControllerPage(Map<String, Object> context) {
        return buildModernHtmlPage(context);
    }

    public String generateEndpointPage(Map<String, Object> context) {
        return buildModernHtmlPage(context);
    }

    private String buildModernHtmlPage(Map<String, Object> context) {
        String title = (String) context.getOrDefault("title", "API Documentation");
        String jsonData = convertContextToJson(context);
        
        return String.format("""
            <!DOCTYPE html>
            <html lang="en" class="scroll-smooth">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>%s</title>
                <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
                <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;500;600&display=swap" rel="stylesheet">
                <style>
                    %s
                </style>
            </head>
            <body>
                <div class="main-container">
                    <!-- Sidebar -->
                    <aside id="sidebar">
                        <div class="sidebar-header">
                            <a href="#" class="logo" onclick="window.location.hash = ''">
                                <div class="logo-icon">D</div>
                                <span id="api-title-sidebar">API Docs</span>
                            </a>
                        </div>
                        <div class="search-container">
                            <div style="position: relative;">
                                <input type="search" class="search-input" placeholder="Search..." id="search-input">
                                <span class="search-shortcut">⌘K</span>
                            </div>
                        </div>
                        <nav id="sidebar-nav" class="nav-menu"></nav>
                    </aside>

                    <div id="main-content-wrapper">
                        <!-- Main Content -->
                        <main id="main-content">
                            <div id="api-content">
                                <!-- API documentation content will be inserted here -->
                            </div>
                        </main>

                        <!-- Utility Panel -->
                        <aside id="utility-panel">
                            <div id="utility-panel-content" class="panel-content">
                                <!-- This will be populated dynamically -->
                            </div>
                        </aside>
                    </div>
                </div>

                <script>
                    const jsonData = %s;
                    %s
                </script>
            </body>
            </html>
            """, title, getModernCssStyles(), jsonData, getModernJavaScript());
    }

    @SuppressWarnings("unchecked")
    private String convertContextToJson(Map<String, Object> context) {
        try {
            // Create the JSON structure that matches the template expectations
            Map<String, Object> jsonData = Map.of(
                "title", context.getOrDefault("title", "API Documentation"),
                "version", context.getOrDefault("version", "1.0.0"),
                "description", context.getOrDefault("description", "Generated API Documentation"),
                "controllers", context.getOrDefault("controllers", List.of())
            );
            
            return objectMapper.writeValueAsString(jsonData);
        } catch (Exception e) {
            logger.error("Error converting context to JSON", e);
            return "{}";
        }
    }

    private String buildHtmlPage(String title, String content, String brandingColor) {
        return String.format("""
            <!DOCTYPE html>
            <html lang="en" class="scroll-smooth">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>%s</title>
                <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
                <style>
                    %s
                </style>
            </head>
            <body>
                <div class="main-container">
                    <!-- Sidebar -->
                    <aside id="sidebar">
                        <h1 id="api-title-sidebar">%s</h1>
                        <nav id="sidebar-nav"></nav>
                    </aside>

                    <div id="main-content-wrapper">
                        <!-- Main Content -->
                        <main id="main-content">
                            <div id="api-content">
                                %s
                            </div>
                        </main>

                        <!-- Utility Panel -->
                        <aside id="utility-panel">
                            <div id="utility-panel-content" class="panel-content">
                                <p style="padding: 1rem;">Select an endpoint from the sidebar to test it.</p>
                            </div>
                        </aside>
                    </div>
                </div>
                <script>
                    %s
                </script>
            </body>
            </html>
            """, title, getCssStyles(brandingColor), title, content, getJavaScript());
    }

    @SuppressWarnings("unchecked")
    private String generateIndexContent(Map<String, Object> context) {
        String title = (String) context.get("title");
        String version = (String) context.get("version");
        String description = (String) context.get("description");
        
        logger.debug("generateIndexContent called with title: {}, version: {}", title, version);
        
        // Get controllers data
        Object controllersObj = context.get("controllers");
        java.util.List<Map<String, Object>> controllers = null;
        int controllerCount = 0;
        int endpointCount = 0;
        
        try {
            if (controllersObj instanceof java.util.List) {
                controllers = (java.util.List<Map<String, Object>>) controllersObj;
                controllerCount = controllers.size();
                
                // Count total endpoints
                for (Object controllerObj : controllers) {
                    if (controllerObj instanceof Map) {
                        Map<String, Object> controller = (Map<String, Object>) controllerObj;
                        Object endpointsObj = controller.get("endpoints");
                        if (endpointsObj instanceof java.util.List) {
                            endpointCount += ((java.util.List<?>) endpointsObj).size();
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error processing controllers data", e);
            controllerCount = 0;
            endpointCount = 0;
        }
        
        logger.info("Final controller count: {}, endpoint count: {}", controllerCount, endpointCount);
        
        StringBuilder controllersHtml = new StringBuilder();
        if (controllers != null && !controllers.isEmpty()) {
            controllersHtml.append("""
                <div class="controllers-section">
                    <div class="section-header">
                        <h2 class="section-title">API Controllers</h2>
                        <p class="section-subtitle">Explore the available API endpoints organized by controller</p>
                    </div>
                    <div class="controllers-grid">
                """);
            
            for (Map<String, Object> controller : controllers) {
                String controllerName = (String) controller.get("name");
                String controllerDesc = (String) controller.get("description");
                String baseUrl = (String) controller.get("baseUrl");
                java.util.List<?> endpoints = (java.util.List<?>) controller.get("endpoints");
                int endpointsCount = endpoints != null ? endpoints.size() : 0;
                
                // Skip if controller name is null
                if (controllerName == null) {
                    continue;
                }
                
                controllersHtml.append(String.format("""
                    <div class="controller-card" onclick="location.href='controllers/%s.html'">
                        <div class="controller-header">
                            <div class="controller-icon">
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <rect x="3" y="3" width="18" height="18" rx="2"/>
                                    <path d="M9 9h6v6H9z"/>
                                </svg>
                            </div>
                            <div class="controller-info">
                                <h3 class="controller-name">%s</h3>
                                <span class="controller-path font-mono">%s</span>
                            </div>
                            <div class="controller-badge">
                                <span class="endpoint-count">%d</span>
                            </div>
                        </div>
                        <p class="controller-description">%s</p>
                        <div class="controller-footer">
                            <span class="controller-tag">REST API</span>
                            <span class="controller-arrow">→</span>
                        </div>
                    </div>
                    """, 
                    controllerName.toLowerCase(),
                    controllerName,
                    baseUrl != null ? baseUrl : "/" + controllerName.toLowerCase(),
                    endpointsCount,
                    controllerDesc != null ? controllerDesc : "REST API controller for " + controllerName.toLowerCase() + " operations"
                ));
            }
            
            controllersHtml.append("""
                    </div>
                </div>
                """);
        }
        
        return String.format("""
            <div class="intro-section" id="intro-section">
                <h1>%s</h1>
                <p class="description">%s</p>
                <p class="version">Version %s</p>
            </div>
            
            <!-- Endpoint cards will be dynamically populated by JavaScript -->
            """, title, description, version);
    }

    private String generateControllerContent(Map<String, Object> context) {
        return """
            <div class="content-header">
                <h1 class="page-title">Controller Documentation</h1>
            </div>
            <div class="controller-details">
                <p>Controller details would be rendered here</p>
            </div>
            """;
    }

    private String generateEndpointContent(Map<String, Object> context) {
        return """
            <div class="content-header">
                <h1 class="page-title">Endpoint Documentation</h1>
            </div>
            <div class="endpoint-details">
                <p>Endpoint details would be rendered here</p>
            </div>
            """;
    }

    private String generateSidebar() {
        return """
            <div class="sidebar-header">
                <a href="/" class="logo">
                    <div class="logo-icon">D</div>
                    Docx
                </a>
            </div>
            <div class="search-container">
                <div style="position: relative;">
                    <input type="search" class="search-input" placeholder="Search documentation..." id="search-input">
                    <span class="search-shortcut">⌘K</span>
                </div>
            </div>
            <nav class="nav-menu">
                <div class="nav-section">
                    <h3 class="nav-section-title">Getting Started</h3>
                    <ul class="nav-list">
                        <li class="nav-item"><a href="#overview" class="nav-link active">Overview</a></li>
                        <li class="nav-item"><a href="#installation" class="nav-link">Installation</a></li>
                        <li class="nav-item"><a href="#quick-start" class="nav-link">Quick Start</a></li>
                    </ul>
                </div>
                <div class="nav-section">
                    <h3 class="nav-section-title">API Documentation</h3>
                    <ul class="nav-list">
                        <li class="nav-item"><a href="#endpoints" class="nav-link">Endpoints</a></li>
                        <li class="nav-item"><a href="#authentication" class="nav-link">Authentication</a></li>
                        <li class="nav-item"><a href="#responses" class="nav-link">Responses</a></li>
                    </ul>
                </div>
                <div class="nav-section">
                    <h3 class="nav-section-title">Controllers</h3>
                    <ul class="nav-list" id="controllers-nav">
                        <!-- Controllers will be dynamically added here -->
                    </ul>
                </div>
                <div class="nav-section">
                    <h3 class="nav-section-title">Models</h3>
                    <ul class="nav-list" id="models-nav">
                        <!-- Models will be dynamically added here -->
                    </ul>
                </div>
            </nav>
            """;
    }

    private String getModernCssStyles() {
        return """
            /* General */
            :root {
                --slate-50: #f8fafc;
                --slate-100: #f1f5f9;
                --slate-300: #cbd5e1;
                --slate-400: #94a3b8;
                --slate-500: #64748b;
                --slate-600: #475569;
                --slate-700: #334155;
                --slate-800: #1e293b;
                --slate-900: #0f172a;
                --indigo-600: #4f46e5;
                --white: #ffffff;
                --purple-600: #9333ea;
                --red-600: #dc2626;
                --primary-color: #FFD700;
            }

            html { scroll-behavior: smooth; }
            body {
                font-family: 'Inter', sans-serif;
                background-color: var(--slate-100);
                color: var(--slate-800);
                margin: 0;
                line-height: 1.5;
                -webkit-font-smoothing: antialiased;
                -moz-osx-font-smoothing: grayscale;
            }
            * {
                box-sizing: border-box;
            }
            .font-mono {
                font-family: 'JetBrains Mono', monospace;
            }

            /* Layout */
            .main-container {
                display: flex;
                min-height: 100vh;
            }
            #sidebar {
                width: 18rem;
                background-color: var(--slate-900);
                color: var(--slate-300);
                position: fixed;
                height: 100%;
                overflow-y: auto;
                flex-shrink: 0;
                border-right: 1px solid var(--slate-700);
                display: flex;
                flex-direction: column;
            }
            #main-content-wrapper {
                display: flex;
                width: 100%;
                margin-left: 18rem;
            }
            #main-content {
                flex-grow: 1;
                padding: 2rem;
                max-width: 50rem;
            }
            #api-content {
                max-width: 100%;
            }
            #utility-panel {
                width: 28rem;
                background-color: var(--slate-900);
                color: var(--slate-300);
                position: fixed;
                right: 0;
                top: 0;
                height: 100vh;
                overflow-y: auto;
                flex-shrink: 0;
            }

            /* Sidebar */
            .sidebar-header {
                padding: 1.5rem;
                border-bottom: 1px solid var(--slate-700);
            }
            .logo {
                font-size: 1.25rem;
                font-weight: 700;
                color: var(--white);
                text-decoration: none;
                display: flex;
                align-items: center;
                gap: 0.5rem;
            }
            .logo-icon {
                width: 28px;
                height: 28px;
                background: var(--primary-color);
                border-radius: 6px;
                display: flex;
                align-items: center;
                justify-content: center;
                font-weight: 600;
                color: #000;
                font-size: 14px;
            }
            .search-container {
                padding: 1rem 1.5rem;
                border-bottom: 1px solid var(--slate-700);
            }
            .search-input {
                width: 100%;
                padding: 0.75rem 1rem;
                border: 1px solid var(--slate-700);
                border-radius: 8px;
                background-color: var(--slate-800);
                color: var(--white);
                font-size: 0.875rem;
            }
            .search-shortcut {
                position: absolute;
                right: 2.25rem;
                top: 5.75rem;
                font-size: 0.75rem;
                color: var(--slate-400);
                font-family: var(--font-mono);
                pointer-events: none;
            }
            .nav-menu {
                flex-grow: 1;
                overflow-y: auto;
            }
            .nav-section {
                padding: 0;
            }
            .nav-section-title {
                font-size: 0.75rem;
                font-weight: 600;
                color: var(--slate-400);
                margin: 1.5rem 1.5rem 0.75rem 1.5rem;
                text-transform: uppercase;
                letter-spacing: 0.05em;
            }
            .nav-list {
                list-style: none;
                margin: 0;
                padding: 0;
            }
            .sidebar-link {
                display: block;
                padding: 0.625rem 1.5rem;
                color: var(--slate-300);
                text-decoration: none;
                font-size: 0.875rem;
                font-weight: 500;
                border-left: 3px solid transparent;
                transition: all 0.2s ease;
            }
            .sidebar-link:hover {
                color: var(--white);
                background-color: var(--slate-800);
            }
            .sidebar-link.active {
                color: var(--white);
                background-color: var(--slate-700);
                border-left-color: var(--primary-color);
                font-weight: 600;
            }

            /* Main Content */
            .intro-section { margin-bottom: 3rem; }
            .intro-section h1 { font-size: 2.25rem; font-weight: 800; color: var(--slate-900); }
            .intro-section .description { margin-top: 0.5rem; font-size: 1.125rem; color: var(--slate-600); }
            .intro-section .version { margin-top: 0.25rem; font-size: 0.875rem; color: var(--slate-500); }

            .endpoint-card {
                display: none;
                background-color: var(--white);
                border: 1px solid #e2e8f0;
                border-radius: 0.75rem;
                box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
                margin-bottom: 2rem;
                overflow: hidden;
            }
            .endpoint-card.active { display: block; }
            .endpoint-card-header {
                background-color: var(--slate-50);
                border-bottom: 1px solid #e2e8f0;
                padding: 1rem 1.5rem;
            }
            .endpoint-card-header h3 { font-size: 1.25rem; font-weight: 700; color: var(--slate-900); }
            .endpoint-url-container { display: flex; align-items: center; margin-top: 0.5rem; }
            .http-method {
                font-family: 'JetBrains Mono', monospace;
                font-size: 0.75rem;
                font-weight: 600;
                padding: 0.25rem 0.5rem;
                border-radius: 0.375rem;
            }
            .endpoint-url { margin-left: 0.75rem; font-family: 'JetBrains Mono', monospace; color: var(--slate-600); font-size: 0.875rem; word-break: break-all; }
            .endpoint-card-body { padding: 1.5rem; }
            .endpoint-card-body .description { color: var(--slate-600); margin-top: 0.25rem; }
            .details-container { margin-top: 1.5rem; }
            .details-container > div + div { margin-top: 1.5rem; }
            .details-container h4 {
                font-size: 0.875rem;
                font-weight: 600;
                color: var(--slate-900);
                text-transform: uppercase;
                letter-spacing: 0.05em;
                margin-bottom: 0.5rem;
            }
            .code-block { background-color: var(--slate-800); color: var(--white); padding: 1rem; border-radius: 0.5rem; }

            /* Tables */
            .table-container { overflow-x: auto; }
            table { width: 100%; text-align: left; margin-top: 0.5rem; border-collapse: collapse; }
            table thead { background-color: var(--slate-50); }
            table thead tr { border-bottom: 2px solid #e2e8f0; }
            table th { padding: 0.5rem; font-weight: 600; font-size: 0.75rem; color: var(--slate-600); }
            table tbody tr { border-bottom: 1px solid #e2e8f0; }
            table tbody tr:last-child { border-bottom: 0; }
            table td { padding: 0.75rem; font-size: 0.875rem; color: var(--slate-600); vertical-align: top; }
            table .font-mono { font-family: 'JetBrains Mono', monospace; }
            table .text-purple { color: var(--purple-600); }
            table .text-red { color: var(--red-600); font-weight: 600; }

            /* Utility Panel */
            #utility-panel .panel-content { padding: 1.5rem; }
            .utility-section {
                background-color: var(--slate-800);
                border: 1px solid var(--slate-700);
                border-radius: 0.5rem;
                margin-bottom: 1.5rem;
            }
            .utility-section-header {
                padding: 0.75rem 1rem;
                font-weight: 600;
                border-bottom: 1px solid var(--slate-700);
                cursor: pointer;
            }
            .utility-section-content {
                padding: 1rem;
            }
            .form-group { margin-bottom: 1rem; }
            .form-group label { display: block; font-weight: 500; margin-bottom: 0.25rem; font-size: 0.875rem; color: var(--slate-300);}
            .form-group input, .form-group textarea {
                width: 100%;
                padding: 0.5rem;
                border: 1px solid var(--slate-600);
                border-radius: 0.25rem;
                background-color: var(--slate-900);
                color: var(--slate-300);
            }
            .send-request-btn {
                background-color: var(--indigo-600);
                color: var(--white);
                padding: 0.75rem 1rem;
                border: none;
                border-radius: 0.25rem;
                cursor: pointer;
                width: 100%;
                font-weight: 600;
                font-size: 1rem;
            }
            .language-tabs { display: flex; margin-bottom: 1rem; border-bottom: 1px solid var(--slate-700); }
            .lang-tab {
                padding: 0.5rem 1rem;
                border: none;
                background: none;
                color: var(--slate-400);
                cursor: pointer;
            }
            .lang-tab.active { color: var(--white); border-bottom: 2px solid var(--indigo-600); }
            #snippet-container, #tester-response-container {
                background-color: var(--slate-900);
                border-radius: 0.375rem;
                min-height: 150px;
                display: flex;
                align-items: center;
                justify-content: center;
                padding: 1rem;
                border: 1px solid var(--slate-700);
            }
            #snippet-container pre, #tester-response-container pre { margin: 0; width: 100%; white-space: pre-wrap; font-size: 0.875rem; }
            .loader {
                border: 4px solid var(--slate-700);
                border-radius: 50%;
                border-top: 4px solid var(--indigo-600);
                width: 30px;
                height: 30px;
                animation: spin 1s linear infinite;
            }
            @keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
            .status-success { color: #22c55e; }
            .status-error { color: #ef4444; }

            /* Controller Cards */
            .controllers-section { margin-top: 3rem; }
            .section-header { margin-bottom: 2rem; }
            .section-title { font-size: 1.875rem; font-weight: 700; margin-bottom: 0.5rem; color: var(--slate-900); }
            .section-subtitle { font-size: 1rem; color: var(--slate-600); margin: 0; }
            .controllers-grid { display: grid; grid-template-columns: 1fr; gap: 1.5rem; }
            .controller-card {
                background: var(--white);
                border: 1px solid #e2e8f0;
                border-radius: 12px;
                padding: 1.5rem;
                cursor: pointer;
                transition: all 0.2s ease;
            }
            .controller-card:hover { border-color: var(--primary-color); box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1); transform: translateY(-2px); }
            .controller-header { display: flex; align-items: flex-start; gap: 1rem; margin-bottom: 1rem; }
            .controller-icon { width: 40px; height: 40px; background: var(--slate-100); border-radius: 8px; display: flex; align-items: center; justify-content: center; color: var(--slate-500); flex-shrink: 0; }
            .controller-info { flex: 1; min-width: 0; }
            .controller-name { font-size: 1.125rem; font-weight: 600; color: var(--slate-900); margin: 0 0 0.25rem 0; }
            .controller-badge { background: var(--primary-color); color: #000; padding: 0.25rem 0.5rem; border-radius: 6px; font-size: 0.75rem; font-weight: 600; flex-shrink: 0; }
            .controller-description { color: var(--slate-600); font-size: 0.875rem; margin-bottom: 1rem; }
            .controller-footer { display: flex; justify-content: space-between; align-items: center; }
            .controller-tag { font-size: 0.75rem; color: var(--slate-500); background: var(--slate-100); padding: 0.25rem 0.5rem; border-radius: 4px; font-weight: 500; }
            .controller-arrow { color: var(--slate-400); font-weight: 500; opacity: 0; transition: opacity 0.2s ease; }
            .controller-card:hover .controller-arrow { opacity: 1; }

            /* Method Colors */
            .method-get { background-color: rgba(59, 130, 246, 0.1); color: #3B82F6; }
            .method-post { background-color: rgba(16, 185, 129, 0.1); color: #10B981; }
            .method-put { background-color: rgba(245, 158, 11, 0.1); color: #F59E0B; }
            .method-patch { background-color: rgba(239, 68, 68, 0.1); color: #EF4444; }
            .method-delete { background-color: rgba(139, 92, 246, 0.1); color: #8B5CF6; }

            /* Responsive */
            @media (max-width: 1280px) {
                #main-content-wrapper { flex-direction: column; margin-left: 18rem; }
                #utility-panel { position: static; width: 100%; height: auto; }
            }
            @media (max-width: 1024px) {
                #sidebar { position: static; width: 100%; height: auto; }
                #main-content-wrapper { margin-left: 0; }
    private String getModernJavaScript() {
        return """
            let currentEndpointId = null;

            document.addEventListener('DOMContentLoaded', () => {
                const sidebarNav = document.getElementById('sidebar-nav');
                const apiContent = document.getElementById('api-content');
                document.getElementById('api-title-sidebar').textContent = jsonData.title;

                const introSection = document.createElement('div');
                introSection.id = "intro-section";
                introSection.className = 'intro-section';
                apiContent.appendChild(introSection);

                jsonData.controllers.forEach(controller => {
                    const controllerSection = document.createElement('div');
                    controllerSection.className = 'nav-section';
                    
                    const controllerTitle = document.createElement('h3');
                    controllerTitle.className = 'nav-section-title';
                    controllerTitle.textContent = controller.name.replace('Controller', '');
                    controllerSection.appendChild(controllerTitle);

                    const endpointList = document.createElement('ul');
                    endpointList.className = 'nav-list';

                    controller.endpoints.forEach((endpoint, index) => {
                        const endpointId = `${controller.name}-${endpoint.name}-${index}`;
                        
                        const listItem = document.createElement('li');
                        const link = document.createElement('a');
                        link.href = `#${endpointId}`;
                        link.textContent = endpoint.name;
                        link.className = 'sidebar-link';
                        listItem.appendChild(link);
                        endpointList.appendChild(listItem);
                        
                        const methodColor = getMethodClass(endpoint.httpMethod);

                        const endpointCard = document.createElement('div');
                        endpointCard.id = `endpoint-${endpointId}`;
                        endpointCard.className = 'endpoint-card';
                        
                        let parametersHtml = '';
                        if (endpoint.pathVariables) parametersHtml += `<div><h4>Path Parameters</h4>${createParametersTable(endpoint.pathVariables)}</div>`;
                        if (endpoint.queryParameters) parametersHtml += `<div><h4>Query Parameters</h4>${createParametersTable(endpoint.queryParameters)}</div>`;
                        if (endpoint.requestBody) parametersHtml += `<div><h4>Request Body</h4><div class="code-block"><pre><code>${endpoint.requestBody.example || ''}</code></pre></div></div>`;
                        let responsesHtml = `<div><h4>Responses</h4>${createResponsesTable(endpoint.responses)}</div>`;

                        endpointCard.innerHTML = `
                            <div class="endpoint-card-header">
                                <h3>${endpoint.name}</h3>
                                <div class="endpoint-url-container">
                                    <span class="http-method ${methodColor}">${endpoint.httpMethod}</span>
                                    <span class="endpoint-url">${endpoint.url}</span>
                                </div>
                            </div>
                            <div class="endpoint-card-body">
                                <p class="description">${endpoint.description || ''}</p>
                                <div class="details-container">
                                   ${parametersHtml}
                                   ${responsesHtml}
                                </div>
                            </div>`;
                        apiContent.appendChild(endpointCard);
                    });
                    controllerSection.appendChild(endpointList);
                    sidebarNav.appendChild(controllerSection);
                });

                setupNavigation();
                initSearch();
            });

            function setupNavigation() {
                window.addEventListener('hashchange', renderPage);
                renderPage(); // Initial render
            }

            function renderPage() {
                const hash = window.location.hash.substring(1);
                currentEndpointId = hash;

                document.querySelectorAll('.endpoint-card').forEach(c => c.classList.remove('active'));
                const introSection = document.getElementById('intro-section');
                
                if (hash) {
                    introSection.style.display = 'none';
                    const activeCard = document.getElementById(`endpoint-${hash}`);
                    if (activeCard) {
                        activeCard.classList.add('active');
                    }
                } else {
                    introSection.style.display = 'block';
                    renderIntroContent();
                }

                document.querySelectorAll('.sidebar-link').forEach(link => {
                    link.classList.toggle('active', link.getAttribute('href') === `#${hash}`);
                });
                
                populateUtilityPanel(currentEndpointId);
            }

            function renderIntroContent() {
                const introSection = document.getElementById('intro-section');
                let controllerCardsHtml = (jsonData.controllers || []).map(controller => {
                     const endpointsCount = controller.endpoints ? controller.endpoints.length : 0;
                     // Create a fake link to the first endpoint of the controller for the card click
                     const firstEndpointId = endpointsCount > 0 ? `${controller.name}-${controller.endpoints[0].name}-0` : '';

                     return `
                        <div class="controller-card" onclick="window.location.hash = '#${firstEndpointId}'">
                            <div class="controller-header">
                                <div class="controller-icon">
                                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2"/><path d="M9 9h6v6H9z"/></svg>
                                </div>
                                <div class="controller-info">
                                    <h3 class="controller-name">${controller.name.replace('Controller', '')}</h3>
                                    <span class="controller-path font-mono">${controller.baseUrl || ''}</span>
                                </div>
                                <div class="controller-badge">
                                    <span class="endpoint-count">${endpointsCount} endpoints</span>
                                </div>
                            </div>
                            <p class="controller-description">${controller.description || ''}</p>
                            <div class="controller-footer">
                                <span class="controller-tag">REST API</span>
                                <span class="controller-arrow">→</span>
                            </div>
                        </div>
                     `;
                }).join('');

                introSection.innerHTML = `
                    <h1>${jsonData.title}</h1>
                    <p class="description">${jsonData.description}</p>
                    <p class="version">Version ${jsonData.version}</p>
                    <div class="controllers-section">
                        <div class="section-header">
                            <h2 class="section-title">API Controllers</h2>
                            <p class="section-subtitle">Explore the available API endpoints organized by controller</p>
                        </div>
                        <div class="controllers-grid">${controllerCardsHtml}</div>
                    </div>
                `;
            }
            
            function populateUtilityPanel(endpointId) {
                const panel = document.getElementById('utility-panel-content');
                const endpoint = getEndpointById(endpointId);

                if (!endpoint) {
                    panel.innerHTML = '<p style="padding: 1rem;">Select an endpoint from the sidebar.</p>';
                    return;
                }

                let pathParamsInputs = (endpoint.pathVariables || []).map(p => `
                    <div class="form-group">
                        <label for="tester-${p.name}-${endpointId}">${p.name}</label>
                        <input type="text" id="tester-${p.name}-${endpointId}" placeholder="${p.description}">
                    </div>`).join('');

                let queryParamsInputs = (endpoint.queryParameters || []).map(p => `
                     <div class="form-group">
                        <label for="tester-${p.name}-${endpointId}">${p.name}</label>
                        <input type="text" id="tester-${p.name}-${endpointId}" placeholder="${p.description}">
                    </div>`).join('');

                let bodyInput = '';
                if (endpoint.requestBody) {
                    bodyInput = `
                        <div class="utility-section">
                            <div class="utility-section-header">Body</div>
                            <div class="utility-section-content">
                                <div class="form-group">
                                    <textarea id="tester-body-${endpointId}" rows="5">${endpoint.requestBody.example || ''}</textarea>
                                </div>
                            </div>
                        </div>`;
                }

                panel.innerHTML = `
                    <div class="utility-section">
                        <div class="utility-section-header">Authentication</div>
                        <div class="utility-section-content">
                            <div class="form-group">
                                <label for="auth-token">Bearer Token</label>
                                <input type="text" id="auth-token" placeholder="Enter your API token">
                            </div>
                        </div>
                    </div>

                    <div class="utility-section">
                        <div class="utility-section-header">Parameters</div>
                        <div class="utility-section-content">
                            ${pathParamsInputs}
                            ${queryParamsInputs}
                        </div>
                    </div>

                    ${bodyInput}

                    <button class="send-request-btn" onclick="sendApiRequest('${endpointId}')">Send API Request</button>

                    <div class="utility-section" style="margin-top: 1.5rem;">
                        <div class="utility-section-header">Request Sample</div>
                        <div class="utility-section-content">
                            <div class="language-tabs">
                                <button class="lang-tab active" data-lang="cURL">cURL</button>
                                <button class="lang-tab" data-lang="JavaScript">JavaScript</button>
                                <button class="lang-tab" data-lang="Python">Python</button>
                            </div>
                            <div id="snippet-container"></div>
                        </div>
                    </div>

                    <div class="utility-section">
                        <div class="utility-section-header">Response</div>
                        <div id="tester-response-container" class="utility-section-content">
                            <pre><code>Click "Send API Request" to see the response.</code></pre>
                        </div>
                    </div>
                `;
                
                setupLanguageTabsForPanel();
                generateSnippet(endpointId, 'cURL'); // Default snippet
            }
            
            function initSearch() {
                const searchInput = document.getElementById('search-input');
                if (!searchInput) return;

                document.addEventListener('keydown', function(e) {
                    if ((e.metaKey || e.ctrlKey) && e.key === 'k') {
                        e.preventDefault();
                        searchInput.focus();
                    }
                });

                searchInput.addEventListener('input', function(e) {
                    const query = e.target.value.toLowerCase().trim();
                    const navSections = document.querySelectorAll('.nav-section');

                    navSections.forEach(section => {
                        const navItems = section.querySelectorAll('.sidebar-link');
                        let hasVisibleItems = false;
                        
                        navItems.forEach(link => {
                            const text = link.textContent.toLowerCase();
                            if (text.includes(query)) {
                                link.parentElement.style.display = 'block';
                                hasVisibleItems = true;
                            } else {
                                link.parentElement.style.display = 'none';
                            }
                        });
                        
                        section.style.display = hasVisibleItems ? 'block' : 'none';
                    });
                });
            }

            function setupLanguageTabsForPanel() {
                const tabs = document.querySelectorAll('#utility-panel .lang-tab');
                tabs.forEach(tab => {
                    tab.addEventListener('click', () => {
                        tabs.forEach(t => t.classList.remove('active'));
                        tab.classList.add('active');
                        if (currentEndpointId) {
                            generateSnippet(currentEndpointId, tab.dataset.lang);
                        }
                    });
                });
            }
            
            function getMethodClass(method) {
                return `method-${method.toLowerCase()}`;
            }

            function createParametersTable(params) {
                let tableRows = params.map(p => `
                    <tr>
                        <td class="font-mono">${p.name}</td>
                        <td class="font-mono text-purple">${p.type}</td>
                        <td>${p.required ? '<span class="text-red">Yes</span>' : 'No'}</td>
                        <td>${p.description || ''}</td>
                    </tr>`).join('');
                return `<div class="table-container"><table><thead><tr><th>Name</th><th>Type</th><th>Required</th><th>Description</th></tr></thead><tbody>${tableRows}</tbody></table></div>`;
            }

            function createResponsesTable(responses) {
                 let tableRows = responses.map(r => `
                    <tr>
                        <td class="font-mono">${r.statusCode}</td>
                        <td>${r.description || ''}</td>
                    </tr>`).join('');
                 return `<div class="table-container"><table><thead><tr><th>Status Code</th><th>Description</th></tr></thead><tbody>${tableRows}</tbody></table></div>`;
            }
            
            async function sendApiRequest(endpointId) {
                const responseContainer = document.getElementById('tester-response-container');
                responseContainer.innerHTML = '<div class="loader"></div>';

                await new Promise(res => setTimeout(res, 1000));

                const endpoint = getEndpointById(endpointId);
                const isSuccess = Math.random() > 0.2;
                const statusCode = isSuccess ? (endpoint.httpMethod === 'POST' ? 201 : 200) : 400;
                const statusClass = isSuccess ? 'status-success' : 'status-error';
                const mockResponse = {
                    status: statusCode,
                    data: isSuccess ? { message: "Request successful!", data: { id: 123, name: "Test Item" } } : { error: "Invalid input provided." }
                };

                responseContainer.innerHTML = `
                    <p><strong>Status:</strong> <span class="${statusClass}">${mockResponse.status}</span></p>
                    <pre><code>${JSON.stringify(mockResponse.data, null, 2)}</code></pre>
                `;
            }

            async function generateSnippet(endpointId, language) {
                const snippetContainer = document.getElementById('snippet-container');
                if (!snippetContainer) return;
                snippetContainer.innerHTML = `<div class="loader"></div>`;

                const endpoint = getEndpointById(endpointId);
                if (!endpoint) {
                    snippetContainer.innerHTML = `<pre><code>Error: Endpoint data not found.</code></pre>`;
                    return;
                }

                // Generate simple code snippets for different languages
                let code = '';
                switch(language) {
                    case 'cURL':
                        code = `curl -X ${endpoint.httpMethod} "${endpoint.url}" \\
  -H "Content-Type: application/json" \\
  -H "Authorization: Bearer YOUR_TOKEN"`;
                        if (endpoint.requestBody) {
                            code += ` \\
  -d '${endpoint.requestBody.example || '{}'}'`;
                        }
                        break;
                    case 'JavaScript':
                        code = `fetch('${endpoint.url}', {
  method: '${endpoint.httpMethod}',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer YOUR_TOKEN'
  }`;
                        if (endpoint.requestBody) {
                            code += `,
  body: JSON.stringify(${endpoint.requestBody.example || '{}'})`;
                        }
                        code += `
})
.then(response => response.json())
.then(data => console.log(data));`;
                        break;
                    case 'Python':
                        code = `import requests

url = "${endpoint.url}"
headers = {
    "Content-Type": "application/json",
    "Authorization": "Bearer YOUR_TOKEN"
}`;
                        if (endpoint.requestBody) {
                            code += `
data = ${endpoint.requestBody.example || '{}'}

response = requests.${endpoint.httpMethod.toLowerCase()}(url, headers=headers, json=data)`;
                        } else {
                            code += `

response = requests.${endpoint.httpMethod.toLowerCase()}(url, headers=headers)`;
                        }
                        code += `
print(response.json())`;
                        break;
                }
                
                snippetContainer.innerHTML = `<pre><code>${code.replace(/</g, "&lt;").replace(/>/g, "&gt;")}</code></pre>`;
            }

            function getEndpointById(endpointId) {
                if (!endpointId) return null;
                const [controllerName, endpointName, index] = endpointId.split('-');
                const controller = jsonData.controllers.find(c => c.name === controllerName);
                return controller ? controller.endpoints[parseInt(index, 10)] : null;
            }
            """;
    }
}
                color: var(--slate-300);
                position: fixed;
                right: 0;
                top: 0;
                height: 100vh;
                overflow-y: auto;
                flex-shrink: 0;
            }

            /* Sidebar */
            #api-title-sidebar {
                font-size: 1.5rem;
                font-weight: 700;
                color: var(--white);
                margin-bottom: 1.5rem;
            }
            .sidebar-controller-title {
                font-size: 0.875rem;
                font-weight: 600;
                color: var(--slate-400);
                text-transform: uppercase;
                letter-spacing: 0.05em;
                margin-top: 1.5rem;
                margin-bottom: 0.5rem;
            }
            .sidebar-link {
                display: block;
                padding: 0.5rem 0.75rem;
                border-radius: 0.375rem;
                font-size: 0.875rem;
                text-decoration: none;
                color: var(--slate-300);
                transition: background-color 0.2s;
                cursor: pointer;
            }
            .sidebar-link:hover {
                background-color: var(--slate-800);
            }
            .sidebar-link.active {
                background-color: var(--slate-700);
                color: var(--white);
            }

            /* Main Content */
            .intro-section { margin-bottom: 3rem; }
            .intro-section h1 { font-size: 2.25rem; font-weight: 800; color: var(--slate-900); }
            .intro-section .description { margin-top: 0.5rem; font-size: 1.125rem; color: var(--slate-600); }
            .intro-section .version { margin-top: 0.25rem; font-size: 0.875rem; color: var(--slate-500); }

            .endpoint-card {
                display: none;
                background-color: var(--white);
                border: 1px solid #e2e8f0;
                border-radius: 0.75rem;
                box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
                margin-bottom: 2rem;
                overflow: hidden;
            }
            .endpoint-card.active { display: block; }
            .endpoint-card-header {
                background-color: var(--slate-50);
                border-bottom: 1px solid #e2e8f0;
                padding: 1rem 1.5rem;
            }
            .endpoint-card-header h3 { font-size: 1.25rem; font-weight: 700; color: var(--slate-900); }
            .endpoint-url-container { display: flex; align-items: center; margin-top: 0.5rem; }
            .http-method {
                font-family: monospace;
                font-size: 0.75rem;
                font-weight: 600;
                padding: 0.25rem 0.5rem;
                border-radius: 0.375rem;
            }
            .endpoint-url { margin-left: 0.75rem; font-family: monospace; color: var(--slate-600); font-size: 0.875rem; word-break: break-all; }
            .endpoint-card-body { padding: 1.5rem; }
            .endpoint-card-body .description { color: var(--slate-600); margin-top: 0.25rem; }
            .details-container { margin-top: 1.5rem; }
            .details-container > div + div { margin-top: 1.5rem; }
            .details-container h4 {
                font-size: 0.875rem;
                font-weight: 600;
                color: var(--slate-900);
                text-transform: uppercase;
                letter-spacing: 0.05em;
                margin-bottom: 0.5rem;
            }

            /* Tables */
            .table-container { overflow-x: auto; }
            table { width: 100%%; text-align: left; margin-top: 0.5rem; border-collapse: collapse; }
            table thead { background-color: var(--slate-50); }
            table thead tr { border-bottom: 2px solid #e2e8f0; }
            table th { padding: 0.5rem; font-weight: 600; font-size: 0.75rem; color: var(--slate-600); }
            table tbody tr { border-bottom: 1px solid #e2e8f0; }
            table tbody tr:last-child { border-bottom: 0; }
            table td { padding: 0.75rem; font-size: 0.875rem; color: var(--slate-600); vertical-align: top; }
            table .font-mono { font-family: monospace; }
            table .text-purple { color: var(--purple-600); }
            table .text-red { color: var(--red-600); font-weight: 600; }

            /* Utility Panel */
            #utility-panel .panel-content { padding: 1.5rem; }
            .utility-section {
                background-color: var(--slate-800);
                border: 1px solid var(--slate-700);
                border-radius: 0.5rem;
                margin-bottom: 1.5rem;
            }
            .utility-section-header {
                padding: 0.75rem 1rem;
                font-weight: 600;
                border-bottom: 1px solid var(--slate-700);
                cursor: pointer;
            }
            .utility-section-content {
                padding: 1rem;
            }
            .form-group { margin-bottom: 1rem; }
            .form-group label { display: block; font-weight: 500; margin-bottom: 0.25rem; font-size: 0.875rem; color: var(--slate-300);}
            .form-group input, .form-group textarea {
                width: 100%%;
                padding: 0.5rem;
                border: 1px solid var(--slate-600);
                border-radius: 0.25rem;
                background-color: var(--slate-900);
                color: var(--slate-300);
            }
            .send-request-btn {
                background-color: var(--indigo-600);
                color: var(--white);
                padding: 0.75rem 1rem;
                border: none;
                border-radius: 0.25rem;
                cursor: pointer;
                width: 100%%;
                font-weight: 600;
                font-size: 1rem;
            }
            .language-tabs { display: flex; margin-bottom: 1rem; border-bottom: 1px solid var(--slate-700); }
            .lang-tab {
                padding: 0.5rem 1rem;
                border: none;
                background: none;
                color: var(--slate-400);
                cursor: pointer;
            }
            .lang-tab.active { color: var(--white); border-bottom: 2px solid var(--indigo-600); }
            #snippet-container, #tester-response-container {
                background-color: var(--slate-900);
                border-radius: 0.375rem;
                min-height: 150px;
                display: flex;
                align-items: center;
                justify-content: center;
                padding: 1rem;
                border: 1px solid var(--slate-700);
            }
            #snippet-container pre, #tester-response-container pre { margin: 0; width: 100%%; white-space: pre-wrap; font-size: 0.875rem; }
            .loader {
                border: 4px solid var(--slate-700);
                border-radius: 50%%;
                border-top: 4px solid var(--indigo-600);
                width: 30px;
                height: 30px;
                animation: spin 1s linear infinite;
            }
            @keyframes spin { 0%% { transform: rotate(0deg); } 100%% { transform: rotate(360deg); } }
            .status-success { color: #22c55e; }
            .status-error { color: #ef4444; }

            /* Method Colors */
            .method-get { background-color: rgba(59, 130, 246, 0.1); color: #3B82F6; }
            .method-post { background-color: rgba(16, 185, 129, 0.1); color: #10B981; }
            .method-put { background-color: rgba(245, 158, 11, 0.1); color: #F59E0B; }
            .method-patch { background-color: rgba(239, 68, 68, 0.1); color: #EF4444; }
            .method-delete { background-color: rgba(139, 92, 246, 0.1); color: #8B5CF6; }

            /* Responsive */
            @media (max-width: 1280px) {
                #main-content-wrapper { flex-direction: column; margin-left: 18rem; }
                #utility-panel { position: static; width: 100%%; height: auto; }
            }
            @media (max-width: 1024px) {
                #sidebar { position: static; width: 100%%; height: auto; }
                #main-content-wrapper { margin-left: 0; }
            }
            """;
            
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }
            
            body {
                font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                background-color: var(--bg-primary);
                color: var(--text-primary);
                line-height: 1.6;
                font-size: 16px;
                font-weight: 400;
                -webkit-font-smoothing: antialiased;
                -moz-osx-font-smoothing: grayscale;
            }
            
            .font-mono {
                font-family: 'JetBrains Mono', 'Fira Code', 'Monaco', 'Menlo', monospace;
            }
            
            .app-container {
                display: flex;
                min-height: 100vh;
                background-color: var(--bg-primary);
            }
            
            .sidebar {
                width: 280px;
                background-color: var(--bg-primary);
                border-right: 1px solid var(--border-color);
                padding: 0;
                overflow-y: auto;
                position: fixed;
                height: 100vh;
                z-index: 100;
            }
            
            .sidebar-header {
                padding: 1.5rem 1.5rem 1rem 1.5rem;
                border-bottom: 1px solid var(--border-light);
                margin-bottom: 0;
            }
            
            .logo {
                font-size: 1.25rem;
                font-weight: 700;
                color: var(--text-primary);
                text-decoration: none;
                display: flex;
                align-items: center;
                gap: 0.5rem;
            }
            
            .logo-icon {
                width: 28px;
                height: 28px;
                background: var(--primary-color);
                border-radius: 6px;
                display: flex;
                align-items: center;
                justify-content: center;
                font-weight: 600;
                color: #000;
                font-size: 14px;
            }
            
            .theme-toggle {
                background: none;
                border: 1px solid var(--border-color);
                border-radius: 8px;
                padding: 0.5rem;
                cursor: pointer;
                color: var(--text-primary);
                font-size: 1.2rem;
            }
            
            .search-container {
                padding: 1rem 1.5rem;
                border-bottom: 1px solid var(--border-light);
            }
            
            .search-input {
                width: 100%%;
                padding: 0.75rem 1rem;
                border: 1px solid var(--border-color);
                border-radius: 8px;
                background-color: var(--bg-secondary);
                color: var(--text-primary);
                font-size: 0.875rem;
                font-family: inherit;
                transition: all 0.2s ease;
                position: relative;
            }
            
            .search-input:focus {
                outline: none;
                border-color: var(--primary-color);
                box-shadow: 0 0 0 3px rgba(255, 229, 0, 0.1);
            }
            
            .search-input::placeholder {
                color: var(--text-muted);
            }
            
            .search-shortcut {
                position: absolute;
                right: 0.75rem;
                top: 50%%;
                transform: translateY(-50%%);
                font-size: 0.75rem;
                color: var(--text-muted);
                font-family: var(--font-mono);
                pointer-events: none;
            }
            
            .nav-section {
                padding: 0;
                margin-bottom: 0;
            }
            
            .nav-section-title {
                font-size: 0.75rem;
                font-weight: 600;
                color: var(--text-muted);
                margin: 1.5rem 1.5rem 0.75rem 1.5rem;
                text-transform: uppercase;
                letter-spacing: 0.05em;
            }
            
            .nav-list {
                list-style: none;
                margin: 0;
                padding: 0;
            }
            
            .nav-item {
                margin: 0;
            }
            
            .nav-link {
                display: block;
                padding: 0.625rem 1.5rem;
                color: var(--text-secondary);
                text-decoration: none;
                font-size: 0.875rem;
                font-weight: 500;
                border-left: 3px solid transparent;
                transition: all 0.2s ease;
                position: relative;
            }
            
            .nav-link:hover {
                color: var(--text-primary);
                background-color: var(--bg-secondary);
            }
            
            .nav-link.active {
                color: var(--text-primary);
                background-color: var(--bg-tertiary);
                border-left-color: var(--primary-color);
                font-weight: 600;
            }
            
            .nav-link.active::before {
                content: '';
                position: absolute;
                left: 0;
                top: 0;
                bottom: 0;
                width: 3px;
                background: var(--primary-color);
            }
            
            .main-content {
                flex: 1;
                margin-left: 280px;
                overflow-y: auto;
                background-color: var(--bg-primary);
            }
            
            .content-wrapper {
                max-width: 1200px;
                margin: 0 auto;
                padding: 2rem 3rem;
            }
            
            .content-header {
                margin-bottom: 3rem;
                padding-bottom: 2rem;
                border-bottom: 1px solid var(--border-light);
            }
            
            .page-title {
                font-size: 2.5rem;
                font-weight: 700;
                margin-bottom: 0.5rem;
                line-height: 1.2;
                color: var(--text-primary);
            }
            
            .page-subtitle {
                font-size: 1.125rem;
                color: var(--text-secondary);
                font-weight: 400;
                margin-bottom: 1.5rem;
            }
            
            .header-actions {
                display: flex;
                gap: 1rem;
                align-items: center;
            }
            
            .btn {
                display: inline-flex;
                align-items: center;
                gap: 0.5rem;
                padding: 0.75rem 1.5rem;
                border-radius: 8px;
                font-weight: 600;
                font-size: 0.875rem;
                text-decoration: none;
                transition: all 0.2s ease;
                border: none;
                cursor: pointer;
            }
            
            .btn-primary {
                background: var(--primary-color);
                color: #000;
            }
            
            .btn-primary:hover {
                background: var(--primary-hover);
                transform: translateY(-1px);
            }
            
            .btn-secondary {
                background: var(--bg-secondary);
                color: var(--text-primary);
                border: 1px solid var(--border-color);
            }
            
            .btn-secondary:hover {
                background: var(--bg-tertiary);
                border-color: var(--border-color);
            }
            
            .version-badge {
                background-color: var(--bg-tertiary);
                color: var(--text-secondary);
                padding: 0.25rem 0.75rem;
                border-radius: 6px;
                font-size: 0.75rem;
                font-weight: 500;
                border: 1px solid var(--border-color);
            }
            
            .overview-grid {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 2rem;
                margin-bottom: 3rem;
            }
            
            .overview-card {
                background: var(--bg-secondary);
                border: 1px solid var(--border-color);
                border-radius: 12px;
                padding: 1.5rem;
                transition: all 0.2s ease;
            }
            
            .overview-card:hover {
                border-color: var(--primary-color);
                box-shadow: var(--shadow-lg);
            }
            
            .overview-card-header {
                margin-bottom: 1rem;
            }
            
            .overview-card-header h3 {
                font-size: 1.125rem;
                font-weight: 600;
                color: var(--text-primary);
                margin: 0;
            }
            
            .overview-stats {
                display: flex;
                justify-content: space-between;
                gap: 1rem;
            }
            
            .stat-item {
                text-align: center;
                flex: 1;
            }
            
            .stat-label {
                display: block;
                font-size: 0.875rem;
                color: var(--text-muted);
                margin-bottom: 0.25rem;
            }
            
            .stat-value {
                display: block;
                font-size: 1.5rem;
                font-weight: 700;
                color: var(--text-primary);
            }
            
            .overview-content {
                color: var(--text-secondary);
                line-height: 1.6;
            }
            
            .overview-content p {
                margin-bottom: 1rem;
            }
            
            .card-link {
                color: var(--primary-color);
                text-decoration: none;
                font-weight: 500;
                font-size: 0.875rem;
            }
            
            .card-link:hover {
                text-decoration: underline;
            }
            
            .stats-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
                gap: 1.5rem;
                margin-top: 2rem;
            }
            
            .stat-card {
                background-color: var(--bg-secondary);
                border: 1px solid var(--border-color);
                border-radius: 12px;
                padding: 1.5rem;
                box-shadow: var(--shadow);
                transition: transform 0.2s, box-shadow 0.2s;
            }
            
            .stat-card:hover {
                transform: translateY(-2px);
                box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -1px rgb(0 0 0 / 0.06);
            }
            
            .stat-icon {
                font-size: 1.5rem;
                margin-bottom: 0.5rem;
                display: block;
            }
            
            .stat-card h3 {
                font-size: 0.875rem;
                font-weight: 600;
                color: var(--text-secondary);
                margin-bottom: 0.5rem;
            }
            
            .stat-number {
                font-size: 2rem;
                font-weight: 700;
                color: var(--primary-color);
            }
            
            .controllers-section {
                margin-top: 3rem;
            }
            
            .section-header {
                margin-bottom: 2rem;
            }
            
            .section-title {
                font-size: 1.875rem;
                font-weight: 700;
                margin-bottom: 0.5rem;
                color: var(--text-primary);
            }
            
            .section-subtitle {
                font-size: 1rem;
                color: var(--text-secondary);
                margin: 0;
            }
            
            .controllers-grid {
                display: grid;
                grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
                gap: 1.5rem;
            }
            
            .controller-card {
                background: var(--bg-secondary);
                border: 1px solid var(--border-color);
                border-radius: 12px;
                padding: 1.5rem;
                cursor: pointer;
                transition: all 0.2s ease;
                position: relative;
                overflow: hidden;
            }
            
            .controller-card:hover {
                border-color: var(--primary-color);
                box-shadow: var(--shadow-lg);
                transform: translateY(-2px);
            }
            
            .controller-header {
                display: flex;
                align-items: flex-start;
                gap: 1rem;
                margin-bottom: 1rem;
            }
            
            .controller-icon {
                width: 40px;
                height: 40px;
                background: var(--bg-tertiary);
                border-radius: 8px;
                display: flex;
                align-items: center;
                justify-content: center;
                color: var(--text-secondary);
                flex-shrink: 0;
            }
            
            .controller-info {
                flex: 1;
                min-width: 0;
            }
            
            .controller-name {
                font-size: 1.125rem;
                font-weight: 600;
                color: var(--text-primary);
                margin: 0 0 0.25rem 0;
                line-height: 1.3;
            }
            
            .controller-path {
                font-size: 0.875rem;
                color: var(--text-muted);
                display: block;
            }
            
            .controller-badge {
                background: var(--primary-color);
                color: #000;
                padding: 0.25rem 0.5rem;
                border-radius: 6px;
                font-size: 0.75rem;
                font-weight: 600;
                flex-shrink: 0;
            }
            
            .endpoint-count {
                font-weight: 600;
            }
            
            .controller-description {
                color: var(--text-secondary);
                font-size: 0.875rem;
                line-height: 1.5;
                margin-bottom: 1rem;
            }
            
            .controller-footer {
                display: flex;
                justify-content: space-between;
                align-items: center;
            }
            
            .controller-tag {
                font-size: 0.75rem;
                color: var(--text-muted);
                background: var(--bg-tertiary);
                padding: 0.25rem 0.5rem;
                border-radius: 4px;
                font-weight: 500;
            }
            
            .controller-arrow {
                color: var(--text-muted);
                font-weight: 500;
                opacity: 0;
                transition: opacity 0.2s ease;
            }
            
            .controller-card:hover .controller-arrow {
                opacity: 1;
            }
            
            @media (max-width: 768px) {
                .app-container {
                    flex-direction: column;
                }
                
                .sidebar {
                    width: 100%%;
                    height: auto;
                    border-right: none;
                    border-bottom: 1px solid var(--border-color);
                }
                
                .stats-grid {
                    grid-template-columns: 1fr;
                }
            }
            """, brandingColor);
    }

    private String getJavaScript() {
        return """
            // Theme functionality
            function toggleTheme() {
                const html = document.documentElement;
                const currentTheme = html.getAttribute('data-theme') || 'light';
                const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
                
                html.setAttribute('data-theme', newTheme);
                localStorage.setItem('docx-theme', newTheme);
            }
            
            function initTheme() {
                const savedTheme = localStorage.getItem('docx-theme');
                const systemDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
                
                let theme = 'light';
                if (savedTheme) {
                    theme = savedTheme;
                } else if (systemDark) {
                    theme = 'dark';
                }
                
                document.documentElement.setAttribute('data-theme', theme);
            }
            
            // Search functionality with keyboard shortcuts
            function initSearch() {
                const searchInput = document.getElementById('search-input');
                if (!searchInput) return;
                
                // Focus search on CMD/Ctrl + K
                document.addEventListener('keydown', function(e) {
                    if ((e.metaKey || e.ctrlKey) && e.key === 'k') {
                        e.preventDefault();
                        searchInput.focus();
                    }
                    
                    // Escape to blur search
                    if (e.key === 'Escape' && document.activeElement === searchInput) {
                        searchInput.blur();
                    }
                });
                
                // Search through navigation items
                searchInput.addEventListener('input', function(e) {
                    const query = e.target.value.toLowerCase().trim();
                    const navSections = document.querySelectorAll('.nav-section');
                    
                    if (query === '') {
                        // Show all items when search is empty
                        navSections.forEach(section => {
                            section.style.display = 'block';
                            const navItems = section.querySelectorAll('.nav-item');
                            navItems.forEach(item => item.style.display = 'block');
                        });
                        return;
                    }
                    
                    navSections.forEach(section => {
                        const navItems = section.querySelectorAll('.nav-item');
                        let hasVisibleItems = false;
                        
                        navItems.forEach(item => {
                            const link = item.querySelector('.nav-link');
                            const text = link ? link.textContent.toLowerCase() : '';
                            
                            if (text.includes(query)) {
                                item.style.display = 'block';
                                hasVisibleItems = true;
                            } else {
                                item.style.display = 'none';
                            }
                        });
                        
                        // Hide section if no items match
                        section.style.display = hasVisibleItems ? 'block' : 'none';
                    });
                });
            }
            
            // Navigation highlighting
            function initNavigation() {
                const navLinks = document.querySelectorAll('.nav-link');
                
                navLinks.forEach(link => {
                    link.addEventListener('click', function(e) {
                        // Remove active class from all links
                        navLinks.forEach(l => l.classList.remove('active'));
                        // Add active class to clicked link
                        this.classList.add('active');
                    });
                });
            }
            
            // Controller cards interaction
            function initControllerCards() {
                const controllerCards = document.querySelectorAll('.controller-card');
                
                controllerCards.forEach(card => {
                    card.addEventListener('click', function() {
                        // Add click effect or navigation logic here
                        console.log('Controller card clicked:', this);
                    });
                });
            }
            
            // Smooth scrolling for anchor links
            function initSmoothScrolling() {
                const anchorLinks = document.querySelectorAll('a[href^="#"]');
                
                anchorLinks.forEach(link => {
                    link.addEventListener('click', function(e) {
                        const targetId = this.getAttribute('href').substring(1);
                        const targetElement = document.getElementById(targetId);
                        
                        if (targetElement) {
                            e.preventDefault();
                            targetElement.scrollIntoView({
                                behavior: 'smooth',
                                block: 'start'
                            });
                        }
                    });
                });
            }
            
            // Initialize everything when DOM is ready
            document.addEventListener('DOMContentLoaded', function() {
                initTheme();
                initSearch();
                initNavigation();
                initControllerCards();
                initSmoothScrolling();
                
                // Add loading fade-in effect
                document.body.style.opacity = '0';
                document.body.style.transition = 'opacity 0.3s ease';
                setTimeout(() => {
                    document.body.style.opacity = '1';
                }, 50);
            });
            
            // Listen for system theme changes
            window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', function(e) {
                if (!localStorage.getItem('docx-theme')) {
                    const theme = e.matches ? 'dark' : 'light';
                    document.documentElement.setAttribute('data-theme', theme);
                }
            });
            """;
    }
}