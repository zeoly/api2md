package com.yahacode.api2md.core.mojo;

import com.alibaba.fastjson.JSON;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.yahacode.api2md.core.mojo.model.ContentClass;
import com.yahacode.api2md.core.writer.MarkdownWriter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
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
        getLog().info("server domain: " + host + ":" + port);

        List<String> roots = project.getCompileSourceRoots();
        getLog().info("root: " + roots.toString());
        List<File> files = new LinkedList<>();
        for (String root : roots) {
            File f = new File(root);
            files.addAll(getFiles(f));
        }

        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.setEncoding("utf-8");
        try {
            for (File file : files) {
                getLog().info("java file found: " + file.toString());
                builder.addSource(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Collection<JavaClass> classes = builder.getClasses();
        List<ContentClass> analysableContents = new LinkedList<>();
        for (JavaClass javaClass : classes) {
            if (AnnotationUtils.isAnalysable(javaClass)) {
                getLog().info("analysable class found: " + javaClass.getName());
                ContentClass contentClass = AnnotationUtils.parseController(javaClass);
                analysableContents.add(contentClass);
            }
        }

        String content = "";
        for (ContentClass contentClass : analysableContents) {
            getLog().info("文档数据:" + JSON.toJSONString(contentClass));
            content += MarkdownWriter.writeClass(contentClass);
        }

        File file = new File(project.getBasedir() + File.separator + "doc" + File.separator + "doc.md");
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
            out.write(content);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
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
