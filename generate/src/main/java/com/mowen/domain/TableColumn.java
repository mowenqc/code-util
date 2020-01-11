package com.mowen.domain;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by  on 2017/10/13.
 */
public class TableColumn implements Serializable {
    private String columnName;
    private String columnType;
    private boolean isNullable;
    //字段名和get,set方法名全部根据columnName,columnType生成
    private String fieldType;
    private String fieldName;
    private String getterMethodName;
    private String setterMethodName;
    private String comment;

    /**
     * 3 主键,2,唯一索引， 1 普通索引 0 普通字段
     */
    private int priKey;

    private ViewType viewType;

    private Config config;

    public TableColumn(String columnName, String columnType, boolean isNullable, String comment, Config config) {
        Objects.requireNonNull(columnName);
        Objects.requireNonNull(columnType);
        this.config = config;
        this.columnName = columnName;
        this.columnType = columnType;
        this.isNullable = isNullable;
        this.comment = comment;
        this.fieldName = generateFieldName(columnName);
        this.fieldType = generateFieldType(columnType, isNullable);
        generateGetterSetter(fieldName, fieldType);
    }

    public TableColumn(String columnName, String columnType, boolean isNullable) {
        Objects.requireNonNull(columnName);
        Objects.requireNonNull(columnType);
        this.columnName = columnName;
        this.columnType = columnType;
        this.isNullable = isNullable;
        this.fieldName = generateFieldName(columnName);
        this.fieldType = generateFieldType(columnType, isNullable);
        generateGetterSetter(fieldName, fieldType);
    }

    private void generateGetterSetter(String fieldName, String fieldType) {
        String tmp = firstLetterToUpper(fieldName);
        //getter
        if (fieldType.equals("boolean")) {
            this.getterMethodName = "is" + tmp;
        } else {
            this.getterMethodName = "get" + tmp;
        }
        //setter
        this.setterMethodName = "set" + tmp;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public String getFieldType() {
        return fieldType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public boolean isNullable() {
        return isNullable;
    }

    public String getGetterMethodName() {
        return getterMethodName;
    }

    public String getSetterMethodName() {
        return setterMethodName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ViewType getViewType() {
        return viewType;
    }

    public void setViewType(ViewType viewType) {
        this.viewType = viewType;
    }

    private String generateFieldType(String columnType, boolean isNullable) {
        switch (columnType) {
            case "bigint": {
                return "Long";
            }
            case "int":
            case "smallint":
            case "tinyint":
            case "bit":
            case "mediumint": {
                return "Integer";
            }
            case "timestamp":
            case "datetime":
            case "date":
            case "time":
            case "year":
                return "Date";
            case "numeric":
            case "decimal":
                return "BigDecimal";
            case "float":
                return "Float";
            case "double":
                return "Double";
            default:
                return "String";
        }
    }

    private String generateFieldName(String columnName) {
        String[] tmpArray = checkName(columnName);
        StringBuilder sb = new StringBuilder();
        sb.append(tmpArray[0]);
        for (int i = config.getDeleteSplit(); i < tmpArray.length; i++) {
            String word = tmpArray[i];
            word = firstLetterToUpper(word);
            sb.append(word);
        }
        return sb.toString();
    }

    public static String firstLetterToUpper(String word) {
        char prefixChar = word.charAt(0);
        char resultPrefixChar = Character.toUpperCase(prefixChar);
        word = word.replaceFirst(String.valueOf(prefixChar), String.valueOf(resultPrefixChar));
        return word;
    }

    public static String firstLetterToLower(String word) {
        char prefixChar = word.charAt(0);
        char resultPrefixChar = Character.toLowerCase(prefixChar);
        word = word.replaceFirst(String.valueOf(prefixChar), String.valueOf(resultPrefixChar));
        return word;
    }

    /***
     * 检查表名或者字段名的，如果检查成功，将"_"作为分隔符，将名称切分成字符串数组
     * @param name
     * @return
     */
    public static String[] checkName(String name) {
        if (name == null) {
            throw new NullPointerException("表名或字段名不能为空");
        }
        String[] tmpArray = name.split("_");
        if (tmpArray == null || tmpArray.length == 0) {
            throw new IllegalArgumentException("表名或字段名为空");
        }
        for (int i = 0; i < tmpArray.length; i++) {
            String word = tmpArray[i];
            if (word.length() == 0) {
                throw new IllegalArgumentException("表名或字段名中的单词不能为空");
            }
            if (!Character.isLetter(word.charAt(0))) {
                throw new IllegalArgumentException("表名或字段名中的单词首字母必须是字母");
            }
            /*if (!StringUtils.isAllLowerCase(word)) {
                throw new IllegalArgumentException("表名或字段名中的单词必须全部都是小写");
            }*/
        }
        return tmpArray;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public void setNullable(boolean nullable) {
        isNullable = nullable;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setGetterMethodName(String getterMethodName) {
        this.getterMethodName = getterMethodName;
    }

    public void setSetterMethodName(String setterMethodName) {
        this.setterMethodName = setterMethodName;
    }

    public int getPriKey() {
        return priKey;
    }

    public void setPriKey(int priKey) {
        this.priKey = priKey;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    @Override
    public String toString() {
        return "TableColumn{" +
                "columnName='" + columnName + '\'' +
                ", columnType='" + columnType + '\'' +
                ", isNullable=" + isNullable +
                ", fieldType='" + fieldType + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", getterMethodName='" + getterMethodName + '\'' +
                ", setterMethodName='" + setterMethodName + '\'' +
                ", comment='" + comment + '\'' +
                ", viewType=" + viewType +
                '}';
    }
}
