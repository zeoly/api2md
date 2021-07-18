package com.yahacode.api2md.core.mojo.model;

import java.util.List;

/**
 * @author zengyongli 2021-07-16
 */
public class ContentField {

    public String fieldType;

    public String fieldName;

    public String comment;

    public List<ContentField> subFields;

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<ContentField> getSubFields() {
        return subFields;
    }

    public void setSubFields(List<ContentField> subFields) {
        this.subFields = subFields;
    }
}
