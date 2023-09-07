package org.openea.eap.extj.base.service.impl;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.openea.eap.extj.base.Page;
import org.openea.eap.extj.base.Pagination;
import org.openea.eap.extj.base.util.dbutil.TableUtil;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.database.datatype.db.DtDMEnum;
import org.openea.eap.extj.database.datatype.db.interfaces.DtInterface;
import org.openea.eap.extj.database.datatype.viewshow.ViewDataTypeEnum;
import org.openea.eap.extj.database.datatype.viewshow.constant.DtViewConst;
import org.openea.eap.extj.database.enums.DbAliasEnum;
import org.openea.eap.extj.database.model.dbfield.DbFieldModel;
import org.openea.eap.extj.database.model.dbtable.DbTableFieldModel;
import org.openea.eap.extj.database.model.dto.PrepSqlDTO;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.database.model.page.DbTableDataForm;
import org.openea.eap.extj.database.model.page.JdbcPageMod;
import org.openea.eap.extj.database.source.DbBase;
import org.openea.eap.extj.database.source.impl.DbPostgre;
import org.openea.eap.extj.database.sql.enums.base.SqlComEnum;
import org.openea.eap.extj.database.sql.util.SqlFastUtil;
import org.openea.eap.extj.database.sql.util.SqlFrameUtil;
import org.openea.eap.extj.database.util.DbTypeUtil;
import org.openea.eap.extj.database.util.JdbcUtil;
import org.openea.eap.extj.database.util.NotTenantPluginHolder;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.base.service.DbLinkService;
import org.openea.eap.extj.base.service.DbTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DbTableServiceImpl implements DbTableService {

    @Autowired
    private DbLinkService dblinkService;
    @Autowired
    private DbTableService dbTableService;

    @Override
    public List<DbTableFieldModel> getList(String dbLinkId, String methodName) throws Exception {
        DbLinkEntity dbLinkEntity = dblinkService.getResource(dbLinkId);
        return SqlFastUtil.getTableList(dbLinkEntity, methodName);
    }

    @Override
    public List<DbTableFieldModel> getListPage(String dbLinkId, Page page) throws Exception {
        List<DbTableFieldModel> list = getList(dbLinkId, DbAliasEnum.TABLE_TYPE.getAlias());
        if(StringUtil.isNotEmpty(page.getKeyword())){
            // 过滤不符条件的元素
            String keyWord = SqlFrameUtil.keyWordTrim(page.getKeyword()).toLowerCase();
            list = list.stream().filter(t->
                    (StringUtil.isNotEmpty(t.getComment()) && t.getComment().toLowerCase().contains(keyWord))
                            || t.getTable().toLowerCase().contains(keyWord)
            ).collect(Collectors.toList());
        }
        return list;
    }

    @Override
    public List<DbTableFieldModel> getListPage(String dbLinkId, Pagination pagination) throws Exception {
        List<DbTableFieldModel> list = getList(dbLinkId, null);
        if(StringUtil.isNotEmpty(pagination.getKeyword())){
            // 过滤不符条件的元素
            String keyWord = SqlFrameUtil.keyWordTrim(pagination.getKeyword()).toLowerCase();
            list = list.stream().filter(t->
                    (StringUtil.isNotEmpty(t.getComment()) && t.getComment().toLowerCase().contains(keyWord))
                            || t.getTable().toLowerCase().contains(keyWord)
            ).collect(Collectors.toList());
        }
        long beginIndex = (pagination.getCurrentPage() -1) * pagination.getPageSize();
        long endIndex = beginIndex + pagination.getPageSize();
        List<DbTableFieldModel> listVO = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if(beginIndex <= i && i < endIndex){
                listVO.add(list.get(i));
            }
        }
        pagination.setTotal(list.size());
        return listVO;
    }

    @Override
    public DbTableFieldModel getTable(String dbLinkId, String table) throws Exception {
        DbLinkEntity dbLinkEntity = dblinkService.getResource(dbLinkId);
        DbTableFieldModel dbTableFieldModel = SqlFastUtil.getTable(dbLinkEntity, table);
        dbTableFieldModel.setHasTableData(getSum(dbLinkId, table) > 0);
        return dbTableFieldModel;
    }

    @Override
    public List<DbFieldModel> getFieldList(String dbLinkId, String table) throws Exception {
        DbLinkEntity dbLinkEntity = dblinkService.getResource(dbLinkId);
        return SqlFastUtil.getFieldList(dbLinkEntity, table);
    }

    @Override
    public boolean isExistTable(String dbLinkId, String table) throws Exception {
        DbLinkEntity dbLinkEntity = dblinkService.getResource(dbLinkId);
        return SqlFastUtil.isExistTable(dbLinkEntity, table);
    }

    @Override
    public boolean dropExistsTable(String dbLinkId, String table) throws Exception {
        DbLinkEntity dbLinkEntity = dblinkService.getResource(dbLinkId);
        if(isExistTable(dbLinkId, table)){
            return SqlFastUtil.dropTable(dbLinkEntity, table);
        }
        return true;
    }

    @Override
    public List<Map<String, Object>> getData(DbTableDataForm dbTableDataForm, String dbLinkId, String table) throws Exception {
        DbLinkEntity dbLinkEntity = dblinkService.getResource(dbLinkId);
        JdbcPageMod result = new JdbcPageMod();
        String orderKey = dbTableDataForm.getField();
        // 防止SQL注入
        String finalOrderKey = orderKey;
        if(getFieldList(dbLinkId, table).stream().noneMatch(field -> field.getField().equals(finalOrderKey))){
            throw  new DataException("排序字段不存在");
        }
        if (dbLinkEntity != null) {
            List<Object> dataList = new ArrayList<>();
            if(DbTypeUtil.checkPostgre(dbLinkEntity)){
                table = DbPostgre.getTable(table);
                orderKey = DbPostgre.getTable(orderKey);
                String schema = dbLinkEntity.getDbSchema();
                if(StringUtil.isNotEmpty(schema)){
                    table = schema + "." + table;
                }
            }
            StringBuilder sql = new StringBuilder(SqlComEnum.SELECT_TABLE.getOutSql(table));
            //模糊查询
            if (!StringUtil.isEmpty(dbTableDataForm.getKeyword()) && !StringUtil.isEmpty(orderKey)) {
                sql.append(" where " + orderKey + " like ?");
                dataList.add("%" + SqlFrameUtil.keyWordTrim(dbTableDataForm.getKeyword()) + "%");
            }
            result = JdbcUtil.queryPage(
                            new PrepSqlDTO(sql.toString(), dataList).withConn(dbLinkEntity),
                            checkOrderKey(orderKey, dbLinkId, table, dbLinkEntity.getDbType()),
                            (int) dbTableDataForm.getCurrentPage(),
                            (int) dbTableDataForm.getPageSize())
                    .setIsLowerCase(true).setIsAlias(true).get();
        }
        dbTableDataForm.setTotal(result.getTotalRecord());
        return (List<Map<String, Object>>)result.getDataList();
    }

    private String checkOrderKey(String orderKey, String dbLinkId, String table, String dbEncode){
        try {
            if(DbBase.DM.equals(dbEncode)){
                List<DbFieldModel> fieldList = getFieldList(dbLinkId, table);
                Optional<DbFieldModel> first = fieldList.stream().filter(field -> field.getField().equals(orderKey)).findFirst();
                if(first.isPresent()){
                    String dataType = first.get().getDataType();
                    if(dataType.equals(DtDMEnum.CLOB.getDataType()) || dataType.equals(DtDMEnum.TEXT.getDataType())){
//                        throw new Exception("无法使用CLOB、TEXT作为字段排序条件");
                        Optional<DbFieldModel> first1 = fieldList.stream().filter(field -> field.getField().toLowerCase().contains("id")).findFirst();
                        if(first1.isPresent()){
                            return first1.get().getField();
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderKey;
    }

    @Override
    public int getSum(String dbLinkId,String table)throws Exception {
        DbLinkEntity dbLinkEntity = dblinkService.getResource(dbLinkId);
        return SqlFastUtil.getSum(dbLinkEntity, table);
    }

    /**=====================增删改========================**/

    @Override
    public void delete(String dbLinkId, String table) throws Exception {
        // 校验
        checkTab(table, dbLinkId, 1);
        SqlFastUtil.dropTable(dblinkService.getResource(dbLinkId), table);
    }

    @Override
    public void deleteAllTable(String dbLinkId, String dbType) throws Exception {
        DbLinkEntity dbLinkEntity = dblinkService.getResource(dbLinkId);
        if(!dbLinkEntity.getDbType().equals(dbType)) throw new Exception("数据库类型不符");
        for (DbTableFieldModel dbTableFieldModel : SqlFastUtil.getTableList(dbLinkEntity)) {
            SqlFastUtil.dropTable(dbLinkEntity, dbTableFieldModel.getTable());
        }
    }

    @Override
    public int createTable(DbTableFieldModel dbTableFieldModel) throws Exception {
        DbLinkEntity dbLinkEntity = dblinkService.getResource(dbTableFieldModel.getDbLinkId());
        // 数据库编码查询校验 (打印模板校验)
        String opeDb = dbLinkEntity.getDbType();
        String creDb =  dbTableFieldModel.getDbEncode();
        if(StringUtil.isNotEmpty(creDb)  && !creDb.equals(opeDb)){
            throw new DataException(MsgCode.DB008.get() + "：" + creDb +  " -> " + opeDb);
        }
        // 表重名判断
        if (isExistTable(dbTableFieldModel.getDbLinkId(), dbTableFieldModel.getTable())) {
            return 0;
        }
        // 表主键检验
        checkPrimary(dbTableFieldModel.getDbFieldModelList(), dbLinkEntity.getDbType());
        // 创建表
        SqlFastUtil.creTable(dbLinkEntity, dbTableFieldModel);
        return 1;
    }

    @Override
    public void update(DbTableFieldModel dbTableFieldModel) throws Exception {
        // 校验
        checkTab(dbTableFieldModel.getUpdateOldTable(), dbTableFieldModel.getDbLinkId(), 2);
        DbLinkEntity dbLinkEntity = dblinkService.getResource(dbTableFieldModel.getDbLinkId());
        //临时表名
        String oldTable = dbTableFieldModel.getUpdateOldTable();
        String newTable = dbTableFieldModel.getUpdateNewTable();
        String tempTabName = "Temp_" + TableUtil.getStringRandom(5);
        // 第一步：创建新表 (成功则删除旧表，失败则回滚)
        checkPrimary(dbTableFieldModel.getDbFieldModelList(), dbLinkEntity.getDbType());
        SqlFastUtil.creTable(dbLinkEntity,
                new DbTableFieldModel(
                        tempTabName,
                        dbTableFieldModel.getComment(),
                        dbTableFieldModel.getDbFieldModelList()
                ));
        // 第二步：删除旧表
        SqlFastUtil.dropTable(dbLinkEntity, oldTable);
        // 第三步：新表改名
        SqlFastUtil.reTableName(dbLinkEntity, tempTabName, newTable);
    }

    @Override
    public void addField(DbTableFieldModel dbTableFieldModel) throws Exception {
        DbLinkEntity dbLinkEntity = dblinkService.getResource(dbTableFieldModel.getDbLinkId());
        SqlFastUtil.addField(dbLinkEntity, dbTableFieldModel.getUpdateNewTable(), dbTableFieldModel.getDbFieldModelList());
    }

    @Override
    public DbTableFieldModel getDbTableModel(String dbLinkId, String tableName)throws Exception {
        NotTenantPluginHolder.setNotSwitchFlag();
        DbTableFieldModel dbTableModel = getList(dbLinkId, null).stream().filter(m -> m.getTable().equals(tableName)).findFirst().orElse(null);
        if(dbTableModel != null){
            dbTableModel.setDbFieldModelList(getFieldList(dbLinkId, tableName));
            return dbTableModel;
        }else {
            throw new DataException("未找到此表：" + tableName);
        }
    }

    /* ================复用代码================== */

    /**
     * 校验表可操作
     *
     * @param table 表
     * @param dbLinkId 数据连接ID
     * @param type 1:删除 2:编辑
     * @throws Exception 错误信息
     */
    private void checkTab(String table, String dbLinkId, Integer type) throws Exception {
        if (TableUtil.checkByoTable(table)) throw new DataException(type == 1 ? MsgCode.DB101.get() : MsgCode.DB102.get());
        if (getSum(dbLinkId, table) > 0) throw new DataException(type == 1 ? MsgCode.DB201.get() : MsgCode.DB202.get());
    }

    /**
     * 检查主键
     * @param tableFieldList  表字段集合
     * @throws DataException ignore
     */
    private void checkPrimary(List<DbFieldModel> tableFieldList, String dbEncode) throws Exception {
        // 默认主键为字符串类型
        // 主键会自动添加"非空"限制，所以不用做判断。(为空不加语句，且数据库默认字段可为空)
        int autoIncrementNum = 0;
        for(DbFieldModel field : tableFieldList) {
            if(field.getIsAutoIncrement() != null && field.getIsAutoIncrement()){
                field.setNullSign("NOT NULL");
                // 一张表最多只有一个自增主键，且此字段必须为primary key或者unique key。
                autoIncrementNum += 1;
                if(autoIncrementNum > 1){
                    throw new DataException("一张表最多只运行有一个自增主键");
                }
                if(!(field.getDataType().equals(DtViewConst.INT) || field.getDataType().equals(DtViewConst.BIGINT))){
                    throw new DataException("自增字段类型必须为数字类型");
                }
                if(!field.getIsPrimaryKey()){
                    throw new DataException("自增字段类型必须为主键");
                }
            }else {
                if (field.getIsPrimaryKey()) {
                    field.setNullSign("NOT NULL");
                    Method method = ViewDataTypeEnum.class.getMethod("getDt" + dbEncode + "Enum");
                    Map<ViewDataTypeEnum, String> allowDtMap = ImmutableMap.of(
                            ViewDataTypeEnum.VARCHAR, DtViewConst.VARCHAR,
                            ViewDataTypeEnum.INT, DtViewConst.INT,
                            ViewDataTypeEnum.BIGINT, DtViewConst.BIGINT
                    );
                    boolean primaryFlag = false;
                    for (Map.Entry<ViewDataTypeEnum, String> mapEntity : allowDtMap.entrySet()) {
                        DtInterface primaryVarcharEnum = (DtInterface)(method.invoke(mapEntity.getKey()));
                        boolean enumFlag = field.getDataType().equalsIgnoreCase(primaryVarcharEnum.getDataType());
                        boolean viewFlag = field.getDataType().equalsIgnoreCase(mapEntity.getValue());
                        if(enumFlag || viewFlag){
                            primaryFlag = true;
                        }
                    }
                    if(!primaryFlag){
                        throw new DataException("主键必须为字符串或整型、长整型。");
                    }
                }
            }
        }
    }
}
