package com.docx.models;

import java.util.List;
import java.util.Map;

public class ApiDocumentation {
    private String title;
    private String version;
    private String description;
    private String baseUrl;
    private List<ControllerDoc> controllers;
    private List<ModelDoc> models;
    private Map<String, String> configuration;

    public ApiDocumentation() {}

    public ApiDocumentation(String title, String version, String description) {
        this.title = title;
        this.version = version;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public List<ControllerDoc> getControllers() {
        return controllers;
    }

    public void setControllers(List<ControllerDoc> controllers) {
        this.controllers = controllers;
    }

    public List<ModelDoc> getModels() {
        return models;
    }

    public void setModels(List<ModelDoc> models) {
        this.models = models;
    }

    public Map<String, String> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Map<String, String> configuration) {
        this.configuration = configuration;
    }
}