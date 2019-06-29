package com.ytx.util;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by chutao on 2017/10/13.
 */
public class Table implements Serializable {
    //表名tableName以及根据表名生成的domain类名和对象名
    private String tableName;
    private String domainClassName;
    private String domainObjName;
    //表字段列表
    private List<TableColumn> columnList;

    private int singleDialog = 1;

    private int columnCount;

    private List<ViewType> viewTypes = new ArrayList<>();

    public Table(ResultSet resultSet, ResultSetMetaData metaData) throws SQLException{
        Objects.requireNonNull(metaData);
        getTableInfo(metaData);
        getColumnInfo(metaData, resultSet);
    }

    private void checkIdExist(List<TableColumn> columns){
        for(TableColumn column : columns){
            if(column.getFieldName().equals("id") && column.getFieldType().equals("long")){
                return;
            }
        }
        throw new IllegalStateException("表定义中缺少类型为bigint,名为id的自增主键");
    }

    public List<ViewType> getViewTypes() {
        return viewTypes;
    }

    public void setViewTypes(List<ViewType> viewTypes) {
        this.viewTypes = viewTypes;
    }

    private void getColumnInfo(ResultSetMetaData metaData, ResultSet resultSet) throws SQLException{
        int size = metaData.getColumnCount();
        String[] columnNames = new String[size];
        String[] columnTypes = new String[size];
        String []columnComment = new String[size];
        int[] columnSize = new int[size];
        boolean[] isNullables = new boolean[size];
        Map<String, String> remark = new HashMap<>();
        while (resultSet.next()){
            String key = resultSet.getString("COLUMN_NAME").toLowerCase();
            String value = resultSet.getString("REMARKS");
            remark.put(key, value);
        }
        for(int i = 0;i < size;i++){
            String name = metaData.getColumnName(i + 1);
            System.out.println(name);
            columnNames[i] = name;
            columnTypes[i] = metaData.getColumnTypeName(i + 1);
            columnComment[i] =  remark.get(name);
            columnSize[i] = metaData.getColumnDisplaySize(i + 1);
            isNullables[i] = metaData.isNullable(i + 1) == ResultSetMetaData.columnNoNulls ? false : true;
        }
        List<TableColumn> tableColumns = new ArrayList<>();
        for(int i = 0;i < columnNames.length;i++){
            String columnName = columnNames[i];
            String columnType = columnTypes[i].toLowerCase();
            boolean isNullable = isNullables[i];
            TableColumn column = new TableColumn(columnName,columnType,isNullable,columnComment[i]);
            tableColumns.add(column);
        }
        this.columnList = tableColumns;
    }

    private void getTableInfo(ResultSetMetaData metaData) throws SQLException{
        this.tableName = metaData.getTableName(1);
        this.domainClassName = generateClassName(tableName);
        this.domainObjName = TableColumn.firstLetterToLower(domainClassName);
    }

    private String generateClassName(String tableName){
        String[] tmpArray = checkTableName(tableName);
        StringBuilder sb = new StringBuilder();
        for(int i = 1;i < tmpArray.length;i++){
            sb.append(TableColumn.firstLetterToUpper(tmpArray[i]));
        }
        return sb.toString();
    }

    private String[] checkTableName(String tableName){
        String[] tmpArray = TableColumn.checkName(tableName);
        if(!tmpArray[0].equals("m")){
            throw new IllegalArgumentException("表名应以ytx开头");
        }
        if(tmpArray.length == 1){
            throw new IllegalArgumentException("表名不能是ytx");
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

    public int getSingleDialog() {
        return singleDialog;
    }

    public void setSingleDialog(int singleDialog) {
        this.singleDialog = singleDialog;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }
}
