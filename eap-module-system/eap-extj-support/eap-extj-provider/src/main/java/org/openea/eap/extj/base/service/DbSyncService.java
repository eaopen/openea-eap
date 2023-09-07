package org.openea.eap.extj.base.service;

import java.util.List;
import java.util.Map;

public interface DbSyncService {

    /**
     * 数据表同步验证
     *
     * @param dbLinkIdFrom 数据库连接From
     * @param dbLinkIdTo   数据库连接To
     * @param table        表名
     * @return 1:初始库表中没有数据
     *         2:目标库中该表不存在，是否在目标库中创建该表，并同步数据?
     *         3:目标表存在数据,是否自动清除并同步数据?
     *         0:同步成功
     *         -1:请检查，同一数据库下无法同步数据
     * @throws Exception ignore
     */
    Integer executeCheck(String dbLinkIdFrom, String dbLinkIdTo, Map<String, String> convertRuleMap, String table) throws Exception;


    /**
     * 同步表执行
     *
     * @param dbLinkIdFrom  数据库连接From
     * @param dbLinkIdTo    数据库连接To
     * @param table         表名
     * @throws Exception ignore
     */
    void execute(String dbLinkIdFrom, String dbLinkIdTo, Map<String, String> convertRuleMap, String table) throws Exception;

    /**
     * 批量执行
     *
     * @param dbLinkIdFrom  数据库连接From
     * @param dbLinkIdTo    数据库连接To
     * @param tableList         表名
     * @throws Exception ignore
     */
    Map<String, Integer> executeBatch(String dbLinkIdFrom, String dbLinkIdTo, Map<String, String> convertRuleMap, List<String> tableList) ;

    /**
     * 打印初始脚本
     *
     * @param printType dbInit:初始脚本、dbStruct:表结构、dbData:数据、tenant:多租户
     */
    Map<String, Integer> printDbInit(String dbLinkIdFrom, String dbTypeTo, List<String> tableList, Map<String, String> convertRuleMap, String printType) throws Exception;

}