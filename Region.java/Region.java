package com.gie.admin.region.domain;
import java.util.Date;
import java.math.BigDecimal;
import java.io.Serializable;

/**
* @description: 地区管理，人员分地区
* @author: mayn
* @createTime: 2020-01-11 14:50:01
*/
public class Region implements Serializable{

    private static final long serialVersionUID = -1L;

    
        /**
        * ID
        */
        private String id;
    
        /**
        * 来源
        */
        private String source;
    
        /**
        * 名称
        */
        private String name;
    
        /**
        * 类型|1:国内,2:国外|select
        */
        private String type;
    
        /**
        * 状态|1:在线,2:离线|select
        */
        private String status;
    
        /**
        * 内容||text
        */
        private String content;
    
        /**
        * 创建时间
        */
        private String createTime;
    
        /**
        * 更新时间
        */
        private String updateTime;
    
            public String getId(){
            return id;
        }

        public void setId(String id){
            this.id = id;
        }

            public String getSource(){
            return source;
        }

        public void setSource(String source){
            this.source = source;
        }

            public String getName(){
            return name;
        }

        public void setName(String name){
            this.name = name;
        }

            public String getType(){
            return type;
        }

        public void setType(String type){
            this.type = type;
        }

            public String getStatus(){
            return status;
        }

        public void setStatus(String status){
            this.status = status;
        }

            public String getContent(){
            return content;
        }

        public void setContent(String content){
            this.content = content;
        }

            public String getCreateTime(){
            return createTime;
        }

        public void setCreateTime(String createTime){
            this.createTime = createTime;
        }

            public String getUpdateTime(){
            return updateTime;
        }

        public void setUpdateTime(String updateTime){
            this.updateTime = updateTime;
        }

    }