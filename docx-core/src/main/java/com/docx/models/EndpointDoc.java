package com.docx.models;

import java.util.List;
import java.util.Map;

public class EndpointDoc {
    private String name;
    private String description;
    private String httpMethod;
    private String url;
    private List<ParameterDoc> parameters;
    private List<ParameterDoc> pathVariables;
    private List<ParameterDoc> queryParameters;
    private ParameterDoc requestBody;
    private ResponseDoc responseBody;
    private List<ResponseDoc> responses;
    private List<String> examples;
    private List<String> tags;
    private boolean deprecated;
    private String apiNote;
    private String apiDescription;

    public EndpointDoc() {}

    public EndpointDoc(String name, String httpMethod, String url) {
        this.name = name;
        this.httpMethod = httpMethod;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<ParameterDoc> getParameters() {
        return parameters;
    }

    public void setParameters(List<ParameterDoc> parameters) {
        this.parameters = parameters;
    }

    public List<ParameterDoc> getPathVariables() {
        return pathVariables;
    }

    public void setPathVariables(List<ParameterDoc> pathVariables) {
        this.pathVariables = pathVariables;
    }

    public List<ParameterDoc> getQueryParameters() {
        return queryParameters;
    }

    public void setQueryParameters(List<ParameterDoc> queryParameters) {
        this.queryParameters = queryParameters;
    }

    public ParameterDoc getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(ParameterDoc requestBody) {
        this.requestBody = requestBody;
    }

    public ResponseDoc getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(ResponseDoc responseBody) {
        this.responseBody = responseBody;
    }

    public List<ResponseDoc> getResponses() {
        return responses;
    }

    public void setResponses(List<ResponseDoc> responses) {
        this.responses = responses;
    }

    public List<String> getExamples() {
        return examples;
    }

    public void setExamples(List<String> examples) {
        this.examples = examples;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public String getApiNote() {
        return apiNote;
    }

    public void setApiNote(String apiNote) {
        this.apiNote = apiNote;
    }

    public String getApiDescription() {
        return apiDescription;
    }

    public void setApiDescription(String apiDescription) {
        this.apiDescription = apiDescription;
    }
}