package com.yahacode.api2md.core.model;

import java.util.List;

public class DocMethod {

    String comment;

    String requestMethod;

    String url;

    List<DocParameter> requestParams;

    DocReturn docReturn;
}
