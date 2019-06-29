package com.ytx.util;

import java.io.Serializable;

/***
 * desc  :
 * author:mayn
 * create_time: 2018/9/5 10:08
 * project_name : util_parent
 */
public class ViewType implements Serializable {

    /**
     * 1. 文本, 2 数字，3 switch，4，radio，5，select, 6,checkbox,7.textarea,8,date
     */
    private int type;

    /**
     * 标签
     */
    private String name;

    /**
     * 属性
     */
    private String field;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    @Override
    public String toString() {
        return "ViewType{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", field='" + field + '\'' +
                '}';
    }
}
