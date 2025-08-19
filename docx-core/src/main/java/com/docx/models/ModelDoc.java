package com.docx.models;

import java.util.List;

public class ModelDoc {
    private String name;
    private String className;
    private String description;
    private String packageName;
    private List<FieldDoc> fields;
    private List<String> annotations;

    public ModelDoc() {}

    public ModelDoc(String name, String className) {
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

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<FieldDoc> getFields() {
        return fields;
    }

    public void setFields(List<FieldDoc> fields) {
        this.fields = fields;
    }

    public List<String> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<String> annotations) {
        this.annotations = annotations;
    }
}