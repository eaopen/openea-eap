package org.openea.eap.extj.database.sql.model;

import lombok.Data;

/**
 * 类功能
 *
 * 
 */
@Data
public class DbStruct {

    /* ============= 通用 ============= */
    /**
     * 用户名
     */
    private String userName;

    /**
     * 表空间
     */
    private String dbTableSpace;

    /* ============= Mysql ============= */
    /**
     * 库名
     * Mysql目前没有使用到模式
     */
    private String mysqlDbName;

    /* ============= Oracle ============= */
    /**
     * 模式
     * Oracle目前没有使用库名
     * 与用户同名
     */
    private String oracleDbSchema;

    /**
     * Oracle 额外参数
     */
    private String oracleParam;
    /* ============= SqlServer ============= */
    /**
     * 库名
     */
    private String sqlServerDbName;
    /**
     * 模式（暂时不使用）
     * 默认：dbo
     * 可选：guest
     */
    private String sqlServerDbSchema = "dbo";

    /* ============= Dm ============= */
    /**
     * 模式
     * Dm目前没有使用库名
     * 与用户同名
     */
    private String dmDbSchema;

    /* ============= KingBase ============= */

    /**
     * 库名
     */
    private String kingBaseDbName;
    /**
     * 模式
     * 默认：public
     */
    private String kingBaseDbSchema = "public";

    /* ============= PostGre ============= */

    /**
     * 库名
     */
    private String postGreDbName;
    /**
     * 模式（暂时不使用）
     * 默认：public
     */
    private String postGreDbSchema = "public";


}
