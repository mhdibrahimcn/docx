package com.docx.generators;

import java.util.Map;

public class HtmlTemplateEngine {

    public String generateIndexPage(Map<String, Object> context) {
        String title = (String) context.get("title");
        String version = (String) context.get("version");
        String description = (String) context.get("description");
        String brandingColor = (String) context.get("brandingColor");
        
        return buildHtmlPage(title, generateIndexContent(context), brandingColor);
    }

    public String generateControllerPage(Map<String, Object> context) {
        String brandingColor = (String) context.get("brandingColor");
        return buildHtmlPage("Controller Documentation", generateControllerContent(context), brandingColor);
    }

    public String generateEndpointPage(Map<String, Object> context) {
        String brandingColor = (String) context.get("brandingColor");
        return buildHtmlPage("Endpoint Documentation", generateEndpointContent(context), brandingColor);
    }

    private String buildHtmlPage(String title, String content, String brandingColor) {
        return String.format("""
            <!DOCTYPE html>
            <html lang="en" data-theme="auto">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>%s</title>
                <style>
                    %s
                </style>
            </head>
            <body>
                <div class="app-container">
                    <nav class="sidebar">
                        %s
                    </nav>
                    <main class="main-content">
                        %s
                    </main>
                </div>
                <script>
                    %s
                </script>
            </body>
            </html>
            """, title, getCssStyles(brandingColor), generateSidebar(), content, getJavaScript());
    }

    private String generateIndexContent(Map<String, Object> context) {
        String title = (String) context.get("title");
        String version = (String) context.get("version");
        String description = (String) context.get("description");
        
        return String.format("""
            <div class="content-header">
                <h1 class="page-title">%s</h1>
                <span class="version-badge">v%s</span>
            </div>
            <div class="description">
                <p>%s</p>
            </div>
            <div class="api-overview">
                <div class="stats-grid">
                    <div class="stat-card">
                        <h3>Controllers</h3>
                        <span class="stat-number">0</span>
                    </div>
                    <div class="stat-card">
                        <h3>Endpoints</h3>
                        <span class="stat-number">0</span>
                    </div>
                    <div class="stat-card">
                        <h3>Models</h3>
                        <span class="stat-number">0</span>
                    </div>
                </div>
            </div>
            """, title, version, description);
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
                <h2 class="logo">Docx</h2>
                <button class="theme-toggle" onclick="toggleTheme()">
                    <span class="theme-icon">🌙</span>
                </button>
            </div>
            <div class="search-container">
                <input type="search" class="search-input" placeholder="Search endpoints...">
            </div>
            <nav class="nav-menu">
                <div class="nav-section">
                    <h3 class="nav-section-title">Overview</h3>
                    <ul class="nav-list">
                        <li><a href="index.html" class="nav-link active">Dashboard</a></li>
                    </ul>
                </div>
                <div class="nav-section">
                    <h3 class="nav-section-title">Controllers</h3>
                    <ul class="nav-list">
                        <!-- Controllers will be dynamically added here -->
                    </ul>
                </div>
                <div class="nav-section">
                    <h3 class="nav-section-title">Models</h3>
                    <ul class="nav-list">
                        <!-- Models will be dynamically added here -->
                    </ul>
                </div>
            </nav>
            """;
    }

    private String getCssStyles(String brandingColor) {
        return String.format("""
            :root {
                --primary-color: %s;
                --primary-hover: #2563eb;
                --text-primary: #1f2937;
                --text-secondary: #6b7280;
                --bg-primary: #ffffff;
                --bg-secondary: #f9fafb;
                --border-color: #e5e7eb;
                --shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1), 0 1px 2px -1px rgb(0 0 0 / 0.1);
            }
            
            [data-theme="dark"] {
                --text-primary: #f9fafb;
                --text-secondary: #d1d5db;
                --bg-primary: #111827;
                --bg-secondary: #1f2937;
                --border-color: #374151;
                --shadow: 0 1px 3px 0 rgb(0 0 0 / 0.3), 0 1px 2px -1px rgb(0 0 0 / 0.3);
            }
            
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }
            
            body {
                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                background-color: var(--bg-primary);
                color: var(--text-primary);
                line-height: 1.6;
            }
            
            .app-container {
                display: flex;
                min-height: 100vh;
            }
            
            .sidebar {
                width: 280px;
                background-color: var(--bg-secondary);
                border-right: 1px solid var(--border-color);
                padding: 1.5rem;
                overflow-y: auto;
            }
            
            .sidebar-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 2rem;
            }
            
            .logo {
                font-size: 1.5rem;
                font-weight: 700;
                color: var(--primary-color);
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
                margin-bottom: 2rem;
            }
            
            .search-input {
                width: 100%%;
                padding: 0.75rem;
                border: 1px solid var(--border-color);
                border-radius: 8px;
                background-color: var(--bg-primary);
                color: var(--text-primary);
                font-size: 0.875rem;
            }
            
            .nav-section {
                margin-bottom: 2rem;
            }
            
            .nav-section-title {
                font-size: 0.875rem;
                font-weight: 600;
                color: var(--text-secondary);
                margin-bottom: 0.5rem;
                text-transform: uppercase;
                letter-spacing: 0.05em;
            }
            
            .nav-list {
                list-style: none;
            }
            
            .nav-link {
                display: block;
                padding: 0.5rem 0.75rem;
                color: var(--text-primary);
                text-decoration: none;
                border-radius: 6px;
                transition: background-color 0.2s;
            }
            
            .nav-link:hover,
            .nav-link.active {
                background-color: var(--primary-color);
                color: white;
            }
            
            .main-content {
                flex: 1;
                padding: 2rem;
                overflow-y: auto;
            }
            
            .content-header {
                display: flex;
                align-items: center;
                gap: 1rem;
                margin-bottom: 2rem;
            }
            
            .page-title {
                font-size: 2rem;
                font-weight: 700;
            }
            
            .version-badge {
                background-color: var(--primary-color);
                color: white;
                padding: 0.25rem 0.75rem;
                border-radius: 9999px;
                font-size: 0.875rem;
                font-weight: 500;
            }
            
            .description {
                margin-bottom: 2rem;
                color: var(--text-secondary);
                font-size: 1.125rem;
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
            // Theme toggle functionality
            function toggleTheme() {
                const html = document.documentElement;
                const currentTheme = html.getAttribute('data-theme');
                const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
                
                html.setAttribute('data-theme', newTheme);
                localStorage.setItem('docx-theme', newTheme);
                
                // Update theme icon
                const themeIcon = document.querySelector('.theme-icon');
                themeIcon.textContent = newTheme === 'dark' ? '☀️' : '🌙';
            }
            
            // Initialize theme
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
                const themeIcon = document.querySelector('.theme-icon');
                if (themeIcon) {
                    themeIcon.textContent = theme === 'dark' ? '☀️' : '🌙';
                }
            }
            
            // Search functionality
            function initSearch() {
                const searchInput = document.querySelector('.search-input');
                if (searchInput) {
                    searchInput.addEventListener('input', function(e) {
                        const query = e.target.value.toLowerCase();
                        const navLinks = document.querySelectorAll('.nav-link');
                        
                        navLinks.forEach(link => {
                            const text = link.textContent.toLowerCase();
                            const listItem = link.closest('li');
                            if (listItem) {
                                listItem.style.display = text.includes(query) ? 'block' : 'none';
                            }
                        });
                    });
                }
            }
            
            // Initialize on DOM load
            document.addEventListener('DOMContentLoaded', function() {
                initTheme();
                initSearch();
            });
            
            // Listen for system theme changes
            window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', function(e) {
                if (!localStorage.getItem('docx-theme')) {
                    const theme = e.matches ? 'dark' : 'light';
                    document.documentElement.setAttribute('data-theme', theme);
                    const themeIcon = document.querySelector('.theme-icon');
                    if (themeIcon) {
                        themeIcon.textContent = theme === 'dark' ? '☀️' : '🌙';
                    }
                }
            });
            """;
    }
}