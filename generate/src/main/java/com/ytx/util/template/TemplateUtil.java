package com.ytx.util.template;

import com.ytx.common.util.CollectionUtil;
import com.ytx.common.util.ObjectUtil;
import com.ytx.util.Table;
import com.ytx.util.TableColumn;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.Properties;

/**
 * Created by chutao on 2017/10/13.
 */
public class TemplateUtil {
    private static VelocityEngine ve;
    static{
        ve = new VelocityEngine();
        Properties p = new Properties();
        p.setProperty("file.resource.loader.class","org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        p.setProperty(Velocity.ENCODING_DEFAULT, "UTF-8");
        p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        ve.init(p);
    }

    public static String generateController(
            String controllerPackageName,
            String iServicePackageName,
            String daoPackageName,
            String domainPackageName,
            String prefix,
            Table table
    ){
        Template t = ve.getTemplate("velocity/controller.vm");
        VelocityContext context = new VelocityContext();
        context.put("table",table);
        context.put("controllerPackageName",controllerPackageName);
        context.put("daoPackageName",daoPackageName);
        context.put("domainPackageName",domainPackageName);
        context.put("iServicePackageName",iServicePackageName);
        context.put("prefix",prefix);
        StringWriter writer = new StringWriter();
        t.merge(context, writer);
        return writer.toString();
    }

    public static String generateIService(
            String iServicePackageName,
            String daoPackageName,
            String domainPackageName,String prefix,
            Table table
    ){
        Template t = ve.getTemplate("velocity/iservice.vm");
        VelocityContext context = new VelocityContext();
        context.put("table",table);
        context.put("prefix",prefix);
        context.put("daoPackageName",daoPackageName);
        context.put("domainPackageName",domainPackageName);
        context.put("iServicePackageName",iServicePackageName);
        StringWriter writer = new StringWriter();
        t.merge(context, writer);
        return writer.toString();
    }

    public static String generateService(
            String servicePackageName,
            String iServicePackageName,
            String daoPackageName,
            String domainPackageName,String prefix,
            Table table
    ){
        Template t = ve.getTemplate("velocity/service.vm");
        VelocityContext context = new VelocityContext();
        context.put("table",table);
        context.put("daoPackageName",daoPackageName);
        context.put("domainPackageName",domainPackageName);
        context.put("servicePackageName",servicePackageName);
        context.put("iServicePackageName",iServicePackageName);
        context.put("prefix", prefix);
        StringWriter writer = new StringWriter();
        t.merge(context, writer);
        return writer.toString();
    }

    public static String generateXml(String daoPackageName,String domainPackageName,Table table){
        Template t = ve.getTemplate("velocity/mybatisXml.vm");
        VelocityContext context = new VelocityContext();
        context.put("table",table);
        context.put("daoPackageName",daoPackageName);
        context.put("domainPackageName",domainPackageName);
        StringWriter writer = new StringWriter();
        t.merge(context, writer);
        return writer.toString();
    }

    public static String generateDao(String daoPackageName,String domainPackageName,Table table){
        Template t = ve.getTemplate("velocity/dao.vm");
        VelocityContext context = new VelocityContext();
        context.put("table",table);
        context.put("daoPackageName",daoPackageName);
        context.put("domainPackageName",domainPackageName);
        StringWriter writer = new StringWriter();
        t.merge(context, writer);
        return writer.toString();
    }

    public static String generateVm(Table table){
        Template t = ve.getTemplate("velocity/index.vm");
        VelocityContext context = new VelocityContext();
        Table deepCopy = ObjectUtil.deepCopy(table);
        String id = "id";
        String createTime = "createTime";
        String updateTime = "updateTime";
        Iterator<TableColumn> iterator = deepCopy.getColumnList().iterator();
        while (iterator.hasNext()){
            TableColumn column = iterator.next();
            if(id.equals(column.getFieldName()) || createTime.equals(column.getFieldName()) || updateTime.equals(column.getFieldName())){
                iterator.remove();
            }
        }
        if(CollectionUtil.isNotEmpty(deepCopy.getColumnList())){
            deepCopy.setColumnCount(deepCopy.getColumnList().size());
        }
        context.put("table",deepCopy);
        StringWriter writer = new StringWriter();
        t.merge(context, writer);
        return writer.toString();
    }

    public static String generateDomain(String packageName,Table table){
        Template t = ve.getTemplate("velocity/domain.vm");
        VelocityContext context = new VelocityContext();
        context.put("table",table);
        context.put("packageName",packageName);
        StringWriter writer = new StringWriter();
        t.merge(context, writer);
        return writer.toString();
    }

}
