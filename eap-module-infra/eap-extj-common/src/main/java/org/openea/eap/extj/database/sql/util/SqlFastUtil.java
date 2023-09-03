package org.openea.eap.extj.database.sql.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.openea.eap.extj.base.Pagination;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.database.enums.DbAliasEnum;
import org.openea.eap.extj.database.model.dbfield.DbFieldModel;
import org.openea.eap.extj.database.model.dbtable.DbTableFieldModel;
import org.openea.eap.extj.database.model.dto.PrepSqlDTO;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.database.sql.enums.SqlComEnum;
import org.openea.eap.extj.database.sql.model.SqlPrintHandler;
import org.openea.eap.extj.database.sql.param.FormatSql;
import org.openea.eap.extj.database.sql.param.FormatSqlOracle;
import org.openea.eap.extj.database.util.DbTypeUtil;
import org.openea.eap.extj.database.util.JdbcUtil;
import org.openea.eap.extj.database.util.NotTenantPluginHolder;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.context.SpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openea.eap.extj.database.model.dbtable.DbTableModelBase;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SqlFastUtil {
    private static final Logger log = LoggerFactory.getLogger(SqlFastUtil.class);
    private static SqlPrintHandler sqlPrintHandler = (SqlPrintHandler) SpringContext.getBean(SqlPrintHandler.class);

    public SqlFastUtil() {
    }

    public static void creTable(DbLinkEntity dbLinkEntity, DbTableFieldModel dbTableFieldModel) throws Exception {
        List<DbFieldModel> dbFieldModelList = dbTableFieldModel.getDbFieldModelList();
        List<String> fieldSqlList = new ArrayList();
        if (!CollectionUtils.isNotEmpty(dbFieldModelList)) {
            throw new Exception("没有初始字段");
        } else {
            Iterator var4 = dbFieldModelList.iterator();

            while(var4.hasNext()) {
                DbFieldModel fieldModel = (DbFieldModel)var4.next();
                String fieldSql = SqlComEnum.CREATE_TABLE.getOutSqlByDb(dbLinkEntity.getDbType(), new String[]{"", FormatSql.getFieldName(fieldModel.getField(), dbLinkEntity.getDbType()), fieldModel.formatDataTypeByView(dbLinkEntity.getDbType()), FormatSql.defaultCheck(fieldModel, dbLinkEntity.getDbType()), fieldModel.getNullSign().equals("NOT NULL") ? fieldModel.getNullSign() : "", fieldModel.getIsPrimaryKey() != null && fieldModel.getIsPrimaryKey() ? "PRIMARY KEY" : "", fieldModel.getIsAutoIncrement() != null && fieldModel.getIsAutoIncrement() ? "AUTO_INCREMENT" : "", "COMMENT '" + fieldModel.getComment() + "'"});
                Matcher matcher = Pattern.compile("【([\\s\\S]+)】").matcher(fieldSql);
                if (!matcher.find()) {
                    throw new DataException("未找到字段SQL语句");
                }

                String fieldSqlFragment = matcher.group(1);
                fieldSqlList.add("\t" + fieldSqlFragment.replaceAll("\\s+", " ").trim());
            }

            String sql = SqlComEnum.CREATE_TABLE.getOutSqlByDb(dbLinkEntity.getDbType(), new String[]{SqlFrameUtil.htmlE(dbTableFieldModel.getTable())}).replaceAll("【.+】", StringUtil.join(fieldSqlList, ",\n"));
            NotTenantPluginHolder.setNotSwitchFlag();
            if (!sqlPrintHandler.creTable(sql)) {
                JdbcUtil.creUpDe((new PrepSqlDTO(sql)).withConn(dbLinkEntity));
            }

            FormatSqlOracle.autoIncrement(dbLinkEntity, dbTableFieldModel);
            commentTable(dbLinkEntity, dbTableFieldModel.getTable(), dbTableFieldModel.getComment());
            if (!DbTypeUtil.checkMySQL(dbLinkEntity)) {
                creTableCommentFiled(dbLinkEntity, dbTableFieldModel.getTable(), dbFieldModelList);
            }

        }
    }

    public static List<Boolean> addField(DbLinkEntity dbLinkEntity, String table, List<DbFieldModel> dbFieldModelList) throws Exception {
        List<DbFieldModel> originFieldList = getFieldList(dbLinkEntity, table);
        List<Boolean> existsFieldFlagList = new ArrayList();
        Iterator var5 = dbFieldModelList.iterator();

        while(var5.hasNext()) {
            DbFieldModel dbFieldModel = (DbFieldModel)var5.next();
            boolean existsFieldFlag = false;
            Iterator var8 = originFieldList.iterator();

            while(var8.hasNext()) {
                DbFieldModel originFile = (DbFieldModel)var8.next();
                if (originFile.getField().equalsIgnoreCase(dbFieldModel.getField())) {
                    existsFieldFlag = true;
                    break;
                }
            }

            existsFieldFlagList.add(existsFieldFlag);
            if (!existsFieldFlag) {
                String sql = SqlComEnum.ADD_COLUMN.getOutSqlByDb(dbLinkEntity.getDbType(), new String[]{"ADD", table, dbFieldModel.getField(), dbFieldModel.formatDataTypeByView(dbLinkEntity.getDbType()), dbFieldModel.getNullSign(), "", "'" + dbFieldModel.getComment() + "'"});
                NotTenantPluginHolder.setNotSwitchFlag();
                JdbcUtil.creUpDe((new PrepSqlDTO(sql)).withConn(dbLinkEntity));
                if (!DbTypeUtil.checkMySQL(dbLinkEntity)) {
                    commentFiled(dbLinkEntity, table, dbFieldModel.getField(), dbFieldModel.getDataType(), dbFieldModel.getComment());
                }
            }
        }

        return existsFieldFlagList;
    }

    public static Boolean dropTable(DbLinkEntity dbLinkEntity, String table) throws Exception {
        NotTenantPluginHolder.setNotSwitchFlag();
        String sql = SqlComEnum.DROP_TABLE.getOutSql(new String[]{SqlFrameUtil.htmlE(table)});
        int flag = 0;
        if (!sqlPrintHandler.dropTable(sql)) {
            flag = JdbcUtil.delete((new PrepSqlDTO(sql)).withConn(dbLinkEntity));
        }

        return flag > 0;
    }

    public static Boolean reTableName(DbLinkEntity dbLinkEntity, String oldTable, String newTable) throws Exception {
        NotTenantPluginHolder.setNotSwitchFlag();
        int i = JdbcUtil.creUpDe((new PrepSqlDTO(SqlComEnum.RE_TABLE_NAME.getOutSqlByDb(dbLinkEntity.getDbType(), new String[]{oldTable, newTable}))).withConn(dbLinkEntity));
        return i > 0;
    }

    public static Boolean commentTable(DbLinkEntity dbLinkEntity, String table, String comment) throws Exception {
        String sql = SqlComEnum.COMMENT_TABLE.getOutSqlByDb(dbLinkEntity.getDbType(), new String[]{SqlFrameUtil.htmlE(table), "'" + SqlFrameUtil.htmlE(comment) + "'"});
        NotTenantPluginHolder.setNotSwitchFlag();
        if (!sqlPrintHandler.comment(sql)) {
            JdbcUtil.creUpDe((new PrepSqlDTO(sql)).withConn(dbLinkEntity));
        }

        return true;
    }

    private static Boolean creTableCommentFiled(DbLinkEntity dbLinkEntity, String table, List<DbFieldModel> dbFieldModelList) throws Exception {
        String dbEncode = dbLinkEntity.getDbType();
        Iterator var4 = dbFieldModelList.iterator();

        while(var4.hasNext()) {
            DbFieldModel dbFieldModel = (DbFieldModel)var4.next();
            String sql = SqlComEnum.COMMENT_COLUMN.getOutSqlByDb(dbEncode, new String[]{table, FormatSql.getFieldName(dbFieldModel.getField(), dbEncode), "'" + dbFieldModel.getComment() + "'", dbFieldModel.formatDataTypeByView(dbEncode), null});
            if (!sqlPrintHandler.comment(sql)) {
                JdbcUtil.update((new PrepSqlDTO(sql)).withConn(dbLinkEntity));
            }
        }

        return true;
    }

    public static Boolean commentFiled(DbLinkEntity dbLinkEntity, String table, String column, String dataType, String comment) throws Exception {
        String sql = SqlComEnum.COMMENT_COLUMN.getOutSqlByDb(dbLinkEntity.getDbType(), new String[]{table, column, "'" + comment + "'", dataType, null});
        NotTenantPluginHolder.setNotSwitchFlag();
        int i = JdbcUtil.creUpDe((new PrepSqlDTO(sql)).withConn(dbLinkEntity));
        return i > 0;
    }

    public static String[] getPageSql(String dbEncode, String selectSql, String orderColumn, String orderSign, Integer currentPage, Integer pageSize) throws DataException {
        if (StringUtil.isEmpty(orderColumn)) {
            throw new DataException("分页查询缺少排序字段");
        } else {
            orderSign = StringUtil.isEmpty(orderSign) ? "" : orderSign;
            String beginIndex = Integer.toString((currentPage - 1) * pageSize);
            String endIndex = Integer.toString(currentPage * pageSize);
            return new String[]{SqlComEnum.ORDER_PAGE.getOutSqlByDb(dbEncode, new String[]{selectSql, orderColumn, beginIndex, endIndex, pageSize.toString(), orderSign}), SqlComEnum.COUNT_SIZE.getOutSqlByDb(dbEncode, new String[]{DbAliasEnum.TOTAL_RECORD.getAlias(dbEncode), selectSql})};
        }
    }

    public static String[] getPageSql(String dbEncode, String selectSql, Pagination pagination) throws DataException {
        return getPageSql(dbEncode, selectSql, pagination.getSidx(), pagination.getSort(), Long.valueOf(pagination.getCurrentPage()).intValue(), Long.valueOf(pagination.getPageSize()).intValue());
    }

    public static void batchInsert(String table, DbLinkEntity dbLinkEntity, List<Map<String, Object>> multiDataMapList) throws Exception {
        if (sqlPrintHandler.getPrintFlag() && multiDataMapList.size() > 0) {
            sqlPrintHandler.deleteAllInfo(SqlComEnum.DELETE_ALL.getOutSqlByDb(dbLinkEntity.getDbType(), new String[]{table}));
        }

        int total = multiDataMapList.size();
        int start = 1;

        Map dataMap;
        for(Iterator var5 = multiDataMapList.iterator(); var5.hasNext(); insert(dbLinkEntity, table, dataMap)) {
            dataMap = (Map)var5.next();
            if (total > 100) {
                log.info("表:" + table + "_数据：(" + start + "/" + total + ")");
                ++start;
            }
        }

    }

    public static void insert(DbLinkEntity dbLinkEntity, String table, Map<String, Object> dataMap) throws Exception {
        String dbEncode = dbLinkEntity.getDbType();
        if (sqlPrintHandler.getPrintFlag()) {
            sqlPrintHandler.insert(formatInsertSql(dataMap, table, dbEncode));
        } else {
            List<String> formatFieldList = new ArrayList();
            List<String> signList = new ArrayList();
            List<Object> valueList = new ArrayList();
            Iterator var7 = dataMap.entrySet().iterator();

            while(var7.hasNext()) {
                Map.Entry<String, Object> map = (Map.Entry)var7.next();
                signList.add("?");
                valueList.add(map.getValue());
                formatFieldList.add(FormatSql.getFieldName((String)map.getKey(), dbEncode));
            }

            PrepSqlDTO prepSqlDTO = (new PrepSqlDTO(SqlComEnum.INSERT.getOutSqlByDb(dbEncode, new String[]{table, String.join(",", formatFieldList), String.join(",", signList)}), valueList)).withConn(dbLinkEntity);
            JdbcUtil.insert(prepSqlDTO);
        }

    }

    private static String formatInsertSql(Map<String, Object> dataMap, String table, String dbEncode) {
        List<String> fieldList = new ArrayList();
        List<String> valueList = new ArrayList();
        StringBuilder oracleClobUpdate = new StringBuilder();
        Iterator var6 = dataMap.entrySet().iterator();

        while(var6.hasNext()) {
            Map.Entry<String, Object> field = (Map.Entry)var6.next();
            String column = (String)field.getKey();
            Object columnValue = field.getValue();
            columnValue = FormatSqlOracle.clobExecute(dbEncode, columnValue, table, (String)field.getKey(), dataMap, oracleClobUpdate);
            fieldList.add(FormatSql.getFieldName(column, dbEncode));
            valueList.add(FormatSql.formatValue(columnValue, dbEncode));
        }

        return SqlComEnum.INSERT.getOutSqlByDb(dbEncode, new String[]{table, String.join(",", fieldList), String.join(",", valueList)}) + ";\n" + (StringUtils.isNotEmpty(oracleClobUpdate) ? oracleClobUpdate.toString() : "");
    }

    public static String getFuzzyQuerySql(String selectSql, String column, String keyWord) throws DataException {
        return StringUtil.isNotEmpty(keyWord) ? SqlComEnum.LIKE.getOutSql(new String[]{selectSql, column, "%" + keyWord + "%"}) : selectSql;
    }

    public static Boolean tableDataExist(String dbLinkId, String table) throws Exception {
        DbLinkEntity dbLinkEntity = (DbLinkEntity)PrepSqlDTO.DB_LINK_FUN.apply(dbLinkId);
        String sql = SqlComEnum.COUNT_SIZE_TABLE.getOutSqlByDb(dbLinkEntity.getDbType(), new String[]{"F_COUNT", table});
        NotTenantPluginHolder.setNotSwitchFlag();
        return JdbcUtil.queryOneInt((new PrepSqlDTO(sql)).withConn(dbLinkId), "F_COUNT") > 0;
    }

    public static int getSum(DbLinkEntity dbLinkEntity, String table) throws Exception {
        PrepSqlDTO dto = (new PrepSqlDTO(SqlComEnum.COUNT_SIZE.getOutSql(new String[]{"COUNT_SUM", "SELECT * FROM " + FormatSql.getFieldName(table, dbLinkEntity.getDbType())}))).withConn(dbLinkEntity);
        NotTenantPluginHolder.setNotSwitchFlag();
        return JdbcUtil.queryOneInt(dto, "COUNT_SUM");
    }

    public static List<DbFieldModel> getFieldList(DbLinkEntity dbLinkEntity, String table) throws Exception {
        NotTenantPluginHolder.setNotSwitchFlag();
        return JdbcUtil.queryCustomMods(SqlComEnum.FIELDS.getPrepSqlDto(dbLinkEntity, table), DbFieldModel.class);
    }

    public static boolean isExistTable(DbLinkEntity dbLinkEntity, String table) throws Exception {
        NotTenantPluginHolder.setNotSwitchFlag();
        Integer total = JdbcUtil.queryOneInt(SqlComEnum.EXISTS_TABLE.getPrepSqlDto(dbLinkEntity, table), DbAliasEnum.TOTAL.getAlias());
        return total > 0;
    }

    public static List<DbTableFieldModel> getTableList(DbLinkEntity dbLinkEntity, String methodName) throws Exception {
        NotTenantPluginHolder.setNotSwitchFlag();
        List list = null;
        if (StringUtil.isNotEmpty(methodName) && DbAliasEnum.TABLE_TYPE.getAlias().equals(methodName)) {
            list = JdbcUtil.queryCustomMods(SqlComEnum.TABLESANDVIEW.getPrepSqlDto(dbLinkEntity, ""), DbTableFieldModel.class);
            return (List)list.stream().sorted(Comparator.comparing(DbTableFieldModel::getType).thenComparing(DbTableModelBase::getTable)).collect(Collectors.toList());
        } else {
            list = JdbcUtil.queryCustomMods(SqlComEnum.TABLES.getPrepSqlDto(dbLinkEntity, ""), DbTableFieldModel.class);
            return (List)list.stream().sorted(Comparator.comparing(DbTableModelBase::getTable)).collect(Collectors.toList());
        }
    }

    public static List<DbTableFieldModel> getTableList(DbLinkEntity dbLinkEntity) throws Exception {
        return getTableList(dbLinkEntity, (String)null);
    }

    public static DbTableFieldModel getTable(DbLinkEntity dbLinkEntity, String table) throws Exception {
        NotTenantPluginHolder.setNotSwitchFlag();
        List<DbTableFieldModel> dbTableFieldModelList = JdbcUtil.queryCustomMods(SqlComEnum.TABLE.getPrepSqlDto(dbLinkEntity, table), DbTableFieldModel.class);
        if (dbTableFieldModelList.size() < 1) {
            throw new DataException(MsgCode.DB010.get(new String[]{dbLinkEntity.getDbName(), table}));
        } else {
            DbTableFieldModel dbTableFieldModel = (DbTableFieldModel)dbTableFieldModelList.get(0);
            dbTableFieldModel.setDbEncode(dbLinkEntity.getDbType());
            return dbTableFieldModel;
        }
    }
}
