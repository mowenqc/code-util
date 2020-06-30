package com.mowen.templete;

import com.mowen.domain.*;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by  on 2017/10/13.
 */
public class Main {

    private static Logger logger = Logger.getLogger(Main.class);

    private static String driverName = "com.mysql.jdbc.Driver";




    public static void main(String[] args) throws IOException {
        String url = "jdbc:mysql://localhost:3306/sale_system?useUnicode=true&characterEncoding=UTF8&zeroDateTimeBehavior=convertToNull&autoReconnect=true&idleConnectionTestPeriod=120&preferredTestQuery=select now()&serverTimezone=UTC";
        String userName = "root";
        String password = "mowen123";
        String fileDir = "G:\\develop\\sale-system";
        String tableName = "m_customer";
        String groupId = "com.exam.sales.admin";
        String pName= "customer";
        String schema = "sale_system";
        String module = "sale-admin";
        Config config = Config.builder().controllerModule(module).daoModule(module).mapperModule(module).
                serviceModule(module).deleteSplit(0).schema(schema).viewTemplate("ftlh").viewModule(module).
                interfaceModule(module).domainModule(module).groupId(groupId).packageName(pName).url(url).userName(userName).
                password(password).projectPath(fileDir).tableName(tableName).build();

        DataLoader dataLoader = new DataLoader(config);
        TemplateUtil.writeFile(dataLoader);
    }
}
