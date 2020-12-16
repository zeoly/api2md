package com.yahacode.api2md.core.mojo;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author zengyongli 2020-12-15
 */
@Mojo(name = "api2md")
public class Api2MdMojo extends AbstractMojo {

    @Parameter(property = "project")
    private MavenProject project;

    @Parameter(property = "api2md.host", defaultValue = "localhost")
    private String host;

    @Parameter(property = "api2md.port", defaultValue = "80")
    private String port;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("start generate markdown");
        getLog().info("domain: " + host + ":" + port);

        List<String> roots = project.getCompileSourceRoots();
        getLog().info("root: " + roots.toString());
        List<File> files = new LinkedList<>();
        for (String root : roots) {
            File f = new File(root);
            files.addAll(getFiles(f));
        }

        JavaProjectBuilder builder = new JavaProjectBuilder();
        try {
            for (File file : files) {
                getLog().info("java file found: " + file.toString());
                builder.addSource(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Collection<JavaClass> classes = builder.getClasses();
        for (JavaClass javaClass : classes) {
            if (AnnotationUtils.isController(javaClass)) {
                getLog().info("controller found: " + javaClass.getName());
                AnnotationUtils.parseController(javaClass);
            }
        }
    }

    private List<File> getFiles(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            List<File> result = new LinkedList<>();
            if (files.length == 0) {
                return result;
            }
            for (File f : files) {
                result.addAll(getFiles(f));
            }
            return result;
        } else if (file.getName().endsWith(".java")) {
            return Arrays.asList(file);
        } else {
            return new ArrayList<>();
        }
    }

}
