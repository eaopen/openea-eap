package org.openea.eap.extj.controller.admin.data;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.alibaba.fastjson.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Cleanup;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.database.datatype.db.interfaces.DtInterface;
import org.openea.eap.extj.database.datatype.sync.util.DtSyncUtil;
import org.openea.eap.extj.database.model.dbfield.DbFieldModel;
import org.openea.eap.extj.database.model.dto.PrepSqlDTO;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.database.source.impl.DbOracle;
import org.openea.eap.extj.database.sql.model.SqlPrintHandler;
import org.openea.eap.extj.database.sql.param.FormatSqlOracle;
import org.openea.eap.extj.database.sql.util.SqlFastUtil;
import org.openea.eap.extj.database.util.DataSourceUtil;
import org.openea.eap.extj.base.model.dbsync.DbSyncForm;
import org.openea.eap.extj.base.model.dbsync.DbSyncPrintForm;
import org.openea.eap.extj.base.model.dbsync.DbSyncVo;
import org.openea.eap.extj.base.service.DbLinkService;
import org.openea.eap.extj.base.service.DbSyncService;
import org.openea.eap.extj.base.service.DbTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.sql.Connection;
import java.util.*;

@Tag(name = "数据同步", description = "DataSync")
@RestController
@RequestMapping("/api/system/DataSync")
public class DbSyncController {

    @Autowired
    private DbSyncService dbSyncService;
    @Autowired
    private DbLinkService dblinkService;
    @Autowired
    private DbTableService dbTableService;
    @Autowired
    private SqlPrintHandler sqlPrintHandler;
    @Autowired
    private DataSourceUtil dataSourceUtil;

    /**
     * 验证连接
     *
     * @param dbSyncForm 页面参数
     * @return
     * @throws Exception
     */
    @PostMapping("Actions/checkDbLink")
    @Parameters({
            @Parameter(name = "dbSyncForm", description = "页面参数", required = true)
    })
    @SaCheckPermission("systemData.dataSync")
    @Operation(summary = "验证连接")
    public ActionResult<DbSyncVo> checkDbLink(@RequestBody DbSyncForm dbSyncForm) throws Exception {
        String fromDbType;
        String toDbType;
        DbSyncVo vo = new DbSyncVo();
        try {
            DbLinkEntity dbLinkEntity = dblinkService.getResource(dbSyncForm.getDbConnectionFrom());
            DbLinkEntity dbLinkEntity1 = dblinkService.getResource(dbSyncForm.getDbConnectionTo());
            fromDbType = dbLinkEntity.getDbType();
            toDbType = dbLinkEntity1.getDbType();
            @Cleanup Connection conn = PrepSqlDTO.getConn(dbLinkEntity);
            @Cleanup Connection conn1 = PrepSqlDTO.getConn(dbLinkEntity1);
            if (conn.getMetaData().getURL().equals(conn1.getMetaData().getURL())) {
                return ActionResult.fail("数据库连接不能相同");
            }
            vo.setCheckDbFlag(true);
            vo.setTableList(SqlFastUtil.getTableList(dbLinkEntity, null));
            // 字段类型全部对应关系
            Map<String, List<String>> ruleMap = getConvertRules(fromDbType, toDbType).getData();
            Map<String, String> defaultRuleMap = getDefaultRules(fromDbType, toDbType).getData();
            // 默认类型置顶
            for (String key : defaultRuleMap.keySet()) {
                List<String> list = ruleMap.get(key);
                if(list != null){
                    String rule = defaultRuleMap.get(key);
                    list.remove(rule);
                    list.add(0, rule + " (默认)");
                    ruleMap.put(key, list);
                }
            }
            vo.setConvertRuleMap(ruleMap);
        }catch (Exception e){
            return ActionResult.fail("数据库连接失败");
        }
        return ActionResult.success(vo);
    }

    /**
     * 执行数据同步
     *
     * @param dbSyncForm 数据同步参数
     * @return ignore
     * @throws Exception ignore
     */
    @PostMapping
    @Operation(summary = "数据同步校验")
    @Parameters({
            @Parameter(name = "dbSyncForm", description = "页面参数", required = true)
    })
    @SaCheckPermission("systemData.dataSync")
    public ActionResult<Object> checkExecute(@RequestBody DbSyncForm dbSyncForm) throws Exception {
        int status;
        try {
            status = dbSyncService.executeCheck(dbSyncForm.getDbConnectionFrom(), dbSyncForm.getDbConnectionTo(), dbSyncForm.getConvertRuleMap(), dbSyncForm.getDbTable());
        } catch (Exception e) {
            e.printStackTrace();
            return ActionResult.fail(e.getMessage());
        }
        if (status == -1) {
            return ActionResult.fail("请检查，同一数据库下无法同步数据");
        }
        return ActionResult.success(status);
    }

    /**
     * 执行数据同步
     *
     * @param dbSyncForm 数据同步参数
     * @return ignore
     * @throws Exception ignore
     */
    @PostMapping("Actions/Execute")
    @Operation(summary = "执行数据同步")
    @Parameters({
            @Parameter(name = "dbSyncForm", description = "页面参数", required = true)
    })
    @SaCheckPermission("systemData.dataSync")
    public ActionResult<String> execute(@RequestBody DbSyncForm dbSyncForm) {
        try{
            dbSyncService.execute(dbSyncForm.getDbConnectionFrom(), dbSyncForm.getDbConnectionTo(), dbSyncForm.getConvertRuleMap(), dbSyncForm.getDbTable());
        }catch (Exception e){
            e.printStackTrace();
            return ActionResult.fail("同步失败：" + e.getMessage());
        }
        return ActionResult.success("成功");
    }

    /**
     * 批量执行数据同步
     *
     * @param dbSyncForm 数据同步参数
     * @return ignore
     * @throws Exception ignore
     */
    @PostMapping("Actions/batchExecute")
    @Operation(summary = "批量执行数据同步")
    @Parameters({
            @Parameter(name = "dbSyncForm", description = "页面参数", required = true)
    })
    @SaCheckPermission("systemData.dataSync")
    public ActionResult<Map<String, Integer>> executeBatch(@RequestBody DbSyncForm dbSyncForm) {
        Map<String, Integer> result = dbSyncService.executeBatch(dbSyncForm.getDbConnectionFrom(), dbSyncForm.getDbConnectionTo(), dbSyncForm.getConvertRuleMap(), dbSyncForm.getDbTableList());
        boolean flag = true;
        List<String> failTableList = new ArrayList<>();
        for (String table : result.keySet()) {
            if(result.get(table) == 0){
                failTableList.add(table);
                flag = false;
            }
        }
        if(flag){
            return ActionResult.success("1", result);
        }else {
            return ActionResult.success("以下表执行同步失败: " + String.join(",", failTableList), result);
        }
    }

    /**
     * 获取数据类型默认转换规则
     * 一对一
     * @param fromDbType 被转换数据库类型
     * @param toDbType 转换数据库类型
     * @return 转换规则
     * @throws Exception 未找到数库
     */
    @GetMapping("Actions/getDefaultRules")
    @SaCheckPermission("systemData.dataSync")
    @Operation(summary = "获取一对一数据类型默认转换规则")
    public static ActionResult<Map<String, String>> getDefaultRules(String fromDbType, String toDbType) throws Exception{
        Map<String, String> map = new LinkedHashMap<>();
        for (DtInterface dtInterface : DtInterface.getClz(fromDbType).getEnumConstants()) {
            DtInterface toFixCovert = DtSyncUtil.getToFixCovert(dtInterface, toDbType);
            if(toFixCovert != null){
                map.put(dtInterface.getDataType(), toFixCovert.getDataType());
            }
        }
        return ActionResult.success(map);
    }

    /**
     * 获取数据类型转换规则
     * 一对多
     * @param fromDbType 被转换数据库类型
     * @param toDbType 转换数据库类型
     * @return 转换规则
     * @throws Exception 未找到数库
     */
    @GetMapping("Actions/getConvertRules")
    @SaCheckPermission("systemData.dataSync")
    @Operation(summary = "获取一对多数据类型转换规则")
    public static ActionResult<Map<String, List<String>>> getConvertRules(String fromDbType, String toDbType) throws Exception{
        Map<String, List<String>> map = new LinkedHashMap<>();
        for (DtInterface dtInterface : DtInterface.getClz(fromDbType).getEnumConstants()) {
            List<String> list = new LinkedList<>();
            DtInterface[] allConverts = DtSyncUtil.getAllConverts(dtInterface, toDbType);
            if(allConverts != null){
                for (DtInterface allConvert : allConverts) {
                    list.add(allConvert.getDataType());
                }
                map.put(dtInterface.getDataType(), list);
            }
        }
        return ActionResult.success(map);
    }

    /* ===================================== SQL转换项目 ======================================= */

    /**
     * 打印转换SQL
     *
     * @param form 参数表单
     */
    @PostMapping("Actions/print")
    @Operation(summary = "打印同步表")
    public ActionResult<Object> print(@RequestBody DbSyncPrintForm form) throws Exception{
        PrintFunction func = ()-> dbSyncService.printDbInit(form.getDbLinkFrom(), form.getDbTypeTo(),
                form.getDbTableList(), form.getConvertRuleMap(), form.getPrintType());
        return ActionResult.success(printCommon(form, form.getPrintType(), func));
    }

    @FunctionalInterface
    public interface PrintFunction {
        Object execute() throws Exception;
    }

    private Object printCommon(DbSyncPrintForm form, String printType, PrintFunction func) throws Exception {
        sqlPrintHandler.start(form.getOutPath(), true, true, true, form.getDbTypeTo());
        boolean flag = new File(form.getOutPath()).mkdir();
        sqlPrintHandler.setFileName(form.getOutFileName());
        Object obj = func.execute();
        sqlPrintHandler.print();
        sqlPrintHandler.close();
        SqlPrintHandler.openDirectory(new File(form.getOutPath()).getPath());
        return obj;
    }

    /**
     * 数据类型转换
     *
     * @param dataType 数据类型 例如:varchar(50)
     * @param fromDbEncode 源数据库类型
     * @param toDbEncode 目标数据库类型
     */
    @GetMapping("getConvertDataType")
    @Operation(summary = "数据类型转换")
    public String getConvertDataType(String dataType, String dtLength, String fromDbEncode, String toDbEncode) throws Exception{
        DbFieldModel dbFieldModel = new DbFieldModel();
        dbFieldModel.setLength(dtLength);
        DtInterface toFixCovert = DtSyncUtil.getToFixCovert(DtInterface.newInstanceByDt(dataType, fromDbEncode), toDbEncode);
        dbFieldModel.setDataType(toFixCovert.getDataType());
        return dbFieldModel.formatDataTypeByView(toDbEncode);
    }

    @PostMapping("formatOracleValue")
    @Operation(summary = "格式化SQL语句中的Oracle值")
    public String getConvertDataType(@RequestBody String data) throws Exception{
        Map<String, Object> json = JSONObject.parseObject(data).getInnerMap();
        // 指定F_Id为主键
        Map<String, Object> dataMap = JSONObject.parseObject(json.get("dataMap").toString()).getInnerMap();
        // 特殊处理：存在值超过2000的字符
        StringBuilder valStrBuilder = new StringBuilder();
        String context =  FormatSqlOracle.clobExecute(DbOracle.ORACLE, json.get("valueStr"),
                json.get("table").toString(), json.get("column").toString(), dataMap, valStrBuilder).toString();
        return valStrBuilder.toString();
    }




}
