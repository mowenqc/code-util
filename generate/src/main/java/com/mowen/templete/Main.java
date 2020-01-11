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
        String url = "jdbc:mysql://mysql.ytx.com/gie?useUnicode=true&characterEncoding=UTF8&zeroDateTimeBehavior=convertToNull&autoReconnect=true&idleConnectionTestPeriod=120&preferredTestQuery=select now()&serverTimezone=UTC";
        String userName = "root";
        String password = "ytxroot";
        String fileDir = "D:\\develop\\tmp-project\\GrandInternationalEducation";
        String tableName = "gie_region";
        String groupId = "com.gie.admin";
        String pName= "region";
        String schema = "gie";
        Config config = Config.builder().controllerModule("admin").daoModule("admin").mapperModule("admin").
                serviceModule("admin").viewTemplate("tpl").deleteSplit(1).schema(schema).
                interfaceModule("admin").viewModule("admin").domainModule("admin").groupId(groupId).packageName(pName).url(url).userName(userName).
                password(password).projectPath(fileDir).tableName(tableName).build();

        DataLoader dataLoader = new DataLoader(config);
        TemplateUtil.writeFile(dataLoader);
    }
}
