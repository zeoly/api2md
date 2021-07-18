package com.yahacode.api2md.core.mojo.model;

/**
 * @author zengyongli 2021-06-21
 */
public class ContentReturn {

    private String returnType;

    private String comment;

    private Boolean list = false;

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getList() {
        return list;
    }

    public void setList(Boolean list) {
        this.list = list;
    }
}
