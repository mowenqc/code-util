package com.mowen.templete;

import com.mowen.domain.Config;
import com.mowen.domain.DataInfo;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Properties;

/**
 * Created by  on 2017/10/13.
 */
public class TemplateUtil {
    private static VelocityEngine ve;

    static {
        ve = new VelocityEngine();
        Properties p = new Properties();
        p.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        p.setProperty(Velocity.ENCODING_DEFAULT, "UTF-8");
        p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        ve.init(p);
    }

    public static String generateController(Config config, DataInfo dataInfo) {
        Template t = ve.getTemplate("velocity/controller.vm");
        return mergeDataToVelocity(config, dataInfo, t);
    }

    public static String mergeDataToVelocity(Config config, DataInfo dataInfo, Template t) {
        VelocityContext context = new VelocityContext();
        context.put("table", dataInfo.getTable());
        context.put("controllerPackageName", dataInfo.getControllerPackage());
        context.put("daoPackageName", dataInfo.getDaoPackage());
        context.put("domainPackageName", dataInfo.getDaoPackage());
        context.put("iServicePackageName", dataInfo.getInterfacePackage());
        context.put("config", config);
        context.put("dataInfo", dataInfo);
        StringWriter writer = new StringWriter();
        t.merge(context, writer);
        return writer.toString();
    }

    public static String generateIService(Config config, DataInfo dataInfo) {
        Template t = ve.getTemplate("velocity/iservice.vm");
        return mergeDataToVelocity(config, dataInfo, t);
    }

    public static String generateService(Config config, DataInfo dataInfo) {
        Template t = ve.getTemplate("velocity/service.vm");
        return mergeDataToVelocity(config, dataInfo, t);
    }

    public static String generateXml(Config config, DataInfo dataInfo) {
        Template t = ve.getTemplate("velocity/mybatisXml.vm");
        return mergeDataToVelocity(config, dataInfo, t);
    }

    public static String generateDao(Config config, DataInfo dataInfo) {
        Template t = ve.getTemplate("velocity/dao.vm");
        return mergeDataToVelocity(config, dataInfo, t);
    }

    public static String generateView(Config config, DataInfo dataInfo) {
        Template t = ve.getTemplate("velocity/index.vm");
        return mergeDataToVelocity(config, dataInfo, t);
    }

    public static String generateDomain(Config config, DataInfo dataInfo) {
        Template t = ve.getTemplate("velocity/domain.vm");
        return mergeDataToVelocity(config, dataInfo, t);
    }

    public static String generate(Config config, DataInfo dataInfo, String velocityTemplate) {
        Template t = ve.getTemplate(velocityTemplate);
        return mergeDataToVelocity(config, dataInfo, t);
    }

    public static void writeFile(DataLoader loader) {
        Config config = loader.getConfig();
        List<DataInfo> dataInfoList = loader.getDataInfoList();
        if (dataInfoList == null || dataInfoList.size() == 0) {
            System.out.println("未加载到任何文件");
            return;
        }

        for (DataInfo dataInfo : dataInfoList) {
            writeController(config, dataInfo);
            writeInterface(config, dataInfo);
            writeService(config, dataInfo);
            writeDao(config, dataInfo);
            writeMapper(config, dataInfo);
            writeView(config, dataInfo);
            writeDomain(config, dataInfo);
        }
    }

    private static void writeDomain(Config config, DataInfo dataInfo) {

        String filePath = config.getProjectPath() + File.separator + validateModule(config.getModuleName(), config.getDomainModule())
                + "/src/main/java" + File.separator + dataInfo.getDomainPackage().replace(".", File.separator);
        String fileName = dataInfo.getTable().getDomainClassName() + ".java";
        finalWriter(filePath, fileName, generateDomain(config, dataInfo));

    }

    private static void writeController(Config config, DataInfo dataInfo) {
        if (isModuleExist(config.getModuleName(), config.getControllerModule())) {
            String filePath = config.getProjectPath() + File.separator + validateModule(config.getModuleName(), config.getControllerModule())
                    + "/src/main/java" + File.separator + dataInfo.getControllerPackage().replace(".", File.separator);
            String fileName = dataInfo.getTable().getDomainClassName() + "Controller.java";
            finalWriter(filePath, fileName, generateController(config, dataInfo));
        }
    }

    private static boolean isModuleExist(String commonModule, String module) {
        String module1 = validateModule(commonModule, module);
        if (module1 == null || module1 == "") {
            return false;
        }
        return true;
    }

    private static void writeInterface(Config config, DataInfo dataInfo) {
        if (isModuleExist(config.getModuleName(), config.getInterfaceModule())) {
            String filePath = config.getProjectPath() + File.separator + validateModule(config.getModuleName(), config.getInterfaceModule())
                    + "/src/main/java" + File.separator + dataInfo.getInterfacePackage().replace(".", File.separator);
            String fileName = "I" + dataInfo.getTable().getDomainClassName() + "Service.java";
            finalWriter(filePath, fileName, generateIService(config, dataInfo));
        }
    }

    private static void writeService(Config config, DataInfo dataInfo) {
        if (isModuleExist(config.getModuleName(), config.getServiceModule())) {
            String filePath = config.getProjectPath() + File.separator + validateModule(config.getModuleName(), config.getServiceModule())
                    + "/src/main/java" + File.separator + dataInfo.getServicePackage().replace(".", File.separator);
            String fileName = dataInfo.getTable().getDomainClassName() + "Service.java";
            finalWriter(filePath, fileName, generateService(config, dataInfo));
        }
    }

    private static void writeDao(Config config, DataInfo dataInfo) {
        if (isModuleExist(config.getModuleName(), config.getDaoModule())) {
            String filePath = config.getProjectPath() + File.separator + validateModule(config.getModuleName(), config.getDaoModule())
                    + "/src/main/java" + File.separator + dataInfo.getDaoPackage().replace(".", File.separator);
            String fileName = dataInfo.getTable().getDomainClassName() + "Mapper.java";
            finalWriter(filePath, fileName, generateDao(config, dataInfo));
        }
    }

    private static void writeMapper(Config config, DataInfo dataInfo) {
        if (isModuleExist(config.getModuleName(), config.getMapperModule())) {
            String filePath = config.getProjectPath() + File.separator + validateModule(config.getModuleName(), config.getMapperModule())
                    + "/src/main/resources/mapper";
            String fileName = dataInfo.getTable().getDomainClassName() + ".xml";
            finalWriter(filePath, fileName, generateXml(config, dataInfo));
        }
    }

    private static void writeView(Config config, DataInfo dataInfo) {
        if (config.getViewTemplate() == null) {
            config.setViewTemplate("tpl");
        }
        if (isModuleExist(config.getModuleName(), config.getViewModule())) {
            String filePath = config.getProjectPath() + File.separator + validateModule(config.getModuleName(), config.getViewModule())
                    + "/src/main/resources/templates" + File.separator + dataInfo.getTable().getDomainObjName();
            String fileName = null;
            if ("tpl".equals(config.getViewTemplate())) {
                fileName = dataInfo.getTable().getDomainClassName() + ".vm";
            }  else if ("ftl".equals(config.getViewTemplate())) {
                fileName = dataInfo.getTable().getDomainClassName() + ".ftl";
            }  else if ("ftlh".equals(config.getViewTemplate())) {
                fileName = "index.ftlh";
            }
            else {
                fileName = dataInfo.getTable().getDomainClassName() + ".jsp";
            }
            finalWriter(filePath, fileName, generateView(config, dataInfo));
        }
    }

    private static String validateModule(String commonModule, String module) {
        if (module != null && module != "") {
            return module;
        }
        return commonModule;
    }

    private static void finalWriter(String filePath, String fileName, String content) {
        File path = new File(filePath);
        if (!path.exists()) {
            path.mkdirs();
        }
        File file = new File(filePath, fileName);
        try {
            FileUtils.write(file, content, "utf-8");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

}
