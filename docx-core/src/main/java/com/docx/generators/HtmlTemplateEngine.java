package com.docx.generators;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class HtmlTemplateEngine {

    private static final Logger logger = LoggerFactory.getLogger(HtmlTemplateEngine.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generate(Map<String, Object> context) {
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
            <body data-theme="light">
                <!-- Theme Toggle Button -->
                <button class="theme-toggle" onclick="toggleTheme()">
                    🌙
                </button>
                
                <div class="main-container">
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
                        <main id="main-content">
                            <div id="api-content"></div>
                        </main>
                        <aside id="utility-panel">
                            <div id="utility-panel-content" class="panel-content"></div>
                        </aside>
                    </div>
                </div>

                <script>
                    const jsonData = %s;
                    %s
                </script>
            </body>
            </html>
            """, title, getStyles(), jsonData, getJavaScript());
    }

    private String convertContextToJson(Map<String, Object> context) {
        try {
            return objectMapper.writeValueAsString(context);
        } catch (Exception e) {
            logger.error("Error converting context to JSON", e);
            return "{}";
        }
    }

    private String getStyles() {
        return """
            /* Light Theme (Default) */
            :root {
                --bg-primary: #ffffff;
                --bg-secondary: #f8fafc;
                --bg-tertiary: #f1f5f9;
                --bg-card: #ffffff;
                --bg-panel: #f8fafc;
                --border-color: #e2e8f0;
                --border-subtle: #f1f5f9;
                --text-primary: #0f172a;
                --text-secondary: #475569;
                --text-muted: #64748b;
                --text-inverse: #ffffff;
                --accent-primary: #3b82f6;
                --accent-secondary: #8b5cf6;
                --success-color: #10b981;
                --warning-color: #f59e0b;
                --error-color: #ef4444;
                --shadow-sm: 0 1px 2px 0 rgb(0 0 0 / 0.05);
                --shadow-md: 0 4px 6px -1px rgb(0 0 0 / 0.1);
                --shadow-lg: 0 10px 15px -3px rgb(0 0 0 / 0.1);
                --radius-sm: 8px;
                --radius-md: 12px;
                --radius-lg: 16px;
                --radius-xl: 20px;
            }
            
            /* Dark Theme */
            [data-theme="dark"] {
                --bg-primary: #0f172a;
                --bg-secondary: #1e293b;
                --bg-tertiary: #334155;
                --bg-card: #1e293b;
                --bg-panel: #334155;
                --border-color: #475569;
                --border-subtle: #334155;
                --text-primary: #f8fafc;
                --text-secondary: #cbd5e1;
                --text-muted: #94a3b8;
                --text-inverse: #0f172a;
                --accent-primary: #3b82f6;
                --accent-secondary: #8b5cf6;
                --success-color: #10b981;
                --warning-color: #f59e0b;
                --error-color: #ef4444;
                --shadow-sm: 0 1px 2px 0 rgb(0 0 0 / 0.3);
                --shadow-md: 0 4px 6px -1px rgb(0 0 0 / 0.3);
                --shadow-lg: 0 10px 15px -3px rgb(0 0 0 / 0.3);
                --radius-sm: 8px;
                --radius-md: 12px;
                --radius-lg: 16px;
                --radius-xl: 20px;
            }
            
            /* Theme Toggle Button */
            .theme-toggle {
                position: fixed;
                top: 24px;
                right: 24px;
                z-index: 1000;
                width: 48px;
                height: 48px;
                background: var(--bg-card);
                border: 2px solid var(--border-color);
                border-radius: var(--radius-xl);
                display: flex;
                align-items: center;
                justify-content: center;
                cursor: pointer;
                color: var(--text-primary);
                font-size: 20px;
                transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
                box-shadow: var(--shadow-md);
            }
            .theme-toggle:hover {
                background: var(--bg-tertiary);
                transform: translateY(-2px) scale(1.05);
                box-shadow: var(--shadow-lg);
                border-color: var(--accent-primary);
            }
            .theme-toggle:active {
                transform: translateY(0) scale(0.95);
            }

            html { 
                scroll-behavior: smooth;
                font-size: 16px;
            }
            body {
                font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
                background: var(--bg-primary);
                color: var(--text-primary);
                margin: 0;
                line-height: 1.6;
                font-weight: 400;
                -webkit-font-smoothing: antialiased;
                -moz-osx-font-smoothing: grayscale;
                transition: background-color 0.3s ease, color 0.3s ease;
            }
            * { 
                box-sizing: border-box; 
            }
            .font-mono { 
                font-family: 'JetBrains Mono', 'SF Mono', Monaco, 'Cascadia Code', monospace; 
            }
            
            .main-container { 
                display: flex; 
                min-height: 100vh;
                background: var(--bg-primary);
            }
            #sidebar {
                width: 320px;
                background: var(--bg-card);
                border-right: 1px solid var(--border-color);
                position: fixed;
                height: 100vh;
                overflow-y: auto;
                flex-shrink: 0;
                display: flex;
                flex-direction: column;
                box-shadow: var(--shadow-md);
            }
            #main-content-wrapper { 
                display: flex; 
                width: 100%; 
                margin-left: 320px;
                min-height: 100vh;
            }
            #main-content { 
                flex: 1; 
                padding: 32px; 
                max-width: calc(100vw - 320px - 400px);
                background: var(--bg-primary);
            }
            #api-content { 
                max-width: 100%; 
            }
            #utility-panel {
                width: 400px;
                background: var(--bg-card);
                border-left: 1px solid var(--border-color);
                position: fixed;
                right: 0;
                top: 0;
                height: 100vh;
                overflow-y: auto;
                flex-shrink: 0;
                box-shadow: var(--shadow-md);
            }

            .sidebar-header { 
                padding: 24px; 
                border-bottom: 1px solid var(--border-color);
                background: var(--bg-card);
            }
            .logo {
                font-size: 18px;
                font-weight: 700;
                color: var(--text-primary);
                text-decoration: none;
                display: flex;
                align-items: center;
                gap: 12px;
                transition: all 0.2s ease;
            }
            .logo:hover {
                color: var(--accent-primary);
            }
            .logo-icon {
                width: 40px;
                height: 40px;
                background: linear-gradient(135deg, var(--accent-primary), var(--accent-secondary));
                border-radius: var(--radius-md);
                display: flex;
                align-items: center;
                justify-content: center;
                font-weight: 700;
                color: var(--text-inverse);
                font-size: 16px;
                box-shadow: var(--shadow-sm);
            }
            .search-container { 
                padding: 24px; 
                border-bottom: 1px solid var(--border-color);
            }
            .search-input {
                width: 100%;
                padding: 12px 16px;
                border: 2px solid var(--border-color);
                border-radius: var(--radius-md);
                background: var(--bg-secondary);
                color: var(--text-primary);
                font-size: 14px;
                font-weight: 400;
                transition: all 0.2s ease;
                outline: none;
            }
            .search-input:focus {
                border-color: var(--accent-primary);
                box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
                background: var(--bg-primary);
            }
            .search-input::placeholder {
                color: var(--text-muted);
            }
            .search-shortcut {
                position: absolute;
                right: 36px;
                top: 124px;
                font-size: 12px;
                color: var(--text-muted);
                font-family: 'JetBrains Mono', monospace;
                pointer-events: none;
                background: var(--bg-tertiary);
                padding: 4px 8px;
                border-radius: var(--radius-sm);
                border: 1px solid var(--border-color);
            }
            .nav-menu { 
                flex: 1; 
                overflow-y: auto; 
                padding: 16px;
            }
            .nav-section { 
                margin-bottom: 24px; 
            }
            .nav-section-title {
                font-size: 12px;
                font-weight: 600;
                color: var(--text-muted);
                margin: 0 0 12px 16px;
                text-transform: uppercase;
                letter-spacing: 0.5px;
            }
            .nav-list { 
                list-style: none; 
                margin: 0; 
                padding: 0; 
                display: flex;
                flex-direction: column;
                gap: 4px;
            }
            .sidebar-link {
                display: flex;
                align-items: center;
                padding: 12px 16px;
                color: var(--text-secondary);
                text-decoration: none;
                font-size: 14px;
                font-weight: 500;
                border-radius: var(--radius-md);
                transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
                position: relative;
                margin: 0 8px;
            }
            .sidebar-link:hover { 
                color: var(--text-primary); 
                background: var(--bg-secondary);
                transform: translateX(4px);
            }
            .sidebar-link.active {
                color: var(--accent-primary);
                background: rgba(59, 130, 246, 0.1);
                font-weight: 600;
            }
            .sidebar-link.active::before {
                content: '';
                position: absolute;
                left: 0;
                top: 50%;
                transform: translateY(-50%);
                width: 4px;
                height: 24px;
                background: var(--accent-primary);
                border-radius: 0 2px 2px 0;
            }

            .intro-section { 
                margin-bottom: 48px; 
            }
            .intro-section h1 { 
                font-size: 36px; 
                font-weight: 800; 
                color: var(--text-primary);
                margin: 0 0 12px 0;
                line-height: 1.2;
            }
            .intro-section .description { 
                font-size: 18px; 
                color: var(--text-secondary);
                margin: 0 0 8px 0;
                line-height: 1.5;
            }
            .intro-section .version { 
                font-size: 14px; 
                color: var(--text-muted);
                margin: 0;
            }

            .endpoint-card {
                display: none;
                background: var(--bg-card);
                border: 1px solid var(--border-color);
                border-radius: var(--radius-lg);
                box-shadow: var(--shadow-sm);
                margin-bottom: 24px;
                overflow: hidden;
                transition: all 0.2s ease;
            }
            .endpoint-card:hover {
                box-shadow: var(--shadow-md);
                border-color: var(--accent-primary);
            }
            .endpoint-card.active { 
                display: block; 
            }
            .endpoint-card-header {
                background: var(--bg-secondary);
                border-bottom: 1px solid var(--border-color);
                padding: 24px;
            }
            .endpoint-card-header h3 { 
                font-size: 20px; 
                font-weight: 700; 
                color: var(--text-primary);
                margin: 0 0 12px 0;
            }
            .endpoint-url-container { display: flex; align-items: center; margin-top: 0.5rem; }
            .http-method {
                font-family: 'JetBrains Mono', monospace;
                font-size: 0.75rem;
                font-weight: 600;
                padding: 0.25rem 0.5rem;
                border-radius: 0.375rem;
            }
            .endpoint-url {
                margin-left: 0.75rem;
                font-family: 'JetBrains Mono', monospace;
                color: var(--slate-600);
                font-size: 0.875rem;
                word-break: break-all;
            }
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

            /* Table Styling */
            .table-container { 
                overflow-x: auto; 
                border-radius: var(--radius-md);
                border: 1px solid var(--border-color);
                margin-top: 16px;
            }
            table { 
                width: 100%; 
                text-align: left; 
                border-collapse: collapse; 
            }
            table thead { 
                background-color: var(--bg-secondary); 
            }
            table thead tr { 
                border-bottom: 1px solid var(--border-color); 
            }
            table th { 
                padding: 12px 16px; 
                font-weight: 600; 
                font-size: 14px; 
                color: var(--text-primary);
                text-align: left;
            }
            table tbody tr { 
                border-bottom: 1px solid var(--border-color);
                transition: background-color 0.2s ease;
            }
            table tbody tr:hover {
                background-color: var(--bg-secondary);
            }
            table tbody tr:last-child { 
                border-bottom: 0; 
            }
            table td { 
                padding: 12px 16px; 
                font-size: 14px; 
                color: var(--text-secondary); 
                vertical-align: top; 
            }
            table .font-mono { 
                font-family: 'JetBrains Mono', monospace; 
            }
            table .text-purple { 
                color: var(--purple-600); 
            }
            table .text-red { 
                color: var(--red-600); 
                font-weight: 600; 
            }
            
            /* Response Example Details */
            .response-example {
                width: 100%;
            }
            .response-example summary {
                cursor: pointer;
                padding: 8px 12px;
                background-color: var(--bg-tertiary);
                border-radius: var(--radius-sm);
                font-weight: 500;
                color: var(--accent-primary);
                transition: all 0.2s ease;
                font-size: 13px;
            }
            .response-example summary:hover {
                background-color: var(--bg-secondary);
            }
            .response-example[open] summary {
                margin-bottom: 12px;
                border-radius: var(--radius-sm) var(--radius-sm) 0 0;
            }
            .response-example pre {
                margin: 0;
                padding: 12px;
                background-color: var(--bg-tertiary);
                border-radius: 0 0 var(--radius-sm) var(--radius-sm);
                font-size: 12px;
                line-height: 1.4;
                overflow-x: auto;
                border: 1px solid var(--border-color);
                border-top: none;
            }
            .response-example code {
                color: var(--text-primary);
                font-family: 'JetBrains Mono', monospace;
            }

            #utility-panel .panel-content { 
                padding: 24px; 
            }
            
            .utility-section {
                background-color: var(--bg-card);
                border: 1px solid var(--border-color);
                border-radius: var(--radius-lg);
                margin-bottom: 20px;
                box-shadow: var(--shadow-sm);
                transition: all 0.2s ease;
            }
            .utility-section:hover {
                box-shadow: var(--shadow-md);
                border-color: var(--accent-primary);
            }
            
            .utility-section-header {
                padding: 16px 20px;
                font-weight: 600;
                color: var(--text-primary);
                border-bottom: 1px solid var(--border-color);
                cursor: pointer;
                display: flex;
                justify-content: space-between;
                align-items: center;
                transition: all 0.2s ease;
                border-radius: var(--radius-lg) var(--radius-lg) 0 0;
            }
            .utility-section-header:hover {
                background-color: var(--bg-secondary);
            }
            
            .utility-section-content { 
                padding: 20px; 
            }
            
            /* Collapsible Sections */
            .utility-section.collapsed .utility-section-content {
                display: none;
            }
            .utility-section.collapsed .utility-section-header {
                border-radius: var(--radius-lg);
            }
            .section-chevron {
                transition: transform 0.2s ease;
                font-size: 14px;
                color: var(--text-secondary);
            }
            .utility-section.collapsed .section-chevron {
                transform: rotate(-90deg);
            }
            
            /* Environment Selector */
            .environment-selector {
                margin-bottom: 20px;
            }
            .environment-selector label {
                display: block;
                font-weight: 600;
                color: var(--text-primary);
                margin-bottom: 8px;
                font-size: 14px;
            }
            .environment-selector select {
                width: 100%;
                padding: 12px 16px;
                border: 1px solid var(--border-color);
                border-radius: var(--radius-md);
                background-color: var(--bg-primary);
                color: var(--text-primary);
                font-size: 14px;
                transition: all 0.2s ease;
            }
            .environment-selector select:focus {
                outline: none;
                border-color: var(--accent-primary);
                box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
            }
            
            /* Response Display */
            .response-display {
                background-color: var(--bg-tertiary);
                border: 1px solid var(--border-color);
                border-radius: var(--radius-md);
                padding: 16px;
                margin-top: 16px;
                max-height: 300px;
                overflow-y: auto;
                font-family: 'JetBrains Mono', monospace;
            }
            .response-status {
                display: flex;
                align-items: center;
                gap: 12px;
                margin-bottom: 16px;
                font-weight: 600;
                color: var(--text-primary);
            }
            .status-badge {
                padding: 6px 12px;
                border-radius: var(--radius-sm);
                font-size: 12px;
                font-weight: 600;
                font-family: 'JetBrains Mono', monospace;
            }
            .status-200, .status-201 { background-color: #10b981; color: white; }
            .status-400, .status-404, .status-500 { background-color: #ef4444; color: white; }
            .status-422 { background-color: #f59e0b; color: white; }
            .status-error { background-color: #ef4444; color: white; }
            
            /* Form Styling */
            .form-group { 
                margin-bottom: 16px; 
            }
            .form-group label {
                display: block;
                font-weight: 600;
                margin-bottom: 8px;
                font-size: 14px;
                color: var(--text-primary);
            }
            .form-group input, .form-group textarea {
                width: 100%;
                padding: 12px 16px;
                border: 1px solid var(--border-color);
                border-radius: var(--radius-md);
                background-color: var(--bg-primary);
                color: var(--text-primary);
                font-size: 14px;
                transition: all 0.2s ease;
                box-sizing: border-box;
            }
            .form-group input:focus, .form-group textarea:focus {
                outline: none;
                border-color: var(--accent-primary);
                box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
            }
            .form-group textarea {
                resize: vertical;
                min-height: 120px;
                font-family: 'JetBrains Mono', monospace;
            }
            
            /* Send Request Button */
            .send-request-btn {
                background: linear-gradient(135deg, var(--accent-primary), #2563eb);
                color: white;
                padding: 16px 24px;
                border: none;
                border-radius: var(--radius-md);
                cursor: pointer;
                width: 100%;
                font-weight: 600;
                font-size: 16px;
                transition: all 0.2s ease;
                margin: 20px 0;
                box-shadow: var(--shadow-sm);
            }
            .send-request-btn:hover {
                background: linear-gradient(135deg, #2563eb, #1d4ed8);
                box-shadow: var(--shadow-md);
                transform: translateY(-1px);
            }
            .send-request-btn:active {
                transform: translateY(0);
            }
            /* Language Tabs */
            .language-tabs { 
                display: flex; 
                margin-bottom: 16px; 
                border-bottom: 1px solid var(--border-color);
                background-color: var(--bg-secondary);
                border-radius: var(--radius-md) var(--radius-md) 0 0;
                overflow: hidden;
            }
            .lang-tab {
                padding: 12px 20px;
                border: none;
                background: none;
                color: var(--text-secondary);
                cursor: pointer;
                font-weight: 500;
                font-size: 14px;
                transition: all 0.2s ease;
                position: relative;
            }
            .lang-tab:hover {
                background-color: var(--bg-tertiary);
                color: var(--text-primary);
            }
            .lang-tab.active { 
                color: var(--accent-primary);
                background-color: var(--bg-primary);
                font-weight: 600;
            }
            .lang-tab.active::after {
                content: '';
                position: absolute;
                bottom: 0;
                left: 0;
                right: 0;
                height: 2px;
                background-color: var(--accent-primary);
            }
            
            /* Code Containers */
            #snippet-container, #tester-response-container {
                background-color: var(--bg-tertiary);
                border-radius: var(--radius-md);
                min-height: 150px;
                display: flex;
                align-items: flex-start;
                justify-content: flex-start;
                padding: 16px;
                border: 1px solid var(--border-color);
                font-family: 'JetBrains Mono', monospace;
                overflow: auto;
            }
            #snippet-container pre, #tester-response-container pre {
                margin: 0;
                width: 100%;
                white-space: pre-wrap;
                font-size: 14px;
                line-height: 1.5;
                color: var(--text-primary);
            }
            
            /* Loader Animation */
            .loader {
                border: 4px solid var(--border-color);
                border-radius: 50%;
                border-top: 4px solid var(--accent-primary);
                width: 30px;
                height: 30px;
                animation: spin 1s linear infinite;
                margin: auto;
            }
            @keyframes spin { 
                0% { transform: rotate(0deg); } 
                100% { transform: rotate(360deg); } 
            }
            
            /* Status Colors */
            .status-success { color: #22c55e; }
            .status-error { color: #ef4444; }

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
            .controller-card:hover {
                border-color: var(--primary-color);
                box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1);
                transform: translateY(-2px);
            }
            .controller-header { display: flex; align-items: flex-start; gap: 1rem; margin-bottom: 1rem; }
            .controller-icon {
                width: 40px;
                height: 40px;
                background: var(--slate-100);
                border-radius: 8px;
                display: flex;
                align-items: center;
                justify-content: center;
                color: var(--slate-500);
                flex-shrink: 0;
            }
            .controller-info { flex: 1; min-width: 0; }
            .controller-name { font-size: 1.125rem; font-weight: 600; color: var(--slate-900); margin: 0 0 0.25rem 0; }
            .controller-badge {
                background: var(--primary-color);
                color: #000;
                padding: 0.25rem 0.5rem;
                border-radius: 6px;
                font-size: 0.75rem;
                font-weight: 600;
                flex-shrink: 0;
            }
            .controller-description { color: var(--slate-600); font-size: 0.875rem; margin-bottom: 1rem; }
            .controller-footer { display: flex; justify-content: space-between; align-items: center; }
            .controller-tag {
                font-size: 0.75rem;
                color: var(--slate-500);
                background: var(--slate-100);
                padding: 0.25rem 0.5rem;
                border-radius: 4px;
                font-weight: 500;
            }
            .controller-arrow {
                color: var(--slate-400);
                font-weight: 500;
                opacity: 0;
                transition: opacity 0.2s ease;
            }
            .controller-card:hover .controller-arrow { opacity: 1; }

            .method-get { background-color: rgba(59, 130, 246, 0.1); color: #3B82F6; }
            .method-post { background-color: rgba(16, 185, 129, 0.1); color: #10B981; }
            .method-put { background-color: rgba(245, 158, 11, 0.1); color: #F59E0B; }
            .method-patch { background-color: rgba(239, 68, 68, 0.1); color: #EF4444; }
            .method-delete { background-color: rgba(139, 92, 246, 0.1); color: #8B5CF6; }

            @media (max-width: 1280px) {
                #main-content-wrapper { flex-direction: column; margin-left: 18rem; }
                #utility-panel { position: static; width: 100%; height: auto; }
            }
            @media (max-width: 1024px) {
                #sidebar { position: static; width: 100%; height: auto; }
                #main-content-wrapper { margin-left: 0; }
            }
            """;
    }

    private String getJavaScript() {
        return """
            let currentEndpointId = null;
            let currentTheme = localStorage.getItem('theme') || 'light';

            // Theme Management
            function toggleTheme() {
                currentTheme = currentTheme === 'dark' ? 'light' : 'dark';
                localStorage.setItem('theme', currentTheme);
                document.body.setAttribute('data-theme', currentTheme);
                
                const themeButton = document.querySelector('.theme-toggle');
                themeButton.textContent = currentTheme === 'dark' ? '☀️' : '🌙';
            }

            // Initialize theme on load
            function initializeTheme() {
                document.body.setAttribute('data-theme', currentTheme);
                const themeButton = document.querySelector('.theme-toggle');
                themeButton.textContent = currentTheme === 'dark' ? '☀️' : '🌙';
            }

            document.addEventListener('DOMContentLoaded', () => {
                initializeTheme();
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
                renderPage();
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
                    <!-- Environment Selector -->
                    <div class="environment-selector">
                        <label for="environment-select">Environment</label>
                        <select id="environment-select">
                            <option value="local">🌐 Local Development</option>
                            <option value="staging">🚀 Staging</option>
                            <option value="production">⚡ Production</option>
                        </select>
                    </div>

                    <!-- Authentication Section -->
                    <div class="utility-section" id="auth-section">
                        <div class="utility-section-header" onclick="toggleSection('auth-section')">
                            <span>🔑 Authentication</span>
                            <span class="section-chevron">▼</span>
                        </div>
                        <div class="utility-section-content">
                            <div class="form-group">
                                <label for="auth-token">Bearer Token</label>
                                <input type="text" id="auth-token" placeholder="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." oninput="updateRequestSample()">
                            </div>
                        </div>
                    </div>

                    <!-- Parameters Section -->
                    <div class="utility-section" id="params-section">
                        <div class="utility-section-header" onclick="toggleSection('params-section')">
                            <span>📝 Parameters</span>
                            <span class="section-chevron">▼</span>
                        </div>
                        <div class="utility-section-content">
                            ${pathParamsInputs}
                            ${queryParamsInputs}
                        </div>
                    </div>

                    ${bodyInput}

                    <button class="send-request-btn" onclick="sendRealApiRequest('${endpointId}')">Send API Request</button>

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
                generateSnippet(endpointId, 'CURL');
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
                 let tableRows = responses.map(r => {
                     // Generate example response based on status code
                     let exampleResponse = getExampleResponseForStatus(r.statusCode, r.description);
                     
                     return `
                    <tr>
                        <td class="font-mono status-badge status-${r.statusCode}">${r.statusCode}</td>
                        <td>${r.description || ''}</td>
                        <td>
                            <details class="response-example">
                                <summary>Example</summary>
                                <pre><code>${exampleResponse}</code></pre>
                            </details>
                        </td>
                    </tr>`;
                 }).join('');
                 return `<div class="table-container"><table><thead><tr><th>Status Code</th><th>Description</th><th>Example Response</th></tr></thead><tbody>${tableRows}</tbody></table></div>`;
            }
            
            function getExampleResponseForStatus(statusCode, description) {
                // Return custom examples based on status code and context
                switch (statusCode) {
                    case 200:
                        if (description && description.toLowerCase().includes('user')) {
                            return JSON.stringify({
                                "id": 1,
                                "username": "johndoe",
                                "email": "john@example.com",
                                "fullName": "John Doe",
                                "age": 30,
                                "phoneNumber": "+1234567890",
                                "active": true,
                                "createdAt": "2025-08-22T08:06:23.858732141",
                                "updatedAt": "2025-08-22T08:06:23.858746937"
                            }, null, 2);
                        } else if (description && description.toLowerCase().includes('list')) {
                            return JSON.stringify({
                                "content": [
                                    {
                                        "id": 1,
                                        "username": "johndoe",
                                        "email": "john@example.com",
                                        "fullName": "John Doe",
                                        "active": true
                                    }
                                ],
                                "pageable": {
                                    "pageNumber": 0,
                                    "pageSize": 20
                                },
                                "totalElements": 1,
                                "totalPages": 1
                            }, null, 2);
                        }
                        return JSON.stringify({ "message": "Operation successful", "data": "..." }, null, 2);
                    
                    case 201:
                        return JSON.stringify({
                            "id": 123,
                            "message": "Resource created successfully",
                            "createdAt": new Date().toISOString()
                        }, null, 2);
                    
                    case 204:
                        return "// No content returned";
                    
                    case 400:
                        return JSON.stringify({
                            "error": "Bad Request",
                            "message": "Validation failed",
                            "details": [
                                "Username is required",
                                "Email format is invalid"
                            ]
                        }, null, 2);
                    
                    case 404:
                        return JSON.stringify({
                            "error": "Not Found",
                            "message": "Resource not found",
                            "timestamp": new Date().toISOString()
                        }, null, 2);
                    
                    case 409:
                        return JSON.stringify({
                            "error": "Conflict",
                            "message": "Resource already exists",
                            "conflictingField": "username"
                        }, null, 2);
                    
                    case 422:
                        return JSON.stringify({
                            "error": "Unprocessable Entity",
                            "message": "Validation errors",
                            "errors": {
                                "username": "Username already taken",
                                "email": "Invalid email format"
                            }
                        }, null, 2);
                    
                    case 500:
                        return JSON.stringify({
                            "error": "Internal Server Error",
                            "message": "An unexpected error occurred",
                            "timestamp": new Date().toISOString()
                        }, null, 2);
                    
                    default:
                        return JSON.stringify({
                            "status": statusCode,
                            "message": description || "Response message"
                        }, null, 2);
                }
            }
            
            // Section Toggle Functionality
            function toggleSection(sectionId) {
                const section = document.getElementById(sectionId);
                section.classList.toggle('collapsed');
            }

            // Update Request Sample
            function updateRequestSample() {
                if (currentEndpointId) {
                    generateSnippet(currentEndpointId, document.querySelector('.lang-tab.active')?.dataset.lang || 'cURL');
                }
            }

            // Real API Request Function
            async function sendRealApiRequest(endpointId) {
                const responseContainer = document.getElementById('tester-response-container');
                responseContainer.innerHTML = '<div class="loader"></div>';

                try {
                    const endpoint = getEndpointById(endpointId);
                    const url = buildRequestUrl(endpoint);
                    const options = buildRequestOptions(endpoint);

                    const response = await fetch(url, options);
                    const responseData = await response.text();
                    
                    let parsedData;
                    try {
                        parsedData = JSON.parse(responseData);
                    } catch (e) {
                        parsedData = responseData;
                    }

                    const statusClass = response.ok ? 'status-success' : 'status-error';
                    responseContainer.innerHTML = `
                        <div class="response-status">
                            <span>Status:</span>
                            <span class="status-badge status-${response.status}">${response.status}</span>
                            <span class="${statusClass}">${response.statusText}</span>
                        </div>
                        <div class="response-display">
                            <pre><code>${JSON.stringify(parsedData, null, 2)}</code></pre>
                        </div>
                    `;
                } catch (error) {
                    responseContainer.innerHTML = `
                        <div class="response-status">
                            <span>Status:</span>
                            <span class="status-badge status-error">Error</span>
                            <span class="status-error">Network Error</span>
                        </div>
                        <div class="response-display">
                            <pre><code>${JSON.stringify({ error: error.message }, null, 2)}</code></pre>
                        </div>
                    `;
                }
            }

            // Build Request URL with Parameters
            function buildRequestUrl(endpoint) {
                const environment = document.getElementById('environment-select')?.value || 'local';
                let baseUrl;
                
                // Handle different environments
                if (environment === 'local') {
                    // For local development, detect if we're in docs mode or actual API mode
                    const currentPort = window.location.port;
                    if (currentPort === '8085' || window.location.href.includes(':8085')) {
                        // We're already on the API server
                        baseUrl = window.location.origin;
                    } else {
                        // We're viewing docs, need to point to API server
                        baseUrl = window.location.protocol + '//' + window.location.hostname + ':8085';
                    }
                } else if (environment === 'staging') {
                    baseUrl = 'https://staging-api.example.com';
                } else if (environment === 'production') {
                    baseUrl = 'https://api.example.com';
                } else {
                    // Default fallback
                    baseUrl = window.location.origin;
                }
                
                let url = baseUrl + endpoint.url;
                
                // Replace path parameters
                if (endpoint.pathVariables) {
                    endpoint.pathVariables.forEach(param => {
                        const input = document.getElementById(`tester-${param.name}-${currentEndpointId}`);
                        if (input && input.value) {
                            url = url.replace(`{${param.name}}`, input.value);
                        }
                    });
                }
                
                // Add query parameters
                const queryParams = new URLSearchParams();
                if (endpoint.queryParameters) {
                    endpoint.queryParameters.forEach(param => {
                        const input = document.getElementById(`tester-${param.name}-${currentEndpointId}`);
                        if (input && input.value) {
                            queryParams.append(param.name, input.value);
                        }
                    });
                }
                
                if (queryParams.toString()) {
                    url += '?' + queryParams.toString();
                }
                
                return url;
            }

            // Build Request Options
            function buildRequestOptions(endpoint) {
                const options = {
                    method: endpoint.httpMethod,
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json'
                    }
                };

                // Add auth token
                const token = document.getElementById('auth-token').value;
                if (token) {
                    options.headers['Authorization'] = `Bearer ${token}`;
                }

                // Add request body
                if (endpoint.requestBody && (endpoint.httpMethod === 'POST' || endpoint.httpMethod === 'PUT' || endpoint.httpMethod === 'PATCH')) {
                    const bodyInput = document.getElementById(`tester-body-${currentEndpointId}`);
                    if (bodyInput && bodyInput.value.trim()) {
                        try {
                            options.body = JSON.stringify(JSON.parse(bodyInput.value));
                        } catch (e) {
                            options.body = bodyInput.value;
                        }
                    }
                }

                return options;
            }

            async function sendApiRequest(endpointId) {
                // Fallback to real API request
                return sendRealApiRequest(endpointId);
            }

            async function generateSnippet(endpointId, language) {
                const snippetContainer = document.getElementById('snippet-container');
                if (!snippetContainer) return;
                snippetContainer.innerHTML = '<div class="loader"></div>';

                const endpoint = getEndpointById(endpointId);
                if (!endpoint) {
                    snippetContainer.innerHTML = `<pre><code>Error: Endpoint data not found.</code></pre>`;
                    return;
                }

                const prompt = `
                    You are an expert API documentation assistant. Generate a concise, runnable client-side code snippet.
                    Instructions:
                    - Language: ${language}.
                    - API Endpoint: ${endpoint.httpMethod} ${endpoint.url}
                    - Path Parameters: ${JSON.stringify(endpoint.pathVariables || [])}
                    - Query Parameters: ${JSON.stringify(endpoint.queryParameters || [])}
                    - Request Body Example: ${endpoint.requestBody ? endpoint.requestBody.example : 'None'}
                    - Replace placeholders like '{id}' with example values like '123'.
                    - Include necessary headers, like 'Content-Type: application/json'.
                    - **IMPORTANT: Only output the raw code. No markdown, no explanations, just the code.**
                `;
                
                // Static code generation - no API dependency
                let code = generateStaticCodeSnippet(endpoint, language);
                snippetContainer.innerHTML = `<pre><code>${code.replace(/</g, "&lt;").replace(/>/g, "&gt;")}</code></pre>`;
            }

            function generateStaticCodeSnippet(endpoint, language) {
                let code = '';
                const environment = document.getElementById('environment-select')?.value || 'local';
                let baseUrl;
                
                // Use same logic as buildRequestUrl for consistency
                if (environment === 'local') {
                    const currentPort = window.location.port;
                    if (currentPort === '8085' || window.location.href.includes(':8085')) {
                        baseUrl = window.location.origin;
                    } else {
                        baseUrl = window.location.protocol + '//' + window.location.hostname + ':8085';
                    }
                } else if (environment === 'staging') {
                    baseUrl = 'https://staging-api.example.com';
                } else if (environment === 'production') {
                    baseUrl = 'https://api.example.com';
                } else {
                    baseUrl = window.location.origin;
                }
                
                let url = baseUrl + endpoint.url;
                
                // Replace path variables with actual input values or examples
                if (endpoint.pathVariables) {
                    endpoint.pathVariables.forEach(param => {
                        const input = document.getElementById(`tester-${param.name}-${currentEndpointId}`);
                        const value = input?.value || (param.type === 'Long' || param.type === 'Integer' ? '123' : 'example');
                        url = url.replace(`{${param.name}}`, value);
                    });
                }
                
                // Add query parameters with actual input values
                if (endpoint.queryParameters && endpoint.queryParameters.length > 0) {
                    const queryParams = endpoint.queryParameters.map(param => {
                        const input = document.getElementById(`tester-${param.name}-${currentEndpointId}`);
                        const value = input?.value || (param.type === 'Long' || param.type === 'Integer' ? '123' : 'example');
                        return `${param.name}=${value}`;
                    }).filter(param => !param.endsWith('='));
                    
                    if (queryParams.length > 0) {
                        url += (url.includes('?') ? '&' : '?') + queryParams.join('&');
                    }
                }
                
                // Get actual auth token
                const authToken = document.getElementById('auth-token')?.value || 'YOUR_TOKEN_HERE';
                
                switch(language) {
                    case 'cURL':
                        code = `curl -X ${endpoint.httpMethod} "${url}" \\\\
  -H "Content-Type: application/json" \\\\
  -H "Authorization: Bearer ${authToken}"`;
                        if (endpoint.requestBody) {
                            const bodyInput = document.getElementById(`tester-body-${currentEndpointId}`);
                            const bodyContent = bodyInput?.value || endpoint.requestBody.example || '{}';
                            code += ` \\\\
  -d '${bodyContent}'`;
                        }
                        break;
                        
                    case 'JavaScript':
                        code = `fetch("${url}", {
  method: "${endpoint.httpMethod}",
  headers: {
    "Content-Type": "application/json",
    "Authorization": "Bearer ${authToken}"
  }`;
                        if (endpoint.requestBody) {
                            const bodyInput = document.getElementById(`tester-body-${currentEndpointId}`);
                            const bodyContent = bodyInput?.value || endpoint.requestBody.example || '{}';
                            code += `,
  body: JSON.stringify(${bodyContent})`;
                        }
                        code += `
})
.then(response => response.json())
.then(data => console.log(data))
.catch(error => console.error('Error:', error));`;
                        break;
                        
                    case 'Python':
                        code = `import requests

url = "${url}"
headers = {
    "Content-Type": "application/json",
    "Authorization": "Bearer ${authToken}"
}`;
                        if (endpoint.requestBody) {
                            const bodyInput = document.getElementById(`tester-body-${currentEndpointId}`);
                            const bodyContent = bodyInput?.value || endpoint.requestBody.example || '{}';
                            code += `
data = ${bodyContent}

response = requests.${endpoint.httpMethod.toLowerCase()}(url, headers=headers, json=data)`;
                        } else {
                            code += `

response = requests.${endpoint.httpMethod.toLowerCase()}(url, headers=headers)`;
                        }
                        code += `
print(f"Status Code: {response.status_code}")
print(f"Response: {response.json()}")`;
                        break;
                        
                    default:
                        code = `// Code snippet for ${language} not available`;
                }
                
                return code;
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