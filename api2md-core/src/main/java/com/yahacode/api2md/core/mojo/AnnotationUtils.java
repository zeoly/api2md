package com.yahacode.api2md.core.mojo;

import com.alibaba.fastjson.JSON;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.yahacode.api2md.core.mojo.model.ContentClass;
import com.yahacode.api2md.core.mojo.model.ContentMethod;
import com.yahacode.api2md.core.mojo.model.ContentParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zengyongli 2020-12-16
 */
public class AnnotationUtils {

    public static boolean isController(JavaClass javaClass) {
        for (JavaAnnotation javaAnnotation : javaClass.getAnnotations()) {
            if (javaAnnotation.getType().isA(getFullQualifyName(RestController.class))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isApi(JavaMethod javaMethod) {
        for (JavaAnnotation javaAnnotation : javaMethod.getAnnotations()) {
            if (javaAnnotation.getType().isA(getFullQualifyName(RequestMapping.class))) {
                return true;
            }
        }
        return false;
    }

    public static String getFullQualifyName(Class clazz) {
        return clazz.getPackage().getName() + "." + clazz.getSimpleName();
    }

    public static ContentClass parseController(JavaClass javaClass) {
        ContentClass contentClass = new ContentClass();
        System.out.println("controller comment: " + javaClass.getComment());
        contentClass.setClassName(javaClass.getName());
        contentClass.setComment(javaClass.getComment());
        DocletTag tag = javaClass.getTagByName("author");
        if (tag != null) {
            System.out.println("controller author: " + tag.getValue());
            contentClass.setAuthor(tag.getValue());
        }

        for (JavaAnnotation javaAnnotation : javaClass.getAnnotations()) {
            if (javaAnnotation.getType().isA(getFullQualifyName(RequestMapping.class))) {
                String path = (String) javaAnnotation.getNamedParameter("value");
                if (path != null) {
                    path = path.replace("\"", "");
                    contentClass.setBaseUri(path);
                }
            }
        }

        List<JavaMethod> javaMethods = javaClass.getMethods();
        List<ContentMethod> methodList = new ArrayList<>();
        for (JavaMethod javaMethod : javaMethods) {
            if (isApi(javaMethod)) {
                System.out.println("api method found: " + javaMethod.getName());
                ContentMethod contentMethod = parseApiMethod(javaMethod);
                contentMethod.setUri(contentClass.getBaseUri() + contentMethod.getUri());
                methodList.add(contentMethod);
            }
        }
        contentClass.setMethodList(methodList);
        return contentClass;
    }

    public static ContentMethod parseApiMethod(JavaMethod javaMethod) {
        ContentMethod contentMethod = new ContentMethod();
        String comment = javaMethod.getComment();
        System.out.println("api comment: " + comment);
        contentMethod.setComment(comment);
        contentMethod.setReturnType(javaMethod.getReturnType().getGenericValue());
        List<DocletTag> paramTags = javaMethod.getTagsByName("param");
        Map<String, String> paramMap = new HashMap<>();
        if (paramTags != null && paramTags.size() > 0) {
            for (DocletTag tag : paramTags) {
                List<String> params = tag.getParameters();
                String name = params.remove(0);
                System.out.println("api param tag: " + name + ", " + params.toString());
                paramMap.put(name, String.join(" ", params));
            }
        }

        for (JavaAnnotation javaAnnotation : javaMethod.getAnnotations()) {
            if (javaAnnotation.getType().isA(getFullQualifyName(RequestMapping.class))) {
                String method = (String) javaAnnotation.getNamedParameter("method");
                String path = (String) javaAnnotation.getNamedParameter("value");
                contentMethod.setMethod(method.replace("RequestMethod.", ""));
                if (path == null) {
                    path = "";
                } else if (!path.startsWith("/")) {
                    path = "/" + path;
                }
                path = path.replace("\"", "");
                contentMethod.setUri(path);
            }
        }

        List<JavaParameter> javaParameters = javaMethod.getParameters();
        List<ContentParam> paramList = new ArrayList<>();
        for (JavaParameter javaParameter : javaParameters) {
            System.out.println("api param define: " + javaParameter.getName() + ", " + javaParameter.getType().getFullyQualifiedName());
            ContentParam contentParam = new ContentParam();
            contentParam.setName(javaParameter.getName());
            contentParam.setComment(paramMap.get(javaParameter.getName()));
            contentParam.setType(javaParameter.getType().getGenericValue());
            paramList.add(contentParam);
        }
        contentMethod.setParamList(paramList);
        return contentMethod;
    }
}
