package com.yahacode.api2md.core.writer;

import com.yahacode.api2md.core.mojo.model.ContentClass;
import com.yahacode.api2md.core.mojo.model.ContentMethod;
import com.yahacode.api2md.core.mojo.model.ContentParam;
import com.yahacode.api2md.core.mojo.model.ContentReturn;

/**
 * @author zengyongli 2021-02-26
 */
public class MarkdownWriter {

    private static final String NEW_LINE = "\n";

    public static String writeClass(ContentClass contentClass) {
        StringBuilder sb = new StringBuilder();
        sb.append("# ").append(contentClass.getClassName()).append(NEW_LINE);
        sb.append(NEW_LINE);
        sb.append("> ").append(contentClass.getComment()).append(NEW_LINE);
        sb.append("> 作者：").append(contentClass.getAuthor()).append(NEW_LINE);
        sb.append(NEW_LINE);
        sb.append("## 接口").append(NEW_LINE);
        sb.append(NEW_LINE);
        for (ContentMethod contentMethod : contentClass.getMethodList()) {
            sb.append(writeMethod(contentMethod)).append(NEW_LINE);
        }
        sb.append(NEW_LINE);
        sb.append("## 附录").append(NEW_LINE);
        sb.append(NEW_LINE);
        sb.append("<h3 id=\"ref\">test</h3>").append(NEW_LINE);
        return sb.toString();
    }

    public static String writeMethod(ContentMethod contentMethod) {
        StringBuilder sb = new StringBuilder();
        sb.append("### ").append(contentMethod.getMethod()).append(" ").append(contentMethod.getUri()).append(NEW_LINE);
        sb.append(NEW_LINE);
        sb.append("> ").append(contentMethod.getComment()).append(NEW_LINE);
        sb.append(NEW_LINE);
        sb.append("**请求参数说明**").append(NEW_LINE);
        sb.append(NEW_LINE);
        if (contentMethod.getParamList() != null && contentMethod.getParamList().size() > 0) {
            sb.append("字段名|字段类型|描述").append(NEW_LINE);
            sb.append("-|-|-").append(NEW_LINE);
            for (ContentParam contentParam : contentMethod.getParamList()) {
                sb.append(contentParam.getName()).append("|").append(contentParam.getType()).append("|").append(contentParam.getComment()).append(NEW_LINE);
            }
        } else {
            sb.append("无").append(NEW_LINE);
        }
        sb.append(NEW_LINE);
        sb.append("**响应数据类型**").append(NEW_LINE);
        sb.append(NEW_LINE);
        ContentReturn contentReturn = contentMethod.getContentReturn();
        if ("void".equals(contentReturn.getReturnType())) {
            sb.append("无");
        } else {
            sb.append("响应类型|描述").append(NEW_LINE);
            sb.append("-|-").append(NEW_LINE);
            sb.append("[").append(contentReturn.getReturnType()).append("](#ref)").append("|").append(contentReturn.getComment());
        }
        sb.append(NEW_LINE);
        return sb.toString();
    }
}
