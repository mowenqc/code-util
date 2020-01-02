package com.ytx.util;

import com.alibaba.fastjson.JSON;
import com.ytx.util.template.TemplateUtil;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chutao on 2017/10/13.
 */
public class Generator {

    private static Logger logger = Logger.getLogger(Generator.class);

    private static String driverName = "com.mysql.jdbc.Driver";


    private static String url = "jdbc:mysql://mysql.ytx.com/gie?useUnicode=true&characterEncoding=UTF8&zeroDateTimeBehavior=convertToNull&autoReconnect=true&idleConnectionTestPeriod=120&preferredTestQuery=select now()&serverTimezone=UTC";
    private static String userName = "root";
    private static String password = "ytxroot";

    public static void main(String[] args) throws IOException {
        //项目路径， 比如后台管理:D:\project\learning\business_admin
//        String fileDir = "D:\\project\\ekaobang\\admin";
        String fileDir = "D:\\develop\\tmp-project\\GrandInternationalEducation";
        String tableName = "gie_user";
        PackageName packageName = generatePackageNames("com.gie.admin", "user");
        packageName.setControllerModule("admin");
        packageName.setDaoModule("admin");
        packageName.setMapperModule("admin");
        packageName.setServiceModule("admin");
        packageName.setInterfaceModule("admin");
        packageName.setViewModule("admin");
        packageName.setDomainModule("admin");
        generate(tableName, packageName, fileDir);
    }

    private static void generate(String tableName, PackageName packageName, String fileDir) {
        try {
            Table table = getTable(tableName);
            if (table == null) {
                return;
            }
            setViewTypes(table);

            //生成
            //写入文件夹
            File dir = new File(fileDir);
            if (!dir.isDirectory() && !dir.exists()) {
                dir.mkdir();
            }
            //1. genarate domain
            writeDomain(packageName, fileDir, table);

            //2. genarate dao
            writeDao(packageName, fileDir, table);

            //3 genarate xml
            writeMapper(packageName, fileDir, table);

            //4 genarate controller
            writeController(packageName, fileDir, table);

            //5. genarate web vm file
            writeVm(packageName, fileDir, table);

            //6 generate service
            writeService(packageName, fileDir, table);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
    }

    private static void setViewTypes(Table table) {
        List<TableColumn> columns = table.getColumnList();
        if (columns == null) {
            logger.info(table + "，的column== null");
            return;
        }

        List<ViewType> viewTypes = new ArrayList<>();
        ViewType viewType;
        for (TableColumn column : columns) {
            System.out.println(column);
            viewType = new ViewType();
            viewType.setField(column.getFieldName());
            viewType.setType(1);
            String type = "";
            String comment = column.getComment();
            System.out.println(comment);
            String title = comment;
            if (comment != null && comment != "") {
                String[] split = comment.split("\\|");
                if (split != null || split.length > 0) {
                    title = split[0];
                    if (split.length == 2) {
                        type = split[1];
                    }
                    if (split.length == 3) {
                        type = split[2];
                        setViewOptions(viewType,split[1]);
                    }
                }
            }
            int fieldType = getFieldType(type);
            viewType.setType(fieldType);
            viewType.setName(title);
            viewTypes.add(viewType);
        }
        table.setViewTypes(viewTypes);
    }

    private static void setViewOptions(ViewType type, String option) {
        if (option == null || option == "") {
            return;
        }
        String[] split = option.split(",");
        if(split == null || split.length == 0){
            return;
        }
        List<NamePair> namePairs = new ArrayList<>();
        for (String s : split) {
            String[] split1 = s.split(":");
            if(split1 != null && split1.length ==2){
                NamePair namePair = new NamePair();
                namePair.setName(split1[0]);
                namePair.setValue(split1[1]);
                namePairs.add(namePair);
            }
        }
        type.setSelectList(namePairs);
    }

    private static int getFieldType(String text) {
        if (text == null || text == "") {
            return 1;
        }
        if("select".equals(text)){
            return 5;
        }
        if ("text".equals(text)) {
            return 7;
        }
        if ("date".equals(text)) {
            return 8;
        }
        return 1;
    }

    private static void confirmVmType(Table table) {
        String property =
                System.getProperty("user.dir") + "/generate/src/main/resources/velocity/index.json";
        StringBuilder json = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(property));
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            reader.close();
            String jsonObject = json.toString().replace(" ", "");
            List<ViewType> viewTypes = JSON.parseArray(jsonObject, ViewType.class);
            if (viewTypes == null || viewTypes.size() == 0) {
                return;
            }
            Map<String, ViewType> cache = new HashMap<>();
            for (ViewType viewType : viewTypes) {
                cache.put(viewType.getField(), viewType);
            }
            for (TableColumn column : table.getColumnList()) {
                if (cache.get(column.getFieldName()) != null) {
                    column.setViewType(cache.get(column.getFieldName()));
                }
            }
            if (viewTypes != null) {
                table.setViewTypes(viewTypes);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
    }

    private static void writeDomain(PackageName packageName, String fileDir, Table table)
            throws IOException {
        if(packageName.getDomainModule() == null || packageName.getDomainModule() == ""){
            return;
        }
        String domain = TemplateUtil.generateDomain(packageName.getDomainPackageName(), table);
        String domainFir =
                fileDir + "/" + packageName.getDomainModule() +  "/src/main/java/" + packageName.getDomainPackageName().replace(".", "/");
        File domainFileDir = new File(domainFir);
        if (!domainFileDir.exists()) {
            domainFileDir.mkdirs();
        }
        File domainFile = new File(domainFir, table.getDomainClassName() + ".java");
        FileUtils.write(domainFile, domain, "utf-8");
    }

    private static void writeDao(PackageName packageName, String fileDir, Table table)
            throws IOException {
        if(packageName.getDaoModule() == null || packageName.getDaoModule() == ""){
            return;
        }
        String dao = TemplateUtil
                .generateDao(packageName.getDaoPackageName(), packageName.getDomainPackageName(), table);
        String daoDir = fileDir + "/" + packageName.getDaoModule() + "/src/main/java/" + packageName.getDaoPackageName().replace(".", "/");
        File daoFileDir = new File(daoDir);
        if (!daoFileDir.exists()) {
            daoFileDir.mkdirs();
        }
        File daoFile = new File(daoDir, table.getDomainClassName() + "Mapper.java");
        FileUtils.write(daoFile, dao, "utf-8");
    }

    private static void writeMapper(PackageName packageName, String fileDir, Table table)
            throws IOException {
        if(packageName.getMapperModule() == null || packageName.getMapperModule() == ""){
            return;
        }
        String xml = TemplateUtil
                .generateXml(packageName.getDaoPackageName(), packageName.getDomainPackageName(), table);
        String mapperDir = fileDir + "/" + packageName.getMapperModule() +  "/src/main/resources/mapper/";
        File mapperFileDir = new File(mapperDir);
        if (!mapperFileDir.exists()) {
            mapperFileDir.mkdirs();
        }
        File xmlFile = new File(mapperDir, table.getDomainClassName() + ".xml");
        FileUtils.write(xmlFile, xml, "utf-8");
    }

    private static void writeController(PackageName packageName, String fileDir, Table table)
            throws IOException {
        if(packageName.getControllerModule() == null || packageName.getControllerModule() == ""){
            return;
        }
        String controllerDir =
                fileDir + "/" + packageName.getControllerModule() +  "/src/main/java/" + packageName.getControllerPackageName().replace(".", "/");
        File controllerFileDir = new File(controllerDir);
        if (!controllerFileDir.exists()) {
            controllerFileDir.mkdirs();
        }
        String controller = TemplateUtil.generateController(packageName.getControllerPackageName(),
                packageName.getiServicePackageName(),
                packageName.getDaoPackageName(), packageName.getDomainPackageName(),
                packageName.getPrefix(), table);
        File controllerFile = new File(controllerDir, table.getDomainClassName() + "Controller.java");
        FileUtils.write(controllerFile, controller, "utf-8");
    }

    private static void writeVm(PackageName packageName, String fileDir, Table table) throws IOException {
        if(packageName.getViewModule() == null || packageName.getViewModule() == ""){
            return;
        }
        String vm = TemplateUtil.generateVm(table);
        String vmDir = fileDir + "/" + packageName.getViewModule() + "/src/main/webapp/WEB-INF/tpl/" + table.getDomainObjName();
        File vmFileDir = new File(vmDir);
        if (!vmFileDir.exists()) {
            vmFileDir.mkdirs();
        }
        File vmFile = new File(vmDir, "index.vm");
        FileUtils.write(vmFile, vm, "utf-8");
    }

    private static void writeService(PackageName packageName, String fileDir, Table table)
            throws IOException {
        if(packageName.getServiceModule() == null || packageName.getServiceModule() == ""){
            return;
        }
        if(packageName.getInterfaceModule() == null || packageName.getInterfaceModule() == ""){
            return;
        }
        String service = TemplateUtil
                .generateService(packageName.getServicePackageName(), packageName.getiServicePackageName(),
                        packageName.getDaoPackageName(), packageName.getDomainPackageName(), packageName.getPrefix(), table);
        String iService = TemplateUtil
                .generateIService(packageName.getiServicePackageName(), packageName.getDaoPackageName(),
                        packageName.getDomainPackageName(), packageName.getPrefix(), table);
        String interfaceDir = fileDir + "/" + packageName.getInterfaceModule() +  "/src/main/java/" + packageName.getiServicePackageName().replace(".", "/");
        String serviceFir = fileDir + "/" + packageName.getServiceModule() + "/src/main/java/" + packageName.getServicePackageName().replace(".", "/");
        File serviceFile = new File(serviceFir, table.getDomainClassName() + "Service.java");
        FileUtils.write(serviceFile, service, "utf-8");
        File iServiceFile = new File(interfaceDir, "I" + table.getDomainClassName() + "Service.java");
        FileUtils.write(iServiceFile, iService, "utf-8");
    }

    private static Table getTable(String tableName) throws SQLException {
        Connection connection = DriverManager.getConnection(url, userName, password);
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        ResultSet tableRet = databaseMetaData.getColumns(null, "%", tableName, "%");
        if (tableRet == null) {
            return null;
        }
        String sql = "select * from " + tableName;
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSetMetaData metaData = statement.getMetaData();
        Table table = new Table(tableRet, metaData);
        return table;
    }

    private static PackageName generatePackageNames(String prefix, String code) {
        PackageName packageName = new PackageName();
        packageName.setDomainPackageName(prefix + "." + code + ".domain");
        packageName.setDaoPackageName(prefix + "." + code + ".dao");
        packageName.setServicePackageName(prefix + "." + code + ".service.impl");
        packageName.setiServicePackageName(prefix + "." + code + ".service");
        packageName.setControllerPackageName(prefix + "." + code + ".controller");
        packageName.setPrefix(prefix);
        return packageName;
    }

    private static class PackageName {

        String tableName;
        String domainPackageName;
        String daoPackageName;
        String servicePackageName;
        String iServicePackageName;
        String controllerPackageName;
        String prefix;

        String viewModule;

        String domainModule;

        String interfaceModule;

        private String serviceModule;

        private String daoModule;

        private String controllerModule;

        private String mapperModule;

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

        public String getInterfaceModule() {
            return interfaceModule;
        }

        public void setInterfaceModule(String interfaceModule) {
            this.interfaceModule = interfaceModule;
        }

        public String getServiceModule() {
            return serviceModule;
        }

        public void setServiceModule(String serviceModule) {
            this.serviceModule = serviceModule;
        }

        public String getDaoModule() {
            return daoModule;
        }

        public void setDaoModule(String daoModule) {
            this.daoModule = daoModule;
        }

        public String getControllerModule() {
            return controllerModule;
        }

        public void setControllerModule(String controllerModule) {
            this.controllerModule = controllerModule;
        }

        public String getMapperModule() {
            return mapperModule;
        }

        public void setMapperModule(String mapperModule) {
            this.mapperModule = mapperModule;
        }

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public String getDomainPackageName() {
            return domainPackageName;
        }

        public void setDomainPackageName(String domainPackageName) {
            this.domainPackageName = domainPackageName;
        }

        public String getDaoPackageName() {
            return daoPackageName;
        }

        public void setDaoPackageName(String daoPackageName) {
            this.daoPackageName = daoPackageName;
        }

        public String getServicePackageName() {
            return servicePackageName;
        }

        public void setServicePackageName(String servicePackageName) {
            this.servicePackageName = servicePackageName;
        }

        public String getiServicePackageName() {
            return iServicePackageName;
        }

        public void setiServicePackageName(String iServicePackageName) {
            this.iServicePackageName = iServicePackageName;
        }

        public String getControllerPackageName() {
            return controllerPackageName;
        }

        public void setControllerPackageName(String controllerPackageName) {
            this.controllerPackageName = controllerPackageName;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }
    }

}
