package com.mowen.domain;

/***
 * @description : 
 * @author: mowen
 * @create_time: 2020/1/10 13:58
 * @since: v1.0
 */
public class DataInfo {

    private Table table;

    private Config config;

    private String daoPackage;
    private String interfacePackage;
    private String servicePackage;

    private String controllerPackage;

    private String domainPackage;

    private String viewPackage;

    public DataInfo(Config config, Table table){
        this.config = config;
        this.table = table;
        initPackage();
    }

    public void initPackage(){
        daoPackage = config.getGroupId() + "." + config.getPackageName() + ".dao";
        interfacePackage = config.getGroupId() + "." + config.getPackageName() + ".service";
        servicePackage = config.getGroupId() + "." + config.getPackageName() + ".service.impl";
        controllerPackage = config.getGroupId() + "." + config.getPackageName() + ".controller";
        domainPackage = config.getGroupId() + "." + config.getPackageName() + ".domain";
        viewPackage = config.getViewTemplate();
    }

    public String getViewPackage() {
        return viewPackage;
    }

    public void setViewPackage(String viewPackage) {
        this.viewPackage = viewPackage;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public String getDaoPackage() {
        return daoPackage;
    }

    public void setDaoPackage(String daoPackage) {
        this.daoPackage = daoPackage;
    }

    public String getInterfacePackage() {
        return interfacePackage;
    }

    public void setInterfacePackage(String interfacePackage) {
        this.interfacePackage = interfacePackage;
    }

    public String getServicePackage() {
        return servicePackage;
    }

    public void setServicePackage(String servicePackage) {
        this.servicePackage = servicePackage;
    }

    public String getControllerPackage() {
        return controllerPackage;
    }

    public void setControllerPackage(String controllerPackage) {
        this.controllerPackage = controllerPackage;
    }

    private String getNotNullString(String str1, String str2){
        if(str1 != null && str1 != ""){
            return str1;
        }
        return str2;
    }

    public String getDomainPackage() {
        return domainPackage;
    }

    public void setDomainPackage(String domainPackage) {
        this.domainPackage = domainPackage;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }
}
