package com.docx.models;

import java.util.List;

public class ControllerDoc {
    private String name;
    private String className;
    private String description;
    private String baseUrl;
    private String author;
    private String since;
    private String version;
    private List<String> tags;
    private List<EndpointDoc> endpoints;

    public ControllerDoc() {}

    public ControllerDoc(String name, String className) {
        this.name = name;
        this.className = className;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSince() {
        return since;
    }

    public void setSince(String since) {
        this.since = since;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<EndpointDoc> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<EndpointDoc> endpoints) {
        this.endpoints = endpoints;
    }
}