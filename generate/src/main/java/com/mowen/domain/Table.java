package com.mowen.domain;

import java.io.Serializable;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * Created time 2017/10/13.
 */
public class Table implements Serializable {
    //表名tableName以及根据表名生成的domain类名和对象名
    private String tableName;

    //对象类名称
    private String domainClassName;

    //对象名称
    private String domainObjName;
    //表字段列表
    private List<TableColumn> columnList;

    private int columnCount;

    private String comment;

    private Config config;


    private List<ViewType> viewTypes = new ArrayList<>();


    public Table(DatabaseMetaData databaseMetaData, ResultSet resultSet, ResultSetMetaData metaData, Config config) throws SQLException {
        Objects.requireNonNull(metaData);
        setConfig(config);
        getTableInfo(metaData);
        getColumnInfo(databaseMetaData, metaData, resultSet);
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public List<ViewType> getViewTypes() {
        return viewTypes;
    }

    public void setViewTypes(List<ViewType> viewTypes) {
        this.viewTypes = viewTypes;
    }

    private void getColumnInfo(DatabaseMetaData databaseMetaData, ResultSetMetaData metaData, ResultSet resultSet) throws SQLException {

        //获取主键
        ResultSet primaryKeys = databaseMetaData.getPrimaryKeys(config.getSchema(), config.getSchema(), tableName);
        Map<String, String> priKey = new HashMap<>();
        if (primaryKeys != null) {
            while (primaryKeys.next()) {
                String key = primaryKeys.getString("COLUMN_NAME").toLowerCase();
                priKey.put(key, key);
            }
        }

        //获取索引
        Map<String, String> uniqueKey = new HashMap<>();
        Map<String, String> indexKey = new HashMap<>();
        ResultSet indexInfo = databaseMetaData.getIndexInfo(null, null, tableName, false, false);
        if(indexInfo != null){
            while (indexInfo.next()){
                String columnName = indexInfo.getString("COLUMN_NAME").toLowerCase();
                System.out.println("获取索引字段：" + columnName);
                boolean nonUnique = indexInfo.getBoolean("NON_UNIQUE");
                if(nonUnique){
                    uniqueKey.put(columnName, columnName);
                }
                else {
                    indexKey.put(columnName, columnName);
                }
            }
        }


        List<TableColumn> tableColumns = new ArrayList<>();
        int i = 1;
        while (resultSet.next()) {
            String columnName = resultSet.getString("COLUMN_NAME").toLowerCase();
            String remarks = resultSet.getString("REMARKS");
            String columnType = metaData.getColumnTypeName(i);
            boolean isNullable = metaData.isNullable(i) == ResultSetMetaData.columnNoNulls ? false : true;
            TableColumn column = new TableColumn(columnName, columnType, isNullable, remarks, config);
            if (indexKey.get(columnName) != null) {
                column.setPriKey(1);
            }

            if (uniqueKey.get(columnName) != null) {
                column.setPriKey(2);
            }

            if (priKey.get(columnName) != null) {
                column.setPriKey(3);
            }
            tableColumns.add(column);
            i++;
        }
        setColumnCount(i);
        Collections.sort(tableColumns,(o1,o2)-> o2.getPriKey() - o1.getPriKey());
        this.columnList = tableColumns;
    }

    private void getTableInfo(ResultSetMetaData metaData) throws SQLException {
        this.tableName = metaData.getTableName(1);
        this.domainClassName = generateClassName(tableName);
        this.domainObjName = TableColumn.firstLetterToLower(domainClassName);
    }

    private String generateClassName(String tableName) {
        String[] tmpArray = checkTableName(tableName);
        StringBuilder sb = new StringBuilder();
        for (int i = config.getDeleteSplit(); i < tmpArray.length; i++) {
            sb.append(TableColumn.firstLetterToUpper(tmpArray[i]));
        }
        return sb.toString();
    }

    private String[] checkTableName(String tableName) {
        String[] tmpArray = TableColumn.checkName(tableName);
        if (tmpArray == null || tmpArray.length == 0) {
            throw new IllegalArgumentException("表名有误，请用正确的命名方式");
        }
        return tmpArray;
    }

    public String getTableName() {
        return tableName;
    }

    public String getDomainClassName() {
        return domainClassName;
    }

    public List<TableColumn> getColumnList() {
        return columnList;
    }

    public String getDomainObjName() {
        return domainObjName;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setDomainClassName(String domainClassName) {
        this.domainClassName = domainClassName;
    }

    public void setDomainObjName(String domainObjName) {
        this.domainObjName = domainObjName;
    }

    public void setColumnList(List<TableColumn> columnList) {
        this.columnList = columnList;
    }

}
