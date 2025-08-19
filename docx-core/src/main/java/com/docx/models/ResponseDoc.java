package com.docx.models;

public class ResponseDoc {
    private int statusCode;
    private String description;
    private String type;
    private String example;
    private String mediaType;

    public ResponseDoc() {}

    public ResponseDoc(int statusCode, String description) {
        this.statusCode = statusCode;
        this.description = description;
    }

    public ResponseDoc(int statusCode, String description, String type) {
        this.statusCode = statusCode;
        this.description = description;
        this.type = type;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
}