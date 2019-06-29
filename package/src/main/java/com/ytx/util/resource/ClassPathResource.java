package com.ytx.util.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by chutao on 2017/8/10.
 */
public class ClassPathResource implements Resource {
    private URL url;
    private String classPath;
    private InputStream in;

    private ClassPathResource(String classPath,URL url,InputStream in){
        this.classPath = classPath;
        this.url = url;
        this.in = in;
    }

    /**
     * 如果找不到该资源，返回null
     * @param classPath
     * @return
     */
    public static ClassPathResource getInstance(String classPath){
        Objects.requireNonNull(classPath);
        URL url = getResource(classPath);
        InputStream in = getResourceAsStream(classPath);
        if(url == null || in == null)
            return null;
        return new ClassPathResource(classPath,url,in);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return in;
    }

    /***
     * 如果本资源不是文件夹，或者文件夹下没有子资源，返回的集合为空集合
     *
     * 必须同时兼容运行环境和jar包，如果是jar包，采用JarFile的方式读取，如果是运行环境，可以依然使用下面的方式处理,
     * 该方式目前无法在jar包中生效的原因如下：
     * https://bugs.openjdk.java.net/browse/JDK-8144977
     * @return
     * @throws IOException
     */
    @Override
    public List<Resource> children() throws IOException{
        //判断当前资源是否在jar包中
        boolean isJar = isJar();
        List<String> childClassPaths;
        if(isJar){
            childClassPaths = childrenClassPathInJar();
        }else{
            childClassPaths = childrenClassPathNotJar();
        }
        List<Resource> children = new ArrayList<>();
        for(String childClassPath : childClassPaths){
            ClassPathResource child = ClassPathResource.getInstance(childClassPath);
            if(child != null) {
                children.add(child);
            }
        }
        return children;
    }

    private List<String> childrenClassPathNotJar() throws IOException{
        List<String> childrenFileNames = getResourceFiles(in);
        List<String> childrenClassPaths = new ArrayList<>();
        for(String childFileName : childrenFileNames){
            String childClassPath = classPath + "/" + childFileName;
            childrenClassPaths.add(childClassPath);
        }
        return childrenClassPaths;
    }

    private List<String> childrenClassPathInJar() throws IOException,MalformedURLException{
        List<String> childrenClassPaths = new ArrayList<>();
        String jarPath = url.toString().substring(0, url.toString().indexOf("!/") + 2);
        URL jarURL = new URL(jarPath);
        JarURLConnection jarCon = (JarURLConnection) jarURL.openConnection();
        JarFile jarFile = jarCon.getJarFile();
        Enumeration<JarEntry> jarEntrys = jarFile.entries();
        while (jarEntrys.hasMoreElements()) {
            JarEntry entry = jarEntrys.nextElement();
            //如果本资源是文件夹，排除本文件夹自身
            if(entry.getName().startsWith(classPath)
                    && !entry.getName().equals(classPath + "/")
                    && !entry.getName().equals(classPath)
                    ) {
                childrenClassPaths.add(entry.getName());
            }
        }
        return childrenClassPaths;
    }

    private boolean isJar(){
        return url.toString().startsWith("jar");
    }

    private static List<String> getResourceFiles(InputStream in) throws IOException {
        Objects.requireNonNull(in);
        List<String> filenames = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))){
            String resource;
            while ((resource = br.readLine()) != null) {
                filenames.add(resource);
            }
        }
        return filenames;
    }

    public String getClassPath(){
        return this.classPath;
    }

    @Override
    public String toString(){
        return classPath;
    }

    private static InputStream getResourceAsStream(String resource) {
        final InputStream in = getContextClassLoader().getResourceAsStream(resource);
        return in == null ? ClassPathResource.class.getResourceAsStream(resource) : in;
    }

    private static URL getResource(String resource){
        final URL url = getContextClassLoader().getResource(resource);
        return url == null ? ClassPathResource.class.getResource(resource) : url;
    }

    private static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
