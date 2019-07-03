package com.ytx.util;

import com.alibaba.fastjson.JSON;
import com.ytx.common.util.StringUtil;
import com.ytx.util.template.TemplateUtil;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * Created by chutao on 2017/10/13.
 */
public class Generator {

  private static Logger logger = Logger.getLogger(Generator.class);

  private static String driverName = "com.mysql.jdbc.Driver";

//    private static String url = "jdbc:mysql://localhost:3306/ytx_maintain?useUnicode=true&characterEncoding=UTF8&zeroDateTimeBehavior=convertToNull&autoReconnect=true&idleConnectionTestPeriod=120&preferredTestQuery=select now()";
//    private static String userName = "root";
//    private static String password = "root";

//    private static String url = "jdbc:mysql://localhost:3306/learning?useUnicode=true&characterEncoding=UTF8&zeroDateTimeBehavior=convertToNull&autoReconnect=true&idleConnectionTestPeriod=120&preferredTestQuery=select now()&serverTimezone=UTC";
//    private static String userName = "root";
//    private static String password = "root";

//    private static String url = "jdbc:mysql://mysql.ytx.com/ytxlearning?useUnicode=true&characterEncoding=UTF8&zeroDateTimeBehavior=convertToNull&autoReconnect=true&idleConnectionTestPeriod=120&preferredTestQuery=select now()";
//    private static String userName = "learning";
//    private static String password = "learning";

//    private static String url = "jdbc:mysql://localhost:3306/ytx_quizzes?useUnicode=true&characterEncoding=UTF8&zeroDateTimeBehavior=convertToNull&autoReconnect=true&idleConnectionTestPeriod=120&preferredTestQuery=select now()&serverTimezone=UTC";
//    private static String userName = "root";
//    private static String password = "root";

//    private static String url = "jdbc:mysql://mysql.ytx.com/ytx_maintenance?useUnicode=true&characterEncoding=UTF8&zeroDateTimeBehavior=convertToNull&autoReconnect=true&idleConnectionTestPeriod=120&preferredTestQuery=select now()";
//    private static String userName = "maintenance";
//    private static String password = "maintenance";

//    private static String url = "jdbc:mysql://mysql.ytx.com/ekaobang?useUnicode=true&characterEncoding=UTF8&zeroDateTimeBehavior=convertToNull&autoReconnect=true&idleConnectionTestPeriod=120&preferredTestQuery=select now()";
//    private static String userName = "ekaobang";
//    private static String password = "ekaobang";


  private static String url = "jdbc:mysql://127.0.0.1/bussiness_system?useUnicode=true&characterEncoding=UTF8&zeroDateTimeBehavior=convertToNull&autoReconnect=true&idleConnectionTestPeriod=120&preferredTestQuery=select now()&serverTimezone=UTC";
  private static String userName = "root";
  private static String password = "root";

  public static void main(String[] args) throws IOException {
    //项目路径， 比如后台管理:D:\project\learning\business_admin
//        String fileDir = "D:\\project\\ekaobang\\admin";
    String fileDir = "D:\\develop\\bussiness_system";
    String tableName = "m_user";
    PackageName packageName = generatePackageNames("com.mowen", "user");
    int singleDialog = 1; //1 表示弹框单列显示 ， 其他的表示双列显示

//    generateJson(tableName);
    generate(tableName, packageName, fileDir, singleDialog);
  }

  private static void generateJson(String table) {
    try {
      Table table1 = getTable(table);
      if (table1 == null) {
        return;
      }
      List<TableColumn> columns = table1.getColumnList();
      if (columns == null) {
        logger.info(table + "，的column== null");
        return;
      }
      String property =
          System.getProperty("user.dir") + "/generate/src/main/resources/velocity/index.json";
      System.out.println(property);

      List<ViewType> viewTypes = new ArrayList<>();
      ViewType viewType;
      for (TableColumn column : columns) {
        System.out.println(column);
        viewType = new ViewType();
        viewType.setName(column.getComment());
        viewType.setField(column.getFieldName());
        viewType.setType(1);
        viewTypes.add(viewType);
      }
      String json = StringUtil.toJSONString(viewTypes);
      BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(property));
      outputStream.write(json.getBytes());
      outputStream.flush();
      outputStream.close();
    } catch (Exception e) {
      e.printStackTrace();
      logger.error(e.getMessage(), e);
    }
  }

  private static void generate(String tableName, PackageName packageName, String fileDir,
      int isSingleDialog) {
    try {
      Table table = getTable(tableName);
      table.setSingleDialog(isSingleDialog);
      if (table == null) {
        return;
      }
      setViewTypes(table);
      confirmVmType(table);

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
      writeVm(fileDir, table);

      //6 generate service
      writeService(packageName, fileDir, table);

    } catch (Exception e) {
      e.printStackTrace();
      logger.error(e.getMessage(), e);
    }
  }

  private static void setViewTypes(Table table){
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
      viewType.setName(column.getComment());
      viewType.setField(column.getFieldName());
      viewType.setType(1);
      viewTypes.add(viewType);
    }
    table.setViewTypes(viewTypes);
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
      if(viewTypes != null){
        table.setViewTypes(viewTypes);
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.error(e.getMessage(), e);
    }
  }

  private static void writeDomain(PackageName packageName, String fileDir, Table table)
      throws IOException {
    String domain = TemplateUtil.generateDomain(packageName.getDomainPackageName(), table);
    String domainFir =
        fileDir + "/src/main/java/" + packageName.getDomainPackageName().replace(".", "/");
    File domainFileDir = new File(domainFir);
    if (!domainFileDir.exists()) {
      domainFileDir.mkdirs();
    }
    File domainFile = new File(domainFir, table.getDomainClassName() + ".java");
    FileUtils.write(domainFile, domain, "utf-8");
  }

  private static void writeDao(PackageName packageName, String fileDir, Table table)
      throws IOException {
    String dao = TemplateUtil
        .generateDao(packageName.getDaoPackageName(), packageName.getDomainPackageName(), table);
    String daoDir = fileDir + "/src/main/java/" + packageName.getDaoPackageName().replace(".", "/");
    File daoFileDir = new File(daoDir);
    if (!daoFileDir.exists()) {
      daoFileDir.mkdirs();
    }
    File daoFile = new File(daoDir, table.getDomainClassName() + "Mapper.java");
    FileUtils.write(daoFile, dao, "utf-8");
  }

  private static void writeMapper(PackageName packageName, String fileDir, Table table)
      throws IOException {
    String xml = TemplateUtil
        .generateXml(packageName.getDaoPackageName(), packageName.getDomainPackageName(), table);
    String mapperDir = fileDir + "/src/main/resources/mapper/";
    File mapperFileDir = new File(mapperDir);
    if (!mapperFileDir.exists()) {
      mapperFileDir.mkdirs();
    }
    File xmlFile = new File(mapperDir, table.getDomainClassName() + ".xml");
    FileUtils.write(xmlFile, xml, "utf-8");
  }

  private static void writeController(PackageName packageName, String fileDir, Table table)
      throws IOException {
    String controllerDir =
        fileDir + "/src/main/java/" + packageName.getControllerPackageName().replace(".", "/");
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

  private static void writeVm(String fileDir, Table table) throws IOException {
    String vm = TemplateUtil.generateVm(table);
    String vmDir = fileDir + "/src/main/webapp/WEB-INF/ftl/" + table.getDomainObjName();
    File vmFileDir = new File(vmDir);
    if (!vmFileDir.exists()) {
      vmFileDir.mkdirs();
    }
    File vmFile = new File(vmDir, "index.ftl");
    FileUtils.write(vmFile, vm, "utf-8");
  }

  private static void writeService(PackageName packageName, String fileDir, Table table)
      throws IOException {
    String service = TemplateUtil
        .generateService(packageName.getServicePackageName(), packageName.getiServicePackageName(),
            packageName.getDaoPackageName(), packageName.getDomainPackageName(), packageName.getPrefix(), table);
    String iService = TemplateUtil
        .generateIService(packageName.getiServicePackageName(), packageName.getDaoPackageName(),
            packageName.getDomainPackageName(),packageName.getPrefix(),  table);
    String interfaceDir = fileDir + "/src/main/java/" + packageName.getiServicePackageName().replace(".", "/");
    String serviceFir = fileDir + "/src/main/java/" + packageName.getServicePackageName().replace(".", "/");
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
