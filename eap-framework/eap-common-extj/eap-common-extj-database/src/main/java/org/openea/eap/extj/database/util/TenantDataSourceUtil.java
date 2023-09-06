package org.openea.eap.extj.database.util;

import cn.hutool.core.text.StrPool;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.ds.ItemDataSource;
import com.baomidou.dynamic.datasource.support.DdConstants;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import org.openea.eap.extj.config.ConfigValueUtil;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.database.enums.TenantDbSchema;
import org.openea.eap.extj.database.model.TenantLinkModel;
import org.openea.eap.extj.database.model.TenantVO;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.database.model.interfaces.DbSourceOrDbLink;
import org.openea.eap.extj.database.plugins.MySchemaInnerInterceptor;
import org.openea.eap.extj.database.plugins.MyTenantLineInnerInterceptor;
import org.openea.eap.extj.database.source.DbBase;
import org.openea.eap.extj.database.source.impl.DbKingbase;
import org.openea.eap.extj.database.source.impl.DbPostgre;
import org.openea.eap.extj.exception.LoginException;
import org.openea.eap.extj.model.MultiTenantType;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.LockObjectUtil;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.TenantProvider;
import org.openea.eap.extj.util.data.DataSourceContextHolder;
import org.openea.eap.extj.util.wxutil.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 租户数据工具类
 * 
 */
@Slf4j
@Component
//@ConditionalOnMissingBean(name="tenantDataSourceUtil")
public class TenantDataSourceUtil {

    private static ConfigValueUtil configValueUtil;
    private static MyTenantLineInnerInterceptor myTenantLineInnerInterceptor;
    private static MySchemaInnerInterceptor mySchemaInnerInterceptor;

    @Autowired(required = false)
    public void setDynamicTableNameInnerInterceptor(DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor) {
        TenantDataSourceUtil.mySchemaInnerInterceptor = (MySchemaInnerInterceptor) dynamicTableNameInnerInterceptor;
    }

    @Autowired(required = false)
    public void setMyTenantLineInnerInterceptor(TenantLineInnerInterceptor tenantLineInnerInterceptor) {
        TenantDataSourceUtil.myTenantLineInnerInterceptor = (MyTenantLineInnerInterceptor) tenantLineInnerInterceptor;
    }

    @Autowired
    public void setConfigValueUtil(ConfigValueUtil configValueUtil) {
        TenantDataSourceUtil.configValueUtil = configValueUtil;
    }

    public static final String DBLINK_KEY = "TenantInfo";

    /**
     * 设置租户信息到Redis缓存中
     *
     * @param tenantId
     * @param tenant
     */
    public static void setTenantInfo(String tenantId, TenantVO tenant) {
        TenantProvider.putTenantCache(tenantId, DBLINK_KEY, tenant);
    }

    /**
     * 从Redis缓存中或远程获取租户信息
     *
     * @param tenantId
     * @return
     */
    public static TenantVO getTenantInfo(String tenantId) throws LoginException {
        TenantVO tenantVO = TenantDataSourceUtil.getCacheTenantInfo(tenantId);
        if (tenantVO == null) {
            tenantVO = TenantDataSourceUtil.getRemoteTenantInfo(tenantId);
        }
        return tenantVO;
    }

    /**
     * 从Redis缓存中获取租户信息
     *
     * @param tenantId
     * @return
     */
    public static TenantVO getCacheTenantInfo(String tenantId) {
        return TenantProvider.getTenantCache(tenantId, DBLINK_KEY);
    }


    /**
     * 从租户系统获取租户信息
     *
     * @param tenantId
     * @return
     * @throws LoginException
     */
    public static TenantVO getRemoteTenantInfo(String tenantId) throws LoginException {
        JSONObject object = HttpUtil.httpRequest(configValueUtil.getMultiTenancyUrl() + tenantId, "GET", null);
        if (object == null || "500".equals(object.get("code").toString())) {
            throw new LoginException(MsgCode.LOG105.get());
        }
        if ("400".equals(object.getString("code"))) {
            log.error("获取多租户信息失败：{}", object.getString("msg"));
            if (configValueUtil.getMultiTenancyUrl().contains("https")) {
                throw new LoginException(object.getString("msg"));
            } else {
                throw new LoginException(object.getString("msg"));
            }
        }
        Map<String, Object> resulList = JsonUtil.stringToMap(object.getString("data"));
        // 租户库名
        TenantVO vo;
        if (resulList.get("java") != null) {
            vo = new TenantVO().setDataSchema(TenantDbSchema.DEFAULT).setDbName(String.valueOf(resulList.get("java")));
        } else {
            // 转换成租户信息模型
            vo = JsonUtil.getJsonToBean(resulList, TenantVO.class);
            if (ObjectUtils.isEmpty(vo.getLinkList())) {
                vo.setDataSchema(TenantDbSchema.DEFAULT);
                vo.setLinkList(null);
            } else {
                vo.setDataSchema(TenantDbSchema.REMOTE);
            }
            TenantDataSourceUtil.setTenantInfo(tenantId, vo);
        }
        return vo;
    }


    /**
     * 获取租户指定库主库
     * @param tenantId
     * @return
     */
    public static TenantLinkModel getTenantAssignDataSource(String tenantId){
        List<TenantLinkModel> linkList = getTenantAssignDataSourceList(tenantId);
        return linkList.stream().filter(link->link.getConfigType().equals(0)).findFirst().orElse(null);
    }

    /**
     * 获取租户指定库列表
     * @param tenantId
     * @return
     */
    public static List<TenantLinkModel> getTenantAssignDataSourceList(String tenantId){
        if(isMultiTenancy() && DataSourceContextHolder.isAssignDataSource()){
            TenantVO tenantVO = null;
            try {
                tenantVO = getTenantInfo(tenantId);
            } catch (LoginException e) {
                throw new RuntimeException("获取缓存租户库列表失败", e);
            }
            List<TenantLinkModel> linkList = tenantVO.getLinkList();
            return linkList;
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * 切换租户， 先从Redis缓存中获取， 再从租户系统中获取
     *
     * @param tenantId
     */
    public static TenantVO switchTenant(String tenantId) throws LoginException {
        TenantVO tenantVO = TenantDataSourceUtil.getTenantInfo(tenantId);
        switchTenant(tenantId, tenantVO);
        return tenantVO;
    }


    /**
     * 切换租户
     */
    public static void switchTenant(String tenantId, TenantVO tenantVO) throws LoginException {
        Assert.notNull(tenantVO, "租户信息获取失败: " + tenantId);
        String dbName;
        Boolean isAssign = false;
        switch (tenantVO.getDataSchema()) {
            case REMOTE:
                dbName = tenantVO.getLinkList().stream().filter(l->l.getConfigType().equals(0)).findFirst().get().getServiceName();
                isAssign = true;
                break;
            case DEFAULT:
                dbName = tenantVO.getDbName();
                break;
            default:
                throw new LoginException(MsgCode.LOG102.get());
        }
        DataSourceContextHolder.setDatasource(tenantId, dbName, isAssign);
    }

    /**
     * 获取当前租户模式名
     *
     * @return
     */
    public static String getTenantSchema() {
        String result = StringUtil.EMPTY;
        if (isMultiTenancy() && configValueUtil.getMultiTenantType().eq(MultiTenantType.SCHEMA)) {
            result = getTenantName();
        }
        return result;
    }

    /**
     * 获取当前字段模式多租户名
     *
     * @return
     */
    public static String getTenantColumn() {
        String result = StringUtil.EMPTY;
        if (isMultiTenancy() && configValueUtil.getMultiTenantType().eq(MultiTenantType.COLUMN)) {
            result = getTenantName();
        }
        return result;
    }

    /**
     * 获取当前租户名
     * 字段模式返回默认配置库名
     * 租户指定数据源和Schema模式返回租户库名
     *
     * @return
     */
    public static String getTenantDbName() {
        String result = StringUtil.EMPTY;
        if (isMultiTenancy()) {
            if(configValueUtil.getMultiTenantType().eq(MultiTenantType.COLUMN)){
                result = DynamicDataSourceUtil.dataSourceUtil.getDbName();
            }else{
                result = DataSourceContextHolder.getDatasourceName();
                result = convertSchemaName(result);
            }
        }
        return result;
    }

    public static void initDataSourceTenantDbName(DbSourceOrDbLink dataSourceUtil){
        if(isMultiTenancy()) {
            if(isTenantAssignDataSource()){
                return;
            }
            if (!(dataSourceUtil instanceof DataSourceUtil) || (dataSourceUtil instanceof DbLinkEntity && !"0".equals(((DbLinkEntity) dataSourceUtil).getId()) && ((DbLinkEntity) dataSourceUtil).getId() != null)) {
                return;
            }
            boolean isColumn = isTenantColumn();
            //默认库在多租户Schema模式情况下需要切库
            //字段多租户模式下， Schema为空设置默认值
            DataSourceUtil ds = (DataSourceUtil) dataSourceUtil;
            switch (ds.getDbType()){
                case DbBase.POSTGRE_SQL:
                    if(isColumn){
                        if(StringUtil.isEmpty(ds.getDbSchema())) {
                            ds.setDbSchema(DbPostgre.DEF_SCHEMA);
                        }
                    }else {
                        ds.setDbSchema(TenantDataSourceUtil.getTenantDbName());
                    }
                    break;
                case DbBase.KINGBASE_ES:
                    if(isColumn){
                        if(StringUtil.isEmpty(ds.getDbSchema())) {
                            ds.setDbSchema(DbKingbase.DEF_SCHEMA);
                        }
                    }else {
                        ds.setDbSchema(TenantDataSourceUtil.getTenantDbName());
                    }
                    break;
                case DbBase.ORACLE:
                    ds.setDbSchema(TenantDataSourceUtil.getTenantDbName());
                    break;
                default:
                    ds.setDbName(TenantDataSourceUtil.getTenantDbName());
            }
        }

    }


    /**
     * 获取当前租户名
     *
     * @return
     */
    public static String getTenantName() {
        String result = StringUtil.EMPTY;
        if (isMultiTenancy() && !DataSourceContextHolder.isAssignDataSource()) {
            result = DataSourceContextHolder.getDatasourceName();
            result = convertSchemaName(result);
        }
        return result;
    }

    /**
     * 转换不同数据库租户模式名
     * @param dbName
     * @return
     */
    public static String convertSchemaName(String dbName){
        if(StringUtil.isNotEmpty(dbName)) {
            switch (DynamicDataSourceUtil.dataSourceUtil.getDbType()) {
                case DbBase.POSTGRE_SQL:
                    dbName = dbName.toLowerCase();
                    break;
                case DbBase.ORACLE:
                    dbName = dbName.toUpperCase();
                    break;
            }
        }
        return dbName;
    }

    public static void initTenantAssignDataSource() throws SQLException {
        if(isTenantAssignDataSource()){
            String tenantId = DataSourceContextHolder.getDatasourceId();
            String dbKey = tenantId + StrPool.DASHED + DdConstants.MASTER;
            synchronized (LockObjectUtil.addLockKey(tenantId)) {
                if(!DynamicDataSourceUtil.dynamicRoutingDataSource.getGroupDataSources().containsKey(dbKey)) {
                    TenantVO tenantVO = null;
                    try {
                        tenantVO = switchTenant(tenantId);
                    } catch (LoginException e) {
                        throw new RuntimeException("获取租户信息失败:" + tenantId);
                    }

                    List<String> list = new ArrayList<>(16);
                    List<TenantLinkModel> linkList = tenantVO.getLinkList();
                    if (ObjectUtils.isEmpty(linkList)) {
                        throw new RuntimeException("未获取到租户指定数据源信息");
                    }
                    if (linkList != null) {
                        // 添加数据源信息到redis中
                        String mKey = dbKey + StrPool.UNDERLINE;
                        String sKey = tenantId + StrPool.DASHED + DdConstants.SLAVE + StrPool.UNDERLINE;
                        for (TenantLinkModel model : linkList) {
                            DbLinkEntity dbLinkEntity = model.toDbLinkEntity();
                            if ("0".equals(String.valueOf(model.getConfigType()))) {
                                dbLinkEntity.setId(mKey + dbLinkEntity.getId());
                            } else {
                                dbLinkEntity.setId(sKey + dbLinkEntity.getId());
                            }
                            try {
                                DataSource dataSource = DynamicDataSourceUtil.createDataSource(dbLinkEntity);
                                dataSource.getConnection().close();
                                list.add(dbLinkEntity.getId());
                                DynamicDataSourceUtil.dynamicRoutingDataSource.addDataSource(dbLinkEntity.getId(), dataSource);
                            } catch (SQLException e) {
                                for (String s : list) {
                                    try {
                                        DynamicDataSourceUtil.dynamicRoutingDataSource.removeDataSource(s);
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }
                                }
                                throw e;
                            }
                        }
                    }
                }
            }
        }
    }

    public static void removeAllAssignDataSource(){
        if(isTenantAssignDataSource()) {
            String tenantId = DataSourceContextHolder.getDatasourceId();
            String dbKey = tenantId + StrPool.DASHED + DdConstants.MASTER;
            TenantVO tenantVO = null;
            try {
                tenantVO = TenantDataSourceUtil.getTenantInfo(tenantId);
            } catch (LoginException e) {
                log.error("获取缓存租户库列表失败:" + tenantId, e);
            }
            if(tenantVO != null) {
                List<TenantLinkModel> linkList = tenantVO.getLinkList();
                if (linkList != null) {
                    // 添加数据源信息到redis中
                    String mKey = dbKey + StrPool.UNDERLINE;
                    String sKey = tenantId + StrPool.DASHED + DdConstants.SLAVE + StrPool.UNDERLINE;
                    for (TenantLinkModel model : linkList) {
                        DbLinkEntity dbLinkEntity = model.toDbLinkEntity();
                        String key;
                        if ("0".equals(String.valueOf(model.getConfigType()))) {
                            key = mKey + dbLinkEntity.getId();
                        } else {
                            key = sKey + dbLinkEntity.getId();
                        }
                        try {
                            DataSource dataSource = DynamicDataSourceUtil.dynamicRoutingDataSource.getDataSources().get(key);
                            if (dataSource instanceof ItemDataSource && ((ItemDataSource) dataSource).getRealDataSource() instanceof DruidDataSource) {
                                //Druid数据源如果正在获取数据源 有概率连接创建线程无法停止
                                ((DruidDataSource) ((ItemDataSource) dataSource).getRealDataSource()).setBreakAfterAcquireFailure(true);
                            }
                            DynamicDataSourceUtil.dynamicRoutingDataSource.removeDataSource(key);
                        } catch (Exception e) {

                        }
                    }
                }
            }else{
                log.error("获取缓存租户库列表失败: {}", tenantId);
            }
        }
    }

    /**
     * 获取租户指定数据源 在连接池中的主库KEY
     * @return
     */
    public static String getTenantAssignDataSourceMasterKeyName(){
        if(isTenantAssignDataSource()){
            return DataSourceContextHolder.getDatasourceId() + StrPool.DASHED +DdConstants.MASTER;
        }
        return StringUtil.EMPTY;
    }

    public static boolean isMultiTenancy(){
        return configValueUtil.isMultiTenancy();
    }

    public static boolean isTenantAssignDataSource(){
        return isMultiTenancy() && DataSourceContextHolder.isAssignDataSource();
    }

    /**
     * 是否开启多租户, 且Column模式
     * @return
     */
    public static boolean isTenantColumn(){
        return isMultiTenancy() && MultiTenantType.COLUMN.eq(configValueUtil.getMultiTenantType());
    }

    /**
     * 是否开启多租户, 且Schema模式
     * @return
     */
    public static boolean isTenantSchema(){
        return isMultiTenancy() && MultiTenantType.SCHEMA.eq(configValueUtil.getMultiTenantType());
    }


    /**
     * 将SQL语句添加多租户过滤
     * @param sql
     * @return
     */
    public static String parseTenantSql(String sql){
        if (isTenantColumn()) {
            try {
                Statement statement = CCJSqlParserUtil.parse(sql);
                if (statement instanceof Select) {
                    return myTenantLineInnerInterceptor.parserSingle(sql, null);
                } else {
                    return myTenantLineInnerInterceptor.parserMulti(sql, null);
                }
            } catch(JSQLParserException e){
                throw new RuntimeException(e);
            }
        }else if(isTenantSchema()){
            return mySchemaInnerInterceptor.changeTable(sql);
        }
        return sql;
    }

}
