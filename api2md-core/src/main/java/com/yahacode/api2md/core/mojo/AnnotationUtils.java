package com.yahacode.api2md.core.mojo;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.yahacode.api2md.core.consts.Tag;
import com.yahacode.api2md.core.mojo.model.ContentClass;
import com.yahacode.api2md.core.mojo.model.ContentMethod;
import com.yahacode.api2md.core.mojo.model.ContentParam;
import com.yahacode.api2md.core.mojo.model.ContentReturn;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    public static boolean isAnalysable(JavaClass javaClass) {
        if (javaClass.getPackageName().contains("model")) {
            return true;
        }
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
            } else if (javaAnnotation.getType().isA(getFullQualifyName(GetMapping.class))) {
                return true;
            } else if (javaAnnotation.getType().isA(getFullQualifyName(PostMapping.class))) {
                return true;
            } else if (javaAnnotation.getType().isA(getFullQualifyName(DeleteMapping.class))) {
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
        DocletTag tag = javaClass.getTagByName(Tag.AUTHOR);
        if (tag != null) {
            System.out.println("controller author: " + tag.getValue());
            contentClass.setAuthor(tag.getValue());
        }

        contentClass.setBaseUri(parseControllerBaseUrl(javaClass));

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
        String comment = parseMethodComment(javaMethod);
        System.out.println("api comment: " + comment);
        contentMethod.setComment(comment);
        contentMethod.setContentReturn(parseMethodReturn(javaMethod));
        List<DocletTag> paramTags = javaMethod.getTagsByName(Tag.PARAM);
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
                System.out.println("path: " + path);
                contentMethod.setMethod(method.replace("RequestMethod.", ""));
                if (path == null) {
                    path = "";
                } else {
                    path = path.replace("\"", "");
                    if (!path.startsWith("/")) {
                        path = "/" + path;
                    }
                }
                System.out.println("final path: " + path);
                contentMethod.setUri(path);
            } else if (javaAnnotation.getType().isA(getFullQualifyName(GetMapping.class)) || javaAnnotation.getType().isA(getFullQualifyName(PostMapping.class)) || javaAnnotation.getType().isA(getFullQualifyName(DeleteMapping.class))) {
                String method = javaAnnotation.getType().getName();
                String path = (String) javaAnnotation.getNamedParameter("value");
                System.out.println("path: " + path);
                contentMethod.setMethod(method.replace("Mapping", "").toUpperCase());
                if (path == null) {
                    path = "";
                } else {
                    path = path.replace("\"", "");
                    if (!path.startsWith("/")) {
                        path = "/" + path;
                    }
                }
                System.out.println("final path: " + path);
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

    public static String parseControllerBaseUrl(JavaClass javaClass) {
        for (JavaAnnotation javaAnnotation : javaClass.getAnnotations()) {
            if (javaAnnotation.getType().isA(getFullQualifyName(RequestMapping.class))) {
                String path = (String) javaAnnotation.getNamedParameter("value");
                if (path != null) {
                    return path.replace("\"", "");
                }
            }
        }
        return "";
    }

    public static String parseMethodComment(JavaMethod javaMethod) {
        String comment = javaMethod.getComment();
        if (StringUtils.isEmpty(comment)) {
            String methodName = javaMethod.getName();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < methodName.length(); i++) {
                char ch = methodName.charAt(i);
                if (Character.isUpperCase(ch)) {
                    sb.append(" ").append(Character.toLowerCase(ch));
                } else {
                    sb.append(ch);
                }
            }
            comment = sb.toString();
        }
        return comment;
    }

    public static ContentReturn parseMethodReturn(JavaMethod javaMethod) {
        ContentReturn contentReturn = new ContentReturn();
        contentReturn.setReturnType(javaMethod.getReturnType().getGenericValue());
        DocletTag returnTag = javaMethod.getTagByName(Tag.RETURN);
        if (returnTag != null) {
            contentReturn.setComment(returnTag.getValue());
        }
        return contentReturn;
    }

    public static void main(String[] args) {
        String a = "/{id}/role";
        System.out.println(!a.startsWith("/"));
    }
}
