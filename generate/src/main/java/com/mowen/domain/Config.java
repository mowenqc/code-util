package com.mowen.domain;

import java.text.SimpleDateFormat;
import java.util.Date;

/***
 * @description :  整个初始化的配置文件，主要指定数据库，数据表，文件输出目录
 * @author: mowen
 * @create_time: 2020/1/10 12:04
 * @since: v1.0
 */
public class Config {

    private String projectPath;

    private String moduleName;

    private String controllerModule;

    private String daoModule;

    private String mapperModule;

    private String serviceModule;

    private String interfaceModule;

    private String viewModule;

    private String domainModule;

    private String tableName;

    private String groupId;

    private String packageName;

    private String url;

    private String userName;

    private String password;

    private int deleteSplit;

    private String viewTemplate;

    private String schema;

    private String user;
    private String time;




    public static Builder builder(){
        return new Builder();
    }
    public static class Builder{
        /**
         * 项目路径，也就从根目录，到项目的目录
         */
        private String projectPath;

        /**
         * 通用模块， 也就是整个生成代码在一个模块中，通常是单工程，比如后台管理系统
         */
        private String moduleName;

        /**
         * 下面几个具体的模块名称，我们在代码中，很可能代码没有在一个模块中，但是在一个项目中
         * 比如我们有一个dubbo的rpc项目， 实体与接口放在一个模块中
         * 具体的服务实现在另外一个模块中
         *
         */
        private String controllerModule;

        private String daoModule;

        private String mapperModule;

        private String serviceModule;

        private String interfaceModule;

        private String viewModule;


        private String domainModule;

        /**
         * 数据库中的表名称
         */
        private String tableName;

        /**
         * 详见groupId
         */
        private String packageName;

        private String url;

        private String userName;

        private String password;

        /**
         * 一个包的前面一样的部分， 比如我们有 org.springframework.aop.Pointcut
         * org.springframework  就是groupId
         * aop 就是packageName
         * Pointcut 就是类型
         * 当然，我们生成的时候会很controller
         * service
         * Mapper, 中间加上一层这样的分层标记
         */
        private String groupId;

        //有些表的名字可能有前缀， 我们在生成的时候应该把前缀去掉，那么我们去掉几个分隔符
        //假如一个公司的简写为gie, 那么在名称表明的时候可能会名称成 gie_user, gie_department, 注意我们约定用下划线分隔，不用其他的
        private int deleteSplit;

        private String viewTemplate;

        private String schema;

        public Builder projectPath(String name){
            this.projectPath = name;
            return this;
        }
        public Builder moduleName(String name){
            this.moduleName = name;
            return this;
        }
        public Builder controllerModule(String name){
            this.controllerModule = name;
            return this;
        }
        public Builder daoModule(String name){
            this.daoModule = name;
            return this;
        }
        public Builder mapperModule(String name){
            this.mapperModule = name;
            return this;
        }
        public Builder serviceModule(String name){
            this.serviceModule = name;
            return this;
        }

        public Builder interfaceModule(String name){
            this.interfaceModule = name;
            return this;
        }
        public Builder viewModule(String name){
            this.viewModule = name;
            return this;
        }
        public Builder domainModule(String name){
            this.domainModule = name;
            return this;
        }

        public Builder tableName(String name){
            this.tableName = name;
            return this;
        }

        public Builder url(String name){
            this.url = name;
            return this;
        }

        public Builder packageName(String name){
            this.packageName = name;
            return this;
        }

        public Builder userName(String name){
            this.userName = name;
            return this;
        }

        public Builder password(String name){
            this.password = name;
            return this;
        }

        public Builder groupId(String name){
            this.groupId = name;
            return this;
        }

        public Builder deleteSplit(int count){
            this.deleteSplit = count;
            return this;
        }

        public Builder viewTemplate(String viewTemplate) {
            this.viewTemplate = viewTemplate;
            return this;
        }

        public Builder schema(String schema) {
            this.schema = schema;
            return this;
        }

        public Config build(){
            Config config = new Config();
            config.setProjectPath(projectPath);
            config.setModuleName(moduleName);
            config.setControllerModule(controllerModule);
            config.setDaoModule(daoModule);
            config.setModuleName(mapperModule);
            config.setServiceModule(serviceModule);
            config.setInterfaceModule(interfaceModule);
            config.setViewModule(viewModule);
            config.setTableName(tableName);
            config.setPackageName(packageName);
            config.setUrl(url);
            config.setUserName(userName);
            config.setPassword(password);
            config.setGroupId(groupId);
            config.setDeleteSplit(deleteSplit);
            config.setViewTemplate(viewTemplate);
            config.setMapperModule(mapperModule);
            config.setDomainModule(domainModule);
            config.setSchema(schema);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = dateFormat.format(new Date());
            config.setTime(format);
            config.setUser(System.getProperty("user.name"));
            return config;
        }
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getViewTemplate() {
        return viewTemplate;
    }

    public void setViewTemplate(String viewTemplate) {
        this.viewTemplate = viewTemplate;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getControllerModule() {
        return controllerModule;
    }

    public void setControllerModule(String controllerModule) {
        this.controllerModule = controllerModule;
    }

    public String getDaoModule() {
        return daoModule;
    }

    public void setDaoModule(String daoModule) {
        this.daoModule = daoModule;
    }

    public String getMapperModule() {
        return mapperModule;
    }

    public void setMapperModule(String mapperModule) {
        this.mapperModule = mapperModule;
    }

    public String getServiceModule() {
        return serviceModule;
    }

    public void setServiceModule(String serviceModule) {
        this.serviceModule = serviceModule;
    }

    public String getInterfaceModule() {
        return interfaceModule;
    }

    public void setInterfaceModule(String interfaceModule) {
        this.interfaceModule = interfaceModule;
    }

    public String getViewModule() {
        return viewModule;
    }

    public void setViewModule(String viewModule) {
        this.viewModule = viewModule;
    }

    public String getDomainModule() {
        return domainModule;
    }

    public void setDomainModule(String domainModule) {
        this.domainModule = domainModule;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getDeleteSplit() {
        return deleteSplit;
    }

    public void setDeleteSplit(int deleteSplit) {
        this.deleteSplit = deleteSplit;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
