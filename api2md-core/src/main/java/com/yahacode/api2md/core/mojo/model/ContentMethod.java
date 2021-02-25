package com.yahacode.api2md.core.mojo.model;

import java.util.List;

/**
 * @author zengyongli 2021-02-24
 */
public class ContentMethod {

    String method;

    String uri;

    String comment;

    List<ContentParam> paramList;

    String returnType;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<ContentParam> getParamList() {
        return paramList;
    }

    public void setParamList(List<ContentParam> paramList) {
        this.paramList = paramList;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }
}
