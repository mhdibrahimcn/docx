package com.docx.config;

/**
 * Configuration class for customizing the API documentation template.
 * This class provides options to customize the appearance and behavior
 * of the generated HTML documentation.
 */
public class TemplateConfig {
    
    private String primaryColor = "#FFD700"; // Gold color as default
    private String title = "API Documentation";
    private String logoText = "D";
    private boolean enableApiTester = true;
    private boolean enableCodeGeneration = true;
    private String geminiApiKey = "";
    private boolean enableSearch = true;
    private boolean enableDarkMode = false;
    
    // Language options for code generation
    private String[] supportedLanguages = {"cURL", "JavaScript", "Python", "Java", "PHP"};
    
    public TemplateConfig() {}
    
    public String getPrimaryColor() {
        return primaryColor;
    }
    
    public void setPrimaryColor(String primaryColor) {
        this.primaryColor = primaryColor;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getLogoText() {
        return logoText;
    }
    
    public void setLogoText(String logoText) {
        this.logoText = logoText;
    }
    
    public boolean isEnableApiTester() {
        return enableApiTester;
    }
    
    public void setEnableApiTester(boolean enableApiTester) {
        this.enableApiTester = enableApiTester;
    }
    
    public boolean isEnableCodeGeneration() {
        return enableCodeGeneration;
    }
    
    public void setEnableCodeGeneration(boolean enableCodeGeneration) {
        this.enableCodeGeneration = enableCodeGeneration;
    }
    
    public String getGeminiApiKey() {
        return geminiApiKey;
    }
    
    public void setGeminiApiKey(String geminiApiKey) {
        this.geminiApiKey = geminiApiKey;
    }
    
    public boolean isEnableSearch() {
        return enableSearch;
    }
    
    public void setEnableSearch(boolean enableSearch) {
        this.enableSearch = enableSearch;
    }
    
    public boolean isEnableDarkMode() {
        return enableDarkMode;
    }
    
    public void setEnableDarkMode(boolean enableDarkMode) {
        this.enableDarkMode = enableDarkMode;
    }
    
    public String[] getSupportedLanguages() {
        return supportedLanguages;
    }
    
    public void setSupportedLanguages(String[] supportedLanguages) {
        this.supportedLanguages = supportedLanguages;
    }
}