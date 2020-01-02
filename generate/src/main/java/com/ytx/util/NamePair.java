package com.ytx.util;

import java.io.Serializable;

/***
 * @description : 
 * @author: mowen
 * @create_time: 2019/12/3 12:16
 * @since: v1.0
 */
public class NamePair implements Serializable {
    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
