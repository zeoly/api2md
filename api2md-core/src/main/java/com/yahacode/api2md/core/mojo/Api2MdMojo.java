package com.yahacode.api2md.core.mojo;

import com.thoughtworks.qdox.JavaProjectBuilder;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
        getLog().info("files: " + files.toString());
        JavaProjectBuilder builder = new JavaProjectBuilder();
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
        } else {
            return Arrays.asList(file);
        }
    }
}
