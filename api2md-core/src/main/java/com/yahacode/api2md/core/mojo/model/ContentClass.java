package com.yahacode.api2md.core.mojo.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengyongli 2021-02-24
 */
public class ContentClass {

    private String className;

    private String comment;

    private String author;

    private String baseUri;

    private List<ContentField> fieldList = new ArrayList<>();

    private List<ContentMethod> methodList;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

    public List<ContentField> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<ContentField> fieldList) {
        this.fieldList = fieldList;
    }

    public List<ContentMethod> getMethodList() {
        return methodList;
    }

    public void setMethodList(List<ContentMethod> methodList) {
        this.methodList = methodList;
    }
}
