package com.mowen.templete;

import com.mowen.domain.*;

import java.sql.*;
import java.util.*;

/***
 * @description : 
 * @author: mowen
 * @create_time: 2020/1/10 13:47
 * @since: v1.0
 */
public class DataLoader {
    private void init() {
        try {
            Map<String, String> tableRemark = new HashMap<>();
            Connection connection = DriverManager.getConnection(config.getUrl(), config.getUserName(), config.getPassword());
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            String tableNames = config.getTableName();
            String[] tableNameArr = tableNames.split(",");
            if (tableNameArr == null || tableNameArr.length == 0) {
                System.out.println("没有数据库表");
                return;
            }

            for (String tableName : tableNameArr) {
                getTableComment(tableRemark, connection, tableName);
                setDataInfo(tableRemark, connection, databaseMetaData, tableName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setDataInfo(Map<String, String> tableRemark, Connection connection, DatabaseMetaData databaseMetaData, String tableName) throws SQLException {
        ResultSet tableRet = databaseMetaData.getColumns(null, "%", tableName, "%");
        if (tableRet == null) {
            return;
        }
        String sql = "select * from " + tableName + " limit 1";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSetMetaData metaData = statement.getMetaData();

        Table table = new Table(databaseMetaData, tableRet, metaData,config);
        table.setComment(tableRemark.get(tableName));
        setViewTypes(table);
        DataInfo dataInfo = new DataInfo(config, table);
        dataInfoList.add(dataInfo);
    }

    private static void setViewTypes(Table table) {
        List<TableColumn> columns = table.getColumnList();
        if (columns == null) {
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
            System.out.println("xx=" + comment);
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

    private void getTableComment(Map<String, String> tableRemark, Connection connection, String tableName) throws SQLException {
        String tableSql = "SHOW CREATE TABLE " + tableName;
        PreparedStatement statement = connection.prepareStatement(tableSql);
        if(statement == null){
            System.out.println(tableSql + " is not exist");
        }
        ResultSet executeQuery = statement.executeQuery();
        if(executeQuery == null){
            System.out.println(tableSql + " has non result set");
        }
        while (executeQuery.next()){
            String string = executeQuery.getString(2);
            tableRemark.put(tableName, parseComment(string));
        }
    }

    private String parseComment(String all){
        if(all == null){
            return "";
        }
        String comment = null;
        int index = all.indexOf("COMMENT='");
        if (index < 0) {
            return "";
        }
        comment = all.substring(index + 9);
        comment = comment.substring(0, comment.length() - 1);
        return comment;
    }

    private Config config;

    private List<DataInfo> dataInfoList;


    public DataLoader(Config config) {
        this.config = config;
        dataInfoList = new ArrayList<>();
        init();
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public List<DataInfo> getDataInfoList() {
        return dataInfoList;
    }

    public void setDataInfoList(List<DataInfo> dataInfoList) {
        this.dataInfoList = dataInfoList;
    }
}
