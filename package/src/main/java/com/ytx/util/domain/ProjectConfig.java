package com.ytx.util.domain;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by chutao on 2018/4/14.
 */
public class ProjectConfig {
    //选填，默认windows，可选项(windows,linux）
    private String operateSystem;
    //选填，默认git
    private String versionControlTool;
    //选填，如果是版本管理使用git，建议填写，如果不填，默认release(打包分支,上线前最好创建打包分支，避免当前在develop分支中的修改与上线内容混淆，如果当前develop就是全部要上线的，也不会混淆，也可以直接检出develop分支)
    private String gitBranch;
    //必填,版本库地址
    private String repositoryUrl;
    //必填，模块，比如"service","webapp"等
    private String module;
    //client的module，用于部署client,默认client
    private String clientModule;
    //选填，默认生产环境,也可以选择其它环境(www,prepub,test)
    private String env = "www";
    //war包目标文件夹
    private String destPath;
    //必填,domain相对路径,即domain文件夹在检出的代码中的相对路径，用于检查domain
    private String domainRelativePath;


    //功能字段
    private File destDir;
    private File warFile;
    private File tmpDir;
    private File scriptTmpDir;

    public String getVersionControlTool() {
        return versionControlTool;
    }

    public void setVersionControlTool(String versionControlTool) {
        this.versionControlTool = versionControlTool;
    }

    public int getVersionControlType(){
        if(versionControlTool.equals("svn")){
            return 1;
        }
        if(versionControlTool.equals("git")){
            return 2;
        }
        throw new IllegalArgumentException("不支持的版本系统");
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getDestPath() {
        return destPath;
    }

    public void setDestPath(String destPath) {
        this.destPath = destPath;
    }

    public String getOperateSystem() {
        return operateSystem;
    }

    public void setOperateSystem(String operateSystem) {
        this.operateSystem = operateSystem;
    }

    public String getGitBranch() {
        return gitBranch;
    }

    public void setGitBranch(String gitBranch) {
        this.gitBranch = gitBranch;
    }

    public String getDomainRelativePath() {
        return domainRelativePath;
    }

    public void setDomainRelativePath(String domainRelativePath) {
        this.domainRelativePath = domainRelativePath;
    }

    public File getDestDir() {
        return destDir;
    }

    public void setDestDir(File destDir) {
        this.destDir = destDir;
    }

    public File getWarFile() {
        return warFile;
    }

    public void setWarFile(File warFile) {
        this.warFile = warFile;
    }

    public File getTmpDir() {
        return tmpDir;
    }

    public void setTmpDir(File tmpDir) {
        this.tmpDir = tmpDir;
    }

    public File getScriptTmpDir() {
        return scriptTmpDir;
    }

    public void setScriptTmpDir(File scriptTmpDir) {
        this.scriptTmpDir = scriptTmpDir;
    }

    public String getClientModule() {
        return clientModule;
    }

    public void setClientModule(String clientModule) {
        this.clientModule = clientModule;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("\n版本库:").append(this.repositoryUrl).append("\n");
        if(versionControlTool.equals("git")){
            sb.append("分支:").append(this.gitBranch).append("\n");
        }
        sb.append("模块:").append(this.module).append("\n")
                .append("目标文件夹:").append(this.destPath).append("\n")
                .append("打包成功！").append("\n");
        return sb.toString();
    }

    public void copyWar() throws IOException{
        FileUtils.copyFileToDirectory(this.warFile,this.destDir);
    }
}
