package com.ytx.util.war;

import com.ytx.util.domain.ProjectConfig;
import com.ytx.util.domain.enums.OperateSystem;
import com.ytx.util.resource.ClassPathResource;
import com.ytx.util.resource.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import sun.misc.IOUtils;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by chutao on 2018/4/14.
 */
public class AppController {
    private static BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();

    public static String takeLog() throws InterruptedException{
        return logQueue.poll(10,TimeUnit.MINUTES);
    }

    /***
     * 1.提示用户是否已经提交全部代码到远程仓库
     * 3.将远程代码检出到本地
     * 4.检查配置的domain中，是否已经全部实现了serializable,以及jsonIgnore("handler")
     * 5.执行命令，将代码打包，完成后，给出提示，并记录一条log
     *
     * @return  将本次打包的信息返回
     * @throws 如果是错误异常，直接抛出去
     */
    public static void run(List<File> configFileList) throws Exception{
        try {
            List<ProjectConfig> configList = convertConfig(configFileList);
            try {
                List<String> buildLogList = new ArrayList<>();
                List<String> resultMsgList = new ArrayList<>();
                resultMsgList.add("\n\n\n\n[打包结果]:");
                //逐个打包
                for (ProjectConfig projectConfig : configList) {
                    File tmpDir = generateTmpDir();
                    File scriptTmpDir = generateTmpDir();
                    projectConfig.setTmpDir(tmpDir);
                    projectConfig.setScriptTmpDir(scriptTmpDir);
                    try {
                        logQueue.put("开始打包\n地址:" + projectConfig.getRepositoryUrl() + "\n模块:" + projectConfig.getModule() + "\n\n");
                        //检出代码
                        logQueue.put("开始检出代码" + "\n");
                        checkoutOrBuild(projectConfig, InvokeScriptType.CHECKOUT);
                        logQueue.put("检出代码结束" + "\n\n");
                        //检查代码中的domain
                        logQueue.put("开始检查domain" + "\n");
                        checkDomain(projectConfig);
                        logQueue.put("检查domain结束" + "\n\n");
                        //如果检查domain没有问题，deploy client
                        logQueue.put("开始部署client" + "\n");
                        checkoutOrBuild(projectConfig, InvokeScriptType.DEPLOY_CLIENT);
                        logQueue.put("部署client结束" + "\n\n");
                        //构建
                        logQueue.put("开始打war包" + "\n");
                        checkoutOrBuild(projectConfig, InvokeScriptType.BUILD);
                        logQueue.put("打war包结束" + "\n\n");
                        //找到war包，设置到projectConfig中
                        setWarFile(projectConfig);
                        resultMsgList.add(projectConfig.toString());
                    } catch (Exception ex) {
                        throw ex;
                    }
                }
                //将包全部复制到目标文件夹
                copyWarToDestDir(configList);
                //输出全部成功的log
                for (String msg : resultMsgList) {
                    logQueue.put(msg);
                }
            }finally{
                //删除所有构建中的临时文件夹
                for(ProjectConfig projectConfig : configList){
                    try{
                        FileUtils.deleteDirectory(projectConfig.getTmpDir());
                        FileUtils.deleteDirectory(projectConfig.getScriptTmpDir());
                    }catch(Exception e){
                    }
                }
            }
        }finally{
            logQueue.put("exit");
        }
    }

    private static void copyWarToDestDir(List<ProjectConfig> projectConfigs) throws IOException{
        for(ProjectConfig projectConfig : projectConfigs){
            queueLog("开始复制war包:\n" + projectConfig.getWarFile().getName() + "\n\n");
            projectConfig.copyWar();
        }
    }

    private static void queueLog(String msg){
        if(StringUtils.isBlank(msg)){
            return;
        }
        try{
            logQueue.put(msg);
        }catch(InterruptedException ex){
        }
    }

    private static void setWarFile(ProjectConfig projectConfig){
        File tmpDir = projectConfig.getTmpDir();
        File moduleDir = new File(tmpDir,projectConfig.getModule());
        if(!moduleDir.isDirectory()){
            throw new IllegalStateException("找不到模块的文件夹");
        }
        File moduleTargetDir = new File(moduleDir,"target");
        if(!moduleTargetDir.isDirectory()){
            throw new IllegalStateException("找不到模块下的target文件夹");
        }
        Collection<File> warFileList = FileUtils.listFiles(moduleTargetDir,new String[]{"war"},false);
        if(CollectionUtils.isEmpty(warFileList)){
            throw new IllegalStateException("找不到war包");
        }
        if(warFileList.size() > 1){
            throw new IllegalStateException("有多个war包");
        }
        File warFile = null;
        for(File f : warFileList){
            warFile = f;
        }
        projectConfig.setWarFile(warFile);
    }

    private static void checkDomain(ProjectConfig projectConfig) throws Exception{
        File tmpDir = projectConfig.getTmpDir();
        File domainDir = new File(tmpDir,projectConfig.getDomainRelativePath());
        if(!domainDir.isDirectory()){
            throw new IllegalArgumentException("找不到domain文件夹");
        }
        Collection<File> domainList = FileUtils.listFiles(domainDir,new String[]{"java"},false);
        StringBuilder errorMsg = new StringBuilder();

        for(File domain : domainList){
            checkSingleDomain(domain,errorMsg);
        }
        String errorMsgStr = errorMsg.toString();
        if(!errorMsgStr.isEmpty()){
            throw new IllegalStateException("\ndomain检查报错，请修改:\n\n" + errorMsgStr);
        }
    }

    public static void checkSingleDomain(File domain,StringBuilder errorMsg) throws IOException{
        final String handlerIgnore = "@JsonIgnoreProperties(value = {\"handler\"})";
        final String serializable = "Serializable";
        final String implement = "implements";
        final String uid = "serialVersionUID";

        String domainCode = FileUtils.readFileToString(domain);
        if(domainCode.indexOf(handlerIgnore) == -1){
            errorMsg.append(domain.getName()).append("没有设置handler的JsonIgnore,请在类上添加\n@JsonIgnoreProperties(value = {\"handler\"})\n\n");
        }
        if(!(domainCode.indexOf(serializable) != -1 && domainCode.indexOf(implement) != -1)){
            errorMsg.append(domain.getName()).append("没有实现Serializable接口\n\n");
        }
        if(domainCode.indexOf(uid) == -1){
            errorMsg.append(domain.getName()).append("没有添加serialVersionUID变量，请在类中添加")
                    .append("\n")
                    .append("private static final long serialVersionUID = 1L;")
                    .append("\n\n");
        }
    }

    private static String getScriptPath(ProjectConfig projectConfig,InvokeScriptType invokeScriptType){
        OperateSystem operateSystem = OperateSystem.parseOperateSystem(projectConfig.getOperateSystem());
        if(invokeScriptType == InvokeScriptType.CHECKOUT){
            return operateSystem.getCheckoutScript();
        }else{
            return operateSystem.getBuildScript();
        }
    }

    private static enum InvokeScriptType{
        CHECKOUT(1,"检出代码","1","检出出错"),
        BUILD(2,"构建","1","打包出错"),
        DEPLOY_CLIENT(3,"部署client","2","部署client出错");

        private int value;
        private String descript;
        private String projectType;
        private String errorMsg;

        InvokeScriptType(int value, String descript,String projectType,String errorMsg) {
            this.value = value;
            this.descript = descript;
            this.projectType = projectType;
            this.errorMsg = errorMsg;
        }

        public int getValue() {
            return value;
        }

        public String getDescript() {
            return descript;
        }

        public String getProjectType() {
            return projectType;
        }

        public String getErrorMsg() {
            return errorMsg;
        }
    }

    private static void checkoutOrBuild(ProjectConfig projectConfig,InvokeScriptType invokeScriptType) throws Exception{
        File tmpDir = projectConfig.getTmpDir();
        File scriptTmpDir = projectConfig.getScriptTmpDir();
        String scriptPathOrigin = getScriptPath(projectConfig,invokeScriptType);
        String packageScript = getTmpScriptPath(scriptPathOrigin,scriptTmpDir);
        String projectDirPath = tmpDir.getAbsolutePath();
        String projectType = invokeScriptType.getProjectType();
        String repository = projectConfig.getRepositoryUrl();
        String vcsType = String.valueOf(projectConfig.getVersionControlType());
        String module = (invokeScriptType == InvokeScriptType.DEPLOY_CLIENT ? projectConfig.getClientModule() : projectConfig.getModule());
        String gitBranch = projectConfig.getGitBranch();
        String env = projectConfig.getEnv();
        String clientModule = projectConfig.getClientModule();
        String command = org.apache.commons.lang3.StringUtils.join(new String[]{packageScript,projectDirPath,repository,projectType,vcsType,module,gitBranch,env,clientModule}," ");
        //执行命令
        Runtime rt = Runtime.getRuntime();
        Process pr = rt.exec(command);
        //读取命令输出
        BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream(), "GBK"));
        String line = null;
        while ((line = input.readLine()) != null) {
            if(org.apache.commons.lang3.StringUtils.isNotBlank(line)) {
                logQueue.put(line + "\n");
            }
        }
        int exitVal = pr.waitFor();
        logQueue.put("Exited with error code " + exitVal);
        if(exitVal != 0){
            throw new IllegalStateException(invokeScriptType.getErrorMsg());
        }
    }

    private static String getTmpScriptPath(String scriptClassPath,File scriptTmpDir) throws IOException{
        ClassPathResource classPathResource = ClassPathResource.getInstance(scriptClassPath);
        if(classPathResource == null){
            throw new IllegalStateException("找不到检出代码的脚本");
        }
        InputStream inputStream = classPathResource.getInputStream();
        List<String> scriptLines = org.apache.commons.io.IOUtils.readLines(inputStream);
        File tmpScriptFile = new File(scriptTmpDir,scriptClassPath);
        FileUtils.writeLines(tmpScriptFile,scriptLines);
        return tmpScriptFile.getPath();
    }

    private static File generateTmpDir(){
        String folder = System.getProperty("java.io.tmpdir");
        File tmpDir = new File(folder);
        if(!tmpDir.isDirectory()){
            throw new IllegalStateException("找不到系统中的临时文件夹");
        }
        long timestamp = System.currentTimeMillis();
        File dir = new File(tmpDir,String.valueOf(timestamp));
        dir.mkdir();
        return dir;
    }

    private static List<ProjectConfig> convertConfig(List<File> configFileList) throws Exception{
        if(configFileList.isEmpty()){
            throw new IllegalArgumentException("没有配置文件");
        }
        for(File f : configFileList){
            if(!f.isFile() && f.getName().endsWith("properties")){
                throw new IllegalArgumentException(f.getName() + "不是配置文件");
            }
        }
        List<ProjectConfig> configList = new ArrayList<>();
        for(File f : configFileList){
            InputStream inputStream = new FileInputStream(f);
            Properties properties = new Properties();
            properties.load(inputStream);
            //读出字段
            String operateSystem = properties.getProperty("operate.system");
            operateSystem = trim(operateSystem);
            String versionControlTool = properties.getProperty("version.control.tool");
            versionControlTool = trim(versionControlTool);
            String gitBranch = properties.getProperty("git.branch");
            gitBranch = trim(gitBranch);
            String env = properties.getProperty("env");
            env = trim(env);
            String repositoryUrl = properties.getProperty("repository.url");
            repositoryUrl = trim(repositoryUrl);
            String module = properties.getProperty("module");
            module = trim(module);
            String clientModule = properties.getProperty("client.module");
            clientModule = trim(clientModule);
            String destPath = properties.getProperty("dest.path");
            destPath = trim(destPath);
            String domainRelativePath = properties.getProperty("domain.relative.path");
            domainRelativePath = trim(domainRelativePath);
            //检查字段合法性
            if(operateSystem != null && (!operateSystem.equals("windows") && !operateSystem.equals("linux"))){
                throw new IllegalArgumentException("操作系统必须是windows或者linux");
            }
            if(versionControlTool != null && (!versionControlTool.trim().equals("git") && !versionControlTool.equals("svn"))){
                throw new IllegalArgumentException("版本管理必须是git或者svn");
            }
            if(env == null || (!env.equals("www") && !env.equals("prepub") && !env.equals("test"))){
                throw new IllegalArgumentException("环境配置有错误，只能够是www,prepub,test中的一个");
            }
            if(repositoryUrl == null || repositoryUrl.isEmpty()){
                throw new IllegalArgumentException("版本库地址不能为空");
            }
            if(versionControlTool.equals("svn") && repositoryUrl.endsWith("/")){
                repositoryUrl = repositoryUrl.substring(0,repositoryUrl.length() - 1);
            }
            if(StringUtils.isBlank(module)){
                throw new IllegalStateException("请指定模块");
            }
            if(StringUtils.isBlank(clientModule)){
                clientModule = "client";
            }
            if(destPath == null || destPath.isEmpty()){
                throw new IllegalArgumentException("目标文件夹不能为空");
            }
            File destDir = new File(destPath);
            if(!destDir.isDirectory()){
                throw new IllegalArgumentException("保存war包的目标文件夹不存在");
            }
            if(domainRelativePath.isEmpty()){
                throw new IllegalArgumentException("domain路径不能为空");
            }
            if(org.apache.commons.lang3.StringUtils.isBlank(gitBranch)){
                gitBranch = "release";
            }
            //构建config对象
            ProjectConfig config = new ProjectConfig();
            config.setOperateSystem(operateSystem);
            config.setVersionControlTool(versionControlTool);
            config.setGitBranch(gitBranch);
            config.setEnv(env);
            config.setRepositoryUrl(repositoryUrl);
            config.setModule(module);
            config.setClientModule(clientModule);
            config.setDestPath(destPath);
            config.setDestDir(destDir);
            config.setDomainRelativePath(domainRelativePath);
            configList.add(config);
        }
        return configList;
    }

    private static String trim(String s){
        if(s == null){
            return "";
        }else{
            return s.trim();
        }
    }
}
