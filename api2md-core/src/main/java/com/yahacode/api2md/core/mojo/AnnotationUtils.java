package com.yahacode.api2md.core.mojo;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    public static void parseController(JavaClass javaClass) {
        System.out.println("controller comment: " + javaClass.getComment());
        DocletTag tag = javaClass.getTagByName("author");
        if (tag != null) {
            System.out.println("controller author: " + tag.getValue());

        }
        List<JavaMethod> javaMethods = javaClass.getMethods();
        for (JavaMethod javaMethod : javaMethods) {
            if (isApi(javaMethod)) {
                System.out.println("api method found: " + javaMethod.getName());
                parseApiMethod(javaMethod);
            }
        }
    }

    public static void parseApiMethod(JavaMethod javaMethod) {
        System.out.println("api comment: " + javaMethod.getComment());
        List<DocletTag> paramTags = javaMethod.getTagsByName("param");
        if (paramTags != null && paramTags.size() > 0) {
            for (DocletTag tag : paramTags) {
                List<String> params = tag.getParameters();
                String name = params.remove(0);
                System.out.println("api param tag: " + name + ", " + params.toString());
            }
        }
        List<JavaParameter> javaParameters = javaMethod.getParameters();
        for (JavaParameter javaParameter : javaParameters) {
            System.out.println("api param define: " + javaParameter.getName() + ", " + javaParameter.getType().getFullyQualifiedName());
        }
    }
}
