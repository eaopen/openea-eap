package org.openea.eap.extj.base.service;

import org.openea.eap.extj.base.Page;
import org.openea.eap.extj.base.Pagination;
import org.openea.eap.extj.database.model.dbfield.DbFieldModel;
import org.openea.eap.extj.database.model.dbtable.DbTableFieldModel;
import org.openea.eap.extj.database.model.page.DbTableDataForm;
import org.openea.eap.extj.exception.DataException;

import java.util.List;
import java.util.Map;

public interface DbTableService {
    /**
     * 1:表列表
     *
     * @param dbLinkId 连接Id
     * @param methodName
     * @return 表集合信息
     * @throws DataException ignore
     */
    List<DbTableFieldModel> getList(String dbLinkId, String methodName) throws Exception;

    /**
     * 1:表列表
     *
     * @param dbLinkId 连接Id
     * @param page 关键字
     * @return 表集合信息
     * @throws DataException ignore
     */
    List<DbTableFieldModel> getListPage(String dbLinkId, Page page) throws Exception;

    /**
     * 1:表列表
     *
     * @param dbLinkId 连接Id
     * @return 表集合信息
     * @throws DataException ignore
     */
    List<DbTableFieldModel> getListPage(String dbLinkId, Pagination pagination) throws Exception;


    /**
     * 2:单表信息
     *
     * @param dbLinkId 连接Id
     * @return 表集合信息
     * @throws DataException ignore
     */
    DbTableFieldModel getTable(String dbLinkId, String table) throws Exception;

    /**
     * 3:表字段
     *
     * @param dbLinkId 连接Id
     * @param table 表名
     * @return 字段集合信息
     * @throws DataException ignore
     */
    List<DbFieldModel> getFieldList(String dbLinkId, String table) throws Exception;

    /**
     * 4:表数据
     *
     * @param dbTableDataForm 分页
     * @param dbLinkId 连接Id
     * @param table 表名
     * @return 表数据集合
     * @throws Exception ignore
     */
    List<Map<String, Object>> getData(DbTableDataForm dbTableDataForm, String dbLinkId, String table) throws Exception;

    /**
     * 5:校验：表名重名
     *
     * @param dbLinkId 连接Id
     * @return 重名标识
     * @throws Exception ignore
     */
    boolean isExistTable(String dbLinkId, String table) throws Exception;

    /**
     * 6:删除存在表
     *
     * @param dbLinkId 连接ID
     * @param table 删除表
     */
    boolean dropExistsTable(String dbLinkId, String table) throws Exception;

    /**
     * 7:删除表
     *
     * @param dbLinkId  连接Id
     * @param table 表名
     * @throws DataException ignore
     */
    void delete(String dbLinkId, String table) throws Exception;

    /**
     * 删除全部表（慎用）
     * @param dbLinkId 连接Id
     */
    void deleteAllTable(String dbLinkId, String dbType) throws Exception;

    /**
     * 8:创建表
     *
     * @param dbTableFieldModel 前端创表表单信息
     * @return 执行状态（1：成功；0：重名）
     * @throws DataException ignore
     */
    int createTable(DbTableFieldModel dbTableFieldModel) throws Exception;

    /**
     * 9:获取表模型
     * @param dbLinkId 数据连接ID
     * @param tableName 表名
     * @return 表模板
     * @throws Exception ignore
     */
    DbTableFieldModel getDbTableModel(String dbLinkId, String tableName) throws Exception;

    /**
     * 10:修改表
     *
     * @param dbTableFieldModel 修改表参数对象
     * @throws DataException ignore
     */
    void update(DbTableFieldModel dbTableFieldModel) throws Exception;

    /**
     * 11:添加字段
     * @param dbTableFieldModel 数据表字段模型
     * @throws DataException ignore
     */
    void addField(DbTableFieldModel dbTableFieldModel) throws Exception;

    /**
     * 12:获取表数据行数
     *
     * @param dbLinkId  数据连接Id
     * @param table 表名
     * @return 数据行数
     * @throws DataException ignore
     */
    int getSum(String dbLinkId, String table) throws Exception;

    /**
     * 13:获取动态数据源
     *
     * @param dbLinkId 数据连接ID
     * @return 动态数据库源
     * @throws DataException ignore
     */
//    DbConnDTO getResource(String dbLinkId) throws DataException;
}
