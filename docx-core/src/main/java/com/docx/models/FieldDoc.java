package com.docx.models;

import java.util.List;

public class FieldDoc {
    private String name;
    private String type;
    private String description;
    private boolean required;
    private String defaultValue;
    private String example;
    private List<ValidationConstraint> constraints;

    public FieldDoc() {}

    public FieldDoc(String name, String type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public List<ValidationConstraint> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<ValidationConstraint> constraints) {
        this.constraints = constraints;
    }
}