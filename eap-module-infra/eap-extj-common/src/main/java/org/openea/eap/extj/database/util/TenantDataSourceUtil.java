//package org.openea.eap.extj.database.util;
//
//import com.alibaba.druid.pool.DruidDataSource;
//import com.alibaba.fastjson.JSONObject;
//import com.baomidou.dynamic.datasource.ds.ItemDataSource;
//import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
//import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
//import net.sf.jsqlparser.JSQLParserException;
//import net.sf.jsqlparser.parser.CCJSqlParserUtil;
//import net.sf.jsqlparser.statement.Statement;
//import net.sf.jsqlparser.statement.select.Select;
//import org.openea.eap.extj.config.ConfigValueUtil;
//import org.openea.eap.extj.model.MultiTenantType;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.util.Assert;
//import org.springframework.util.ObjectUtils;
//
//import javax.sql.DataSource;
//import java.sql.SQLException;
//import java.util.*;
//
//public class TenantDataSourceUtil {
//
//    private static ConfigValueUtil configValueUtil;
//    private static MyTenantLineInnerInterceptor myTenantLineInnerInterceptor;
//    private static MySchemaInnerInterceptor mySchemaInnerInterceptor;
//    public static final String DBLINK_KEY = "TenantInfo";
//
//    public TenantDataSourceUtil() {
//    }
//
//    @Autowired(
//            required = false
//    )
//    public void setDynamicTableNameInnerInterceptor(DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor) {
//        mySchemaInnerInterceptor = (MySchemaInnerInterceptor)dynamicTableNameInnerInterceptor;
//    }
//
//    @Autowired(
//            required = false
//    )
//    public void setMyTenantLineInnerInterceptor(TenantLineInnerInterceptor tenantLineInnerInterceptor) {
//        myTenantLineInnerInterceptor = (MyTenantLineInnerInterceptor)tenantLineInnerInterceptor;
//    }
//
//    @Autowired
//    public void setConfigValueUtil(ConfigValueUtil configValueUtil) {
//        TenantDataSourceUtil.configValueUtil = configValueUtil;
//    }
//
//    public static void setTenantInfo(String tenantId, TenantVO tenant) {
//        TenantProvider.putTenantCache(tenantId, "TenantInfo", tenant);
//    }
//
//    public static TenantVO getTenantInfo(String tenantId) throws LoginException {
//        TenantVO tenantVO = getCacheTenantInfo(tenantId);
//        if (tenantVO == null) {
//            tenantVO = getRemoteTenantInfo(tenantId);
//        }
//
//        return tenantVO;
//    }
//
//    public static TenantVO getCacheTenantInfo(String tenantId) {
//        return (TenantVO)TenantProvider.getTenantCache(tenantId, "TenantInfo");
//    }
//
//    public static TenantVO getRemoteTenantInfo(String tenantId) throws LoginException {
//        JSONObject object = HttpUtil.httpRequest(configValueUtil.getMultiTenancyUrl() + tenantId, "GET", (String)null);
//        if (object != null && !"500".equals(object.get("code").toString())) {
//            if ("400".equals(object.getString("code"))) {
//                log.error("获取多租户信息失败：{}", object.getString("msg"));
//                if (configValueUtil.getMultiTenancyUrl().contains("https")) {
//                    throw new LoginException(object.getString("msg"));
//                } else {
//                    throw new LoginException(object.getString("msg"));
//                }
//            } else {
//                Map<String, Object> resulList = JsonUtil.stringToMap(object.getString("data"));
//                TenantVO vo;
//                if (resulList.get("java") != null) {
//                    vo = (new TenantVO()).setDataSchema(TenantDbSchema.DEFAULT).setDbName(String.valueOf(resulList.get("java")));
//                } else {
//                    vo = (TenantVO)JsonUtil.getJsonToBean(resulList, TenantVO.class);
//                    if (ObjectUtils.isEmpty(vo.getLinkList())) {
//                        vo.setDataSchema(TenantDbSchema.DEFAULT);
//                        vo.setLinkList((List)null);
//                    } else {
//                        vo.setDataSchema(TenantDbSchema.REMOTE);
//                    }
//
//                    setTenantInfo(tenantId, vo);
//                }
//
//                return vo;
//            }
//        } else {
//            throw new LoginException(MsgCode.LOG105.get());
//        }
//    }
//
//    public static TenantLinkModel getTenantAssignDataSource(String tenantId) {
//        List<TenantLinkModel> linkList = getTenantAssignDataSourceList(tenantId);
//        return (TenantLinkModel)linkList.stream().filter((link) -> {
//            return link.getConfigType().equals(0);
//        }).findFirst().orElse((Object)null);
//    }
//
//    public static List<TenantLinkModel> getTenantAssignDataSourceList(String tenantId) {
//        if (isMultiTenancy() && DataSourceContextHolder.isAssignDataSource()) {
//            TenantVO tenantVO = null;
//
//            try {
//                tenantVO = getTenantInfo(tenantId);
//            } catch (LoginException var3) {
//                throw new RuntimeException("获取缓存租户库列表失败", var3);
//            }
//
//            List<TenantLinkModel> linkList = tenantVO.getLinkList();
//            return linkList;
//        } else {
//            return Collections.EMPTY_LIST;
//        }
//    }
//
//    public static TenantVO switchTenant(String tenantId) throws LoginException {
//        TenantVO tenantVO = getTenantInfo(tenantId);
//        switchTenant(tenantId, tenantVO);
//        return tenantVO;
//    }
//
//    public static void switchTenant(String tenantId, TenantVO tenantVO) throws LoginException {
//        Assert.notNull(tenantVO, "租户信息获取失败: " + tenantId);
//        Boolean isAssign = false;
//        String dbName;
//        switch (tenantVO.getDataSchema()) {
//            case REMOTE:
//                dbName = ((TenantLinkModel)tenantVO.getLinkList().stream().filter((l) -> {
//                    return l.getConfigType().equals(0);
//                }).findFirst().get()).getServiceName();
//                isAssign = true;
//                break;
//            case DEFAULT:
//                dbName = tenantVO.getDbName();
//                break;
//            default:
//                throw new LoginException(MsgCode.LOG102.get());
//        }
//
//        DataSourceContextHolder.setDatasource(tenantId, dbName, isAssign);
//    }
//
//    public static String getTenantSchema() {
//        String result = "";
//        if (isMultiTenancy() && configValueUtil.getMultiTenantType().eq(MultiTenantType.SCHEMA)) {
//            result = getTenantName();
//        }
//
//        return result;
//    }
//
//    public static String getTenantColumn() {
//        String result = "";
//        if (isMultiTenancy() && configValueUtil.getMultiTenantType().eq(MultiTenantType.COLUMN)) {
//            result = getTenantName();
//        }
//
//        return result;
//    }
//
//    public static String getTenantDbName() {
//        String result = "";
//        if (isMultiTenancy()) {
//            if (configValueUtil.getMultiTenantType().eq(MultiTenantType.COLUMN)) {
//                result = DynamicDataSourceUtil.dataSourceUtil.getDbName();
//            } else {
//                result = DataSourceContextHolder.getDatasourceName();
//                result = convertSchemaName(result);
//            }
//        }
//
//        return result;
//    }
//
//    public static void initDataSourceTenantDbName(DbSourceOrDbLink dataSourceUtil) {
//        if (isMultiTenancy()) {
//            if (isTenantAssignDataSource()) {
//                return;
//            }
//
//            if (!(dataSourceUtil instanceof DataSourceUtil) || dataSourceUtil instanceof DbLinkEntity && !"0".equals(((DbLinkEntity)dataSourceUtil).getId()) && ((DbLinkEntity)dataSourceUtil).getId() != null) {
//                return;
//            }
//
//            boolean isColumn = isTenantColumn();
//            DataSourceUtil ds = (DataSourceUtil)dataSourceUtil;
//            switch (ds.getDbType()) {
//                case "PostgreSQL":
//                    if (isColumn) {
//                        if (StringUtil.isEmpty(ds.getDbSchema())) {
//                            ds.setDbSchema(DbPostgre.DEF_SCHEMA);
//                        }
//                    } else {
//                        ds.setDbSchema(getTenantDbName());
//                    }
//                    break;
//                case "KingbaseES":
//                    if (isColumn) {
//                        if (StringUtil.isEmpty(ds.getDbSchema())) {
//                            ds.setDbSchema(DbKingbase.DEF_SCHEMA);
//                        }
//                    } else {
//                        ds.setDbSchema(getTenantDbName());
//                    }
//                    break;
//                default:
//                    ds.setDbName(getTenantDbName());
//            }
//        }
//
//    }
//
//    public static String getTenantName() {
//        String result = "";
//        if (isMultiTenancy() && !DataSourceContextHolder.isAssignDataSource()) {
//            result = DataSourceContextHolder.getDatasourceName();
//            result = convertSchemaName(result);
//        }
//
//        return result;
//    }
//
//    public static String convertSchemaName(String dbName) {
//        if (StringUtil.isNotEmpty(dbName)) {
//            switch (DynamicDataSourceUtil.dataSourceUtil.getDbType()) {
//                case "PostgreSQL":
//                    dbName = dbName.toLowerCase();
//                    break;
//                case "Oracle":
//                    dbName = dbName.toUpperCase();
//            }
//        }
//
//        return dbName;
//    }
//
//    public static void initTenantAssignDataSource() throws SQLException {
//        if (isTenantAssignDataSource()) {
//            String tenantId = DataSourceContextHolder.getDatasourceId();
//            String dbKey = tenantId + "-" + "master";
//            synchronized(LockObjectUtil.addLockKey(tenantId)) {
//                if (!DynamicDataSourceUtil.dynamicRoutingDataSource.getGroupDataSources().containsKey(dbKey)) {
//                    TenantVO tenantVO = null;
//
//                    try {
//                        tenantVO = switchTenant(tenantId);
//                    } catch (LoginException var17) {
//                        throw new RuntimeException("获取租户信息失败:" + tenantId);
//                    }
//
//                    List<String> list = new ArrayList(16);
//                    List<TenantLinkModel> linkList = tenantVO.getLinkList();
//                    if (ObjectUtils.isEmpty(linkList)) {
//                        throw new RuntimeException("未获取到租户指定数据源信息");
//                    }
//
//                    if (linkList != null) {
//                        String mKey = dbKey + "_";
//                        String sKey = tenantId + "-" + "slave" + "_";
//                        Iterator var8 = linkList.iterator();
//
//                        while(var8.hasNext()) {
//                            TenantLinkModel model = (TenantLinkModel)var8.next();
//                            DbLinkEntity dbLinkEntity = model.toDbLinkEntity();
//                            if ("0".equals(String.valueOf(model.getConfigType()))) {
//                                dbLinkEntity.setId(mKey + dbLinkEntity.getId());
//                            } else {
//                                dbLinkEntity.setId(sKey + dbLinkEntity.getId());
//                            }
//
//                            try {
//                                DataSource dataSource = DynamicDataSourceUtil.createDataSource(dbLinkEntity);
//                                dataSource.getConnection().close();
//                                list.add(dbLinkEntity.getId());
//                                DynamicDataSourceUtil.dynamicRoutingDataSource.addDataSource(dbLinkEntity.getId(), dataSource);
//                            } catch (SQLException var18) {
//                                Iterator var12 = list.iterator();
//
//                                while(var12.hasNext()) {
//                                    String s = (String)var12.next();
//
//                                    try {
//                                        DynamicDataSourceUtil.dynamicRoutingDataSource.removeDataSource(s);
//                                    } catch (Exception var16) {
//                                        var16.printStackTrace();
//                                    }
//                                }
//
//                                throw var18;
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//    }
//
//    public static void removeAllAssignDataSource() {
//        if (isTenantAssignDataSource()) {
//            String tenantId = DataSourceContextHolder.getDatasourceId();
//            String dbKey = tenantId + "-" + "master";
//            TenantVO tenantVO = null;
//
//            try {
//                tenantVO = getTenantInfo(tenantId);
//            } catch (LoginException var12) {
//                log.error("获取缓存租户库列表失败:" + tenantId, var12);
//            }
//
//            if (tenantVO != null) {
//                List<TenantLinkModel> linkList = tenantVO.getLinkList();
//                if (linkList != null) {
//                    String mKey = dbKey + "_";
//                    String sKey = tenantId + "-" + "slave" + "_";
//                    Iterator var6 = linkList.iterator();
//
//                    while(var6.hasNext()) {
//                        TenantLinkModel model = (TenantLinkModel)var6.next();
//                        DbLinkEntity dbLinkEntity = model.toDbLinkEntity();
//                        String key;
//                        if ("0".equals(String.valueOf(model.getConfigType()))) {
//                            key = mKey + dbLinkEntity.getId();
//                        } else {
//                            key = sKey + dbLinkEntity.getId();
//                        }
//
//                        try {
//                            DataSource dataSource = (DataSource)DynamicDataSourceUtil.dynamicRoutingDataSource.getDataSources().get(key);
//                            if (dataSource instanceof ItemDataSource && ((ItemDataSource)dataSource).getRealDataSource() instanceof DruidDataSource) {
//                                ((DruidDataSource)((ItemDataSource)dataSource).getRealDataSource()).setBreakAfterAcquireFailure(true);
//                            }
//
//                            DynamicDataSourceUtil.dynamicRoutingDataSource.removeDataSource(key);
//                        } catch (Exception var11) {
//                        }
//                    }
//                }
//            } else {
//                log.error("获取缓存租户库列表失败: {}", tenantId);
//            }
//        }
//
//    }
//
//    public static String getTenantAssignDataSourceMasterKeyName() {
//        return isTenantAssignDataSource() ? DataSourceContextHolder.getDatasourceId() + "-" + "master" : "";
//    }
//
//    public static boolean isMultiTenancy() {
//        return configValueUtil.isMultiTenancy();
//    }
//
//    public static boolean isTenantAssignDataSource() {
//        return isMultiTenancy() && DataSourceContextHolder.isAssignDataSource();
//    }
//
//    public static boolean isTenantColumn() {
//        return isMultiTenancy() && MultiTenantType.COLUMN.eq(configValueUtil.getMultiTenantType());
//    }
//
//    public static boolean isTenantSchema() {
//        return isMultiTenancy() && MultiTenantType.SCHEMA.eq(configValueUtil.getMultiTenantType());
//    }
//
//    public static String parseTenantSql(String sql) {
//        if (isTenantColumn()) {
//            try {
//                Statement statement = CCJSqlParserUtil.parse(sql);
//                return statement instanceof Select ? myTenantLineInnerInterceptor.parserSingle(sql, (Object)null) : myTenantLineInnerInterceptor.parserMulti(sql, (Object)null);
//            } catch (JSQLParserException var2) {
//                throw new RuntimeException(var2);
//            }
//        } else {
//            return isTenantSchema() ? mySchemaInnerInterceptor.changeTable(sql) : sql;
//        }
//    }
//}
