package org.openea.eap.extj.database.model;

import org.openea.eap.extj.database.model.entity.DbLinkEntity;

import java.io.Serializable;

/**
 * 
 */
public class TenantLinkModel implements Serializable {
    /**
     * id
     */
    public String id;

    /**
     * 数据库名
     */
    public String serviceName;

    /**
     * 用户名
     */
    public String userName;

    /**
     * 端口
     */
    public String port;

    /**
     * 连接名称
     */
    public String fullName;

    /**
     * 主机地址
     */
    public String host;

    /**
     * 密码
     */
    public String password;

    /**
     * 模式
     */
    public String dbSchema;

    /**
     * 连接配置（0：主，1：从）
     */
    public Integer configType;

    /**
     * 数据库类型
     */
    public String dbType;

    /**
     * 自定义连接语句
     */
    public String connectionStr;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDbSchema() {
        return dbSchema;
    }

    public void setDbSchema(String dbSchema) {
        this.dbSchema = dbSchema;
    }

    public Integer getConfigType() {
        return configType;
    }

    public void setConfigType(Integer configType) {
        this.configType = configType;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getConnectionStr() {
        return connectionStr;
    }

    public void setConnectionStr(String connectionStr) {
        this.connectionStr = connectionStr;
    }

    public DbLinkEntity toDbLinkEntity(){
        DbLinkEntity dbLinkEntity = new DbLinkEntity();
        dbLinkEntity.setId(getId());
        dbLinkEntity.setDbType(getDbType());
        dbLinkEntity.setPrepareUrl(getConnectionStr());
        dbLinkEntity.setUserName(getUserName());
        dbLinkEntity.setPassword(getPassword());
        dbLinkEntity.setPort(Integer.valueOf(getPort()));
        dbLinkEntity.setDbName(getServiceName());
        dbLinkEntity.setDbSchema(getDbSchema());
        dbLinkEntity.setHost(getHost());
        return dbLinkEntity;
    }
}
