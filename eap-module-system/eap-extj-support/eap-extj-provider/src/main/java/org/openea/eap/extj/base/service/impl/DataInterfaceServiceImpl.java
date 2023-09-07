package org.openea.eap.extj.base.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.ActionResultCode;
import org.openea.eap.extj.base.Pagination;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.base.entity.DataInterfaceEntity;
import org.openea.eap.extj.base.entity.InterfaceOauthEntity;
import org.openea.eap.extj.base.mapper.DataInterfaceMapper;
import org.openea.eap.extj.base.model.dataInterface.*;
import org.openea.eap.extj.base.service.*;
import org.openea.eap.extj.base.util.DataInterfaceParamUtil;
import org.openea.eap.extj.base.util.interfaceUtil.InterfaceUtil;
import org.openea.eap.extj.base.vo.PaginationVO;
import org.openea.eap.extj.config.ConfigValueUtil;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.database.model.dto.PrepSqlDTO;
import org.openea.eap.extj.database.util.DataSourceUtil;
import org.openea.eap.extj.database.util.JdbcUtil;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.permission.entity.OrganizeAdministratorEntity;
import org.openea.eap.extj.permission.model.datainterface.DataInterfaceVarEnum;
import org.openea.eap.extj.permission.service.OrganizeAdministratorService;
import org.openea.eap.extj.permission.service.OrganizeService;
import org.openea.eap.extj.util.*;
import org.openea.eap.extj.util.jscript.JScriptUtil;
import org.openea.eap.extj.util.wxutil.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DataInterfaceServiceImpl extends SuperServiceImpl<DataInterfaceMapper, DataInterfaceEntity> implements DataInterfaceService {
    @Autowired
    private EapUserProvider userProvider;
    @Autowired
    private DbLinkService dblinkService;
    @Autowired
    private DataSourceUtil dataSourceUtils;
    @Autowired
    private DataInterfaceLogService dataInterfaceLogService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private InterfaceOauthService interfaceOauthService;
    @Autowired
    private OrganizeService organizeService;
    @Autowired
    private OrganizeAdministratorService organizeAdminIsTratorService;


    @Override
    public List<DataInterfaceEntity> getList(PaginationDataInterface pagination, String dataType, boolean enabledMark) {
        // 定义变量判断是否需要使用修改时间倒序
        boolean flag = false;
        QueryWrapper<DataInterfaceEntity> queryWrapper = new QueryWrapper<>();
        //下拉框，查询启用的接口
        if (enabledMark) {
            queryWrapper.lambda().eq(DataInterfaceEntity::getEnabledMark,1);
        }
        //关键字
        if (!StringUtil.isEmpty(pagination.getKeyword())) {
            flag = true;
            queryWrapper.lambda().and(
                    t -> t.like(DataInterfaceEntity::getFullName, pagination.getKeyword())
                            .or().like(DataInterfaceEntity::getEnCode, pagination.getKeyword())
            );
        }
        // 是否分页
        if (pagination.getHasPage() != null && pagination.getHasPage() == 0) {
            queryWrapper.lambda().eq(DataInterfaceEntity::getCheckType, pagination.getHasPage());
        }
        //分类
        queryWrapper.lambda().eq(DataInterfaceEntity::getCategoryId, pagination.getCategoryId());
        // 类型
        if (StringUtil.isNotEmpty(dataType)) {
            queryWrapper.lambda().eq(DataInterfaceEntity::getDataType, Integer.valueOf(dataType));
        }
        //排序
        queryWrapper.lambda().orderByAsc(DataInterfaceEntity::getSortCode)
                .orderByDesc(DataInterfaceEntity::getCreatorTime);
        if (flag) {
            queryWrapper.lambda().orderByDesc(DataInterfaceEntity::getLastModifyTime);
        }
        queryWrapper.lambda().select(DataInterfaceEntity::getId, DataInterfaceEntity::getCheckType, DataInterfaceEntity::getCreatorTime, DataInterfaceEntity::getDataType,
                DataInterfaceEntity::getEnCode, DataInterfaceEntity::getEnabledMark, DataInterfaceEntity::getFullName, DataInterfaceEntity::getSortCode,
                DataInterfaceEntity::getRequestMethod, DataInterfaceEntity::getRequestParameters);
        Page<DataInterfaceEntity> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<DataInterfaceEntity> iPage = this.page(page, queryWrapper);
        return pagination.setData(iPage.getRecords(), iPage.getTotal());
    }

    @Override
    public List<DataInterfaceEntity> getList(boolean filterPage) {
        QueryWrapper<DataInterfaceEntity> queryWrapper = new QueryWrapper<>();
        if (filterPage) {
            queryWrapper.lambda().ne(DataInterfaceEntity::getCheckType, 1);
        }
        queryWrapper.lambda().eq(DataInterfaceEntity::getEnabledMark, 1)
                .orderByAsc(DataInterfaceEntity::getSortCode)
                .orderByDesc(DataInterfaceEntity::getCreatorTime);
//        queryWrapper.lambda().in(DataInterfaceEntity::getRequestMethod, 3,6,7);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public DataInterfaceEntity getInfo(String id) {
        QueryWrapper<DataInterfaceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DataInterfaceEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(DataInterfaceEntity entity) {
        if (entity.getId() == null) {
            entity.setId(RandomUtil.uuId());
            entity.setCreatorUser(userProvider.get().getUserId());
            entity.setCreatorTime(DateUtil.getNowDate());
            entity.setLastModifyTime(DateUtil.getNowDate());
        }
        this.saveOrUpdateIgnoreLogic(entity);
    }

    @Override
    public boolean update(DataInterfaceEntity entity, String id) throws DataException {
        entity.setId(id);
        entity.setLastModifyUser(userProvider.get().getUserId());
        entity.setLastModifyTime(DateUtil.getNowDate());
        return this.updateById(entity);
    }

    @Override
    public void delete(DataInterfaceEntity entity) {
        this.removeById(entity.getId());
    }

    @Override
    public boolean isExistByFullName(String fullName, String id) {
        QueryWrapper<DataInterfaceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DataInterfaceEntity::getFullName, fullName);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(DataInterfaceEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public boolean isExistByEnCode(String enCode, String id) {
        QueryWrapper<DataInterfaceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DataInterfaceEntity::getEnCode, enCode);
        if (!StringUtil.isEmpty(id)) {
            queryWrapper.lambda().ne(DataInterfaceEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public ActionResult infoToId(String id) {
        return infoToId(id, null, null);
    }

    @Override
    public ActionResult infoToIdPageList(String id, DataInterfacePage page) {
        DataInterfaceEntity entity = this.getInfo(id);
        if (entity == null) {
            return ActionResult.page(new ArrayList<>(), JsonUtil.getJsonToBean(new Pagination(), PaginationVO.class));
        }
        if (entity.getCheckType() == 1) {
            Map<String, String> map = null;
            if (page.getParamList() != null) {
                map = new HashMap<>();
                List<DataInterfaceModel> jsonToList = JsonUtil.getJsonToList(page.getParamList(), DataInterfaceModel.class);
                for (DataInterfaceModel dataInterfaceModel : jsonToList) {
                    String defaultValue = dataInterfaceModel.getDefaultValue();
                    if ("".equals(defaultValue) && "int".equals(dataInterfaceModel.getDataType())) {
                        map.put(dataInterfaceModel.getField(), "0");
                    } else {
                        if ("varchar".equals(dataInterfaceModel.getDataType()) && defaultValue == null) {
                            defaultValue = "";
                        }
                        map.put(dataInterfaceModel.getField(), defaultValue);
                    }
                }
            }
            Pagination pagination = new Pagination();
            pagination.setPageSize(page.getPageSize());
            pagination.setCurrentPage(page.getCurrentPage());
            pagination.setKeyword(page.getKeyword());
            return infoToId(id, null, map, null, null, null, pagination, null);
        } else {
            String dataProcessing = null;
            if (Objects.nonNull(entity) && StringUtil.isNotEmpty(entity.getDataProcessing())) {
                dataProcessing = entity.getDataProcessing();
            }
            List<Map<String, Object>> dataList = new ArrayList<>();
            int total = 0;
            Map<String, String> map = null;
            if (page.getParamList() != null) {
                map = new HashMap<>();
                List<DataInterfaceModel> jsonToList = JsonUtil.getJsonToList(page.getParamList(), DataInterfaceModel.class);
                for (DataInterfaceModel dataInterfaceModel : jsonToList) {
                    String defaultValue = dataInterfaceModel.getDefaultValue();
                    if ("".equals(defaultValue) && "int".equals(dataInterfaceModel.getDataType())) {
                        map.put(dataInterfaceModel.getField(), "0");
                    } else {
                        if ("varchar".equals(dataInterfaceModel.getDataType()) && defaultValue == null) {
                            defaultValue = "";
                        }
                        map.put(dataInterfaceModel.getField(), defaultValue);
                    }
                }
            }
            ActionResult result = infoToId(id, null, map);
            if(result.getData() != null) {
                if (result.getData() instanceof List) {
                    dataList = (List<Map<String, Object>>) result.getData();
                    total = dataList.size();
                }
            }
            if (StringUtil.isNotEmpty(page.getKeyword()) && StringUtil.isNotEmpty(page.getRelationField())) {
                dataList = dataList.stream().filter(t -> String.valueOf(t.get(page.getRelationField())).contains(page.getKeyword())).collect(Collectors.toList());
            }
            PaginationVO pagination = new PaginationVO();
            page.setTotal(dataList.size());
            if (StringUtil.isNotEmpty(page.getKeyword()) && StringUtil.isNotEmpty(page.getColumnOptions())) {
                String[] colOptions = page.getColumnOptions().split(",");
                dataList = dataList.stream().filter(t ->{
                            boolean isFit = false;
                            for (String c : colOptions){
                                if (String.valueOf(t.get(c)).contains(page.getKeyword())){
                                    isFit = true;
                                    break;
                                }
                            }
                            return isFit;
                        }
                ).collect(Collectors.toList());
            }
            dataList = PageUtil.getListPage((int) page.getCurrentPage(), (int) page.getPageSize(), dataList);
            pagination = JsonUtil.getJsonToBean(page, PaginationVO.class);
            return ActionResult.page(dataList, pagination, dataProcessing);
        }
    }

    @Override
    public List<Map<String, Object>> infoToInfo(String id, DataInterfacePage page) {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, String> map = null;
        DataInterfaceEntity entity = this.getInfo(id);
        if (entity == null) {
            return new ArrayList<>();
        }
        try {
            if (entity.getCheckType() == 1) {
                if (page.getParamList() != null) {
                    map = new HashMap<>();
                    List<DataInterfaceModel> jsonToList = JsonUtil.getJsonToList(page.getParamList(), DataInterfaceModel.class);
                    for (DataInterfaceModel dataInterfaceModel : jsonToList) {
                        String defaultValue = dataInterfaceModel.getDefaultValue();
                        if ("".equals(defaultValue) && "int".equals(dataInterfaceModel.getDataType())) {
                            map.put(dataInterfaceModel.getField(), "0");
                        } else {
                            if ("varchar".equals(dataInterfaceModel.getDataType()) && defaultValue == null) {
                                defaultValue = "";
                            }
                            map.put(dataInterfaceModel.getField(), defaultValue);
                        }
                    }
                }
                Map<String, Object> showMap = new HashMap<>();
                if (page.getIds() instanceof List) {
                    List<Object> ids = (List<Object>) page.getIds();
                    Map<String, String> finalMap = map;
                    ids.forEach(t -> {
                        showMap.put(page.getPropsValue(), t);
                        ActionResult result = infoToId(id, null, finalMap, null, null, null, null, showMap);
                        if (result.getData() instanceof Map) {
                            Map<String, Object> objectMap = (Map<String, Object>) result.getData();
                            if (objectMap.size() > 0) {
                                List<Map> mapList = JsonUtil.getJsonToList(objectMap.get("list"), Map.class);
                                if (mapList != null && mapList.size() > 0) {
                                    list.add(mapList.get(0));
                                } else {
                                    list.add(objectMap);
                                }
                            }
                        } else if (result.getData() instanceof List) {
                            List<Map> list1 = (List<Map>) result.getData();
                            list.add(list1.get(0));
                        } else {

                        }
                    });
                }
            } else {
                if (page.getIds() != null) {
                    Map<String, Object> dataMap = new HashMap<>();
                    if (page.getParamList() != null) {
                        map = new HashMap<>();
                        List<DataInterfaceModel> jsonToList = JsonUtil.getJsonToList(page.getParamList(), DataInterfaceModel.class);
                        for (DataInterfaceModel dataInterfaceModel : jsonToList) {
                            String defaultValue = dataInterfaceModel.getDefaultValue();
                            if ("".equals(defaultValue) && "int".equals(dataInterfaceModel.getDataType())) {
                                map.put(dataInterfaceModel.getField(), "0");
                            } else {
                                if ("varchar".equals(dataInterfaceModel.getDataType()) && defaultValue == null) {
                                    defaultValue = "";
                                }
                                map.put(dataInterfaceModel.getField(), defaultValue);
                            }
                        }
                    }
                    ActionResult result = infoToId(id, null, map);
                    List<Map<String, Object>> dataList = new ArrayList<>();
                    if (result.getData() instanceof List) {
                        dataList = (List<Map<String, Object>>) result.getData();
                        List<String> ids = (List<String>) page.getIds();
                        List<Map<String, Object>> finalDataList = dataList;
                        ids.forEach(t->{
                            list.add(finalDataList.stream().filter(data -> t.equals(String.valueOf(data.get(page.getPropsValue())))).findFirst().orElse(new HashMap<>()));
                        });
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return list;
        }
        return list;
    }

    @Override
    public ActionResult infoToId(String id, String tenantId, Map<String, String> map) {
        return infoToId(id, tenantId, map, null, null, null, null, null);
    }

    @Override
    public ActionResult infoToId(String id, String tenantId, Map<String, String> map, String token, String appId, String invokType, Pagination pagination, Map<String,Object> showMap) {
        DataInterfaceEntity entity = this.getInfo(id);
        if (entity == null) {
            entity = new DataInterfaceEntity();
        }
        //开始调用的时间
        LocalDateTime dateTime = LocalDateTime.now();
        //调用时间
        int invokWasteTime = 0;
        // 验证参数必填或类型
        String checkRequestParams = checkRequestParams(entity.getRequestParameters(), map, entity.getQuerySql());
        if (StringUtil.isNotEmpty(checkRequestParams)) {
            return ActionResult.fail(checkRequestParams);
        }
        Object callJs = null;
        try {
            //参数默认值替换
            List<Map<String, Object>> jsonToListMap = JsonUtil.getJsonToListMap(entity.getRequestParameters());
            if(ObjectUtils.isNotEmpty(jsonToListMap)){
                if(map == null){
                    map = new HashMap<>(16);
                }
                for (Map<String, Object> stringObjectMap : jsonToListMap) {
                    String field = stringObjectMap.getOrDefault("field", StringUtil.EMPTY).toString();
                    Object defaultValue = stringObjectMap.get("defaultValue");
                    if(!map.containsKey(field) && ObjectUtils.isNotEmpty(defaultValue)){
                        map.put(field, defaultValue.toString());
                    }
                }
            }
            if (pagination == null) {
                pagination = new Pagination();
            }
            //如果是静态数据
            if (entity.getDataType() == 2) {
                try {
                    Map<String, Object> maps = JsonUtil.stringToMap(entity.getQuerySql());
                    return ActionResult.success(maps != null ? maps : new ArrayList<>());
                } catch (Exception e) {
                    try {
                        List<Map<String, Object>> list = JsonUtil.getJsonToListMap(entity.getQuerySql());
                        return ActionResult.success(list != null ? list : new ArrayList<>());
                    } catch (Exception exception) {
                        Object obj = entity.getQuerySql() != null ? entity.getQuerySql() : null;
                        return ActionResult.success(obj != null ? obj : new ArrayList<>());
                    }
                }
            } else if (entity.getDataType() == 3) {
                JSONObject jsonObject = new JSONObject();
                if (showMap == null) {
                    if (entity.getCheckType() == null || entity.getCheckType() == 0) {
                        pagination = null;
                    }
                    //HTTP调用或HTTPS调用
                    jsonObject = callHTTP(entity, map, token, pagination, null);
                } else {
                    jsonObject = callHTTP(entity, map, token, null, showMap);
                }
                if (Objects.nonNull(jsonObject) && "1".equals(jsonObject.get("errorCode"))) {
                    return ActionResult.fail("外部接口暂时只支持HTTP和HTTPS方式");
                }
                // 判断返回参数长度和key是否跟内置的一致
                if (jsonObject == null) {
                    return ActionResult.fail("接口请求失败");
                }
                if (isInternal(jsonObject)) {
                    callJs = JScriptUtil.callJs(entity.getDataProcessing(), jsonObject.get("data") == null ? new ArrayList<>() : jsonObject.get("data"));
                } else {
                    callJs = JScriptUtil.callJs(entity.getDataProcessing(), jsonObject == null ? new ArrayList<>() : jsonObject);
                }
            } else if (entity.getDataType() == 1) {
                if (showMap == null) {
                    List<Map<String, Object>> sqlMapList = executeSql(entity, null, map, pagination, null);
                    callJs = JScriptUtil.callJs(entity.getDataProcessing(), sqlMapList == null ? new ArrayList<>() : sqlMapList);
                    if (entity.getCheckType() != null && entity.getCheckType() == 1) {
                        List<Map<String, Object>> maps = executeSql(entity, "1", map, pagination, null);
                        if (maps.get(0) != null) {
                            Long total = Long.valueOf(String.valueOf(maps.get(0).values().stream().findFirst().orElse(sqlMapList.size())));
                            pagination.setTotal(total);
                        }
                        return ActionResult.page(sqlMapList, JsonUtil.getJsonToBean(pagination, PaginationVO.class));
                    }
                } else {
                    List<Map<String, Object>> sqlMapList = executeSql(entity, "2", map, pagination, showMap);
                    callJs = JScriptUtil.callJs(entity.getDataProcessing(), sqlMapList == null && sqlMapList.size() > 0 ? new ArrayList<>() : sqlMapList.get(0));
                }
            }
            if (callJs instanceof Exception) {
                return ActionResult.success("接口请求失败", "JS调用失败,错误：" + ((Exception) callJs).getMessage());
            }
            return ActionResult.success(callJs);
        } catch (Exception e) {
            log.error("错误提示:" + e.getMessage());
            // 本地调试时打印出问题
            e.printStackTrace();
            return ActionResult.fail("接口请求失败");
        } finally {
            //调用时间
            invokWasteTime = invokTime(dateTime);
            //添加调用日志
            dataInterfaceLogService.create(id, invokWasteTime, appId, invokType);
        }
    }

    /**
     * 检查参数是够必填或类型是否正确
     *
     * @param requestParameters
     * @param map
     */
    private String checkRequestParams(String requestParameters, Map<String, String> map, String sql) {
        if (map == null || StringUtil.isEmpty(requestParameters)) {
            return "";
        }
        StringBuilder message = new StringBuilder();
        List<Map<String, Object>> jsonToListMap = JsonUtil.getJsonToListMap(requestParameters);
        jsonToListMap.forEach(t -> {
            if (StringUtil.isEmpty(message.toString())) {
                DataInterfaceModel model = JsonUtil.getJsonToBean(t, DataInterfaceModel.class);
                // 验证是否必填
                if ("1".equals(String.valueOf(model.getRequired()))) {
                    String value = map.get(model.getField());
                    if (StringUtil.isEmpty(value)) {
                        message.append(model.getField() + "不能为空");
                    }
                }
                if (StringUtil.isEmpty(message.toString())) {
                    // 验证类型
                    if (model.getDataType() != null) {
                        // 判断是整形
                        if ("int".equals(model.getDataType())) {
                            String value = map.get(model.getField());
                            if (sql.contains(" in ") && value.contains(",")) {
                                String[] valueSpilt = value.split(",");
                                for (int i = 0; i < valueSpilt.length; i++) {
                                    if (StringUtil.isNotEmpty(valueSpilt[i])) {
                                        try {
                                            Integer.parseInt(valueSpilt[i]);
                                        } catch (Exception e) {
                                            message.append(model.getField() + "类型必须为整型");
                                        }
                                    }
                                }
                            } else {
                                try {
                                    Integer.parseInt(value);
                                } catch (Exception e) {
                                    message.append(model.getField() + "类型必须为整型");
                                }
                            }
                        } else if ("datetime".equals(model.getDataType())) {
                            String value = map.get(model.getField());
                            if (sql.contains(" in ") && value.contains(",")) {
                                String[] valueSpilt = value.split(",");
                                for (int i = 0; i < valueSpilt.length; i++) {
                                    if (StringUtil.isNotEmpty(valueSpilt[i])) {
                                        try {
                                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                            formatter.parse(valueSpilt[i]);
                                        } catch (Exception e) {
                                            message.append(model.getField() + "类型必须为日期时间型");
                                        }
                                    }
                                }
                            } else {
                                try {
                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    formatter.parse(value);
                                } catch (Exception e) {
                                    message.append(model.getField() + "类型必须为日期时间型");
                                }
                            }
                        } else if ("decimal".equals(model.getDataType())) {
                            String value = map.get(model.getField());
                            if (sql.contains(" in ") && value.contains(",")) {
                                String[] valueSpilt = value.split(",");
                                for (int i = 0; i < valueSpilt.length; i++) {
                                    if (StringUtil.isNotEmpty(valueSpilt[i])) {
                                        try {
                                            Double.valueOf(valueSpilt[i]);
                                        } catch (Exception e) {
                                            message.append(model.getField() + "类型必须为浮点型");
                                        }
                                    }
                                }
                            } else {
                                try {
                                    Double.valueOf(value);
                                } catch (Exception e) {
                                    message.append(model.getField() + "类型必须为浮点型");
                                }
                            }
                        }
                    }
                }
            }
        });
        return message.toString();
    }

    @Override
    public ActionResult infoToIdNew(String id, String tenantId, DataInterfaceActionModel model) {
        //鉴权验证
        // 获取token
        String authorSignature = ServletUtil.getRequest().getHeader(Constants.AUTHORIZATION);
        String[] authorSignatureArr = authorSignature.split(":");
        if (authorSignatureArr.length != 3) {
            return ActionResult.fail(ActionResultCode.ValidateError.getMessage());
        }
        String appId = authorSignatureArr[0];
        String author = authorSignatureArr[2];
        Map<String, String> map = model.getMap();
        InterfaceOauthEntity infoByAppId = interfaceOauthService.getInfoByAppId(appId);
        //未提供app相关，接口认证失效，接口不在授权列表时无权访问
        if (infoByAppId == null||infoByAppId.getEnabledMark()==0||!infoByAppId.getDataInterfaceIds().contains(id)) {
            return ActionResult.fail(MsgCode.WF122.get());
        }
        if(infoByAppId.getVerifySignature()==1){//验证开启
            try {
                //验证请求有效期1分钟内
                String ymdateStr = ServletUtil.getRequest().getHeader(InterfaceUtil.YMDATE);
                Date ymdate = new Date(Long.parseLong(ymdateStr));
                Date time = DateUtil.dateAddMinutes(ymdate, 1);
                if (DateUtil.getNowDate().after(time)) {
                    return ActionResult.fail("验证请求超时");
                }
                //验证签名有效性
                boolean flag = InterfaceUtil.verifySignature(infoByAppId.getAppSecret(), author);
                if (!flag) {
                    return ActionResult.fail(ActionResultCode.ValidateError.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ActionResult.fail(ActionResultCode.ValidateError.getMessage());
            }
        }else{//验证未开启，直接使用秘钥进行验证
            if(!infoByAppId.getAppSecret().equals(author)){
                return ActionResult.fail("appSecret错误");
            }
        }
        //验证使用期限
        Date usefulLife = infoByAppId.getUsefulLife();
        if (infoByAppId.getUsefulLife()!=null && usefulLife.before(DateUtil.getNowDate())) {//空值无限期
            return ActionResult.fail("appId使用期限已到期");
        }
        //黑白名单验证
        String ipwhiteList = StringUtil.isNotEmpty(infoByAppId.getWhiteList()) ? infoByAppId.getWhiteList() : "";//ip白名单
        String ipwhiteBlackList = StringUtil.isNotEmpty(infoByAppId.getBlackList()) ? infoByAppId.getBlackList() : "";//ip黑名单
        String ipAddr = IpUtil.getIpAddr();
        if (StringUtil.isNotEmpty(ipwhiteList) && !ipwhiteList.contains(ipAddr)) {//不属于白名单
            return ActionResult.fail(MsgCode.LOG010.get());
        }
//        if (StringUtil.isNotEmpty(ipwhiteBlackList) && ipwhiteBlackList.contains(ipAddr)) {//属于黑名单
//            return ActionResult.fail(ActionResultCode.ValidateError.getMessage());
//        }
        //以下调用接口
        return infoToId(id, null, map, null, infoByAppId.getAppId(), model.getInvokType(), null, null);
    }


    @Override
    public DataInterfaceActionModel checkParams(Map<String, String> map) {
        String ymDate = ServletUtil.getRequest().getHeader(InterfaceUtil.YMDATE);
        String authorSignature = ServletUtil.getRequest().getHeader(Constants.AUTHORIZATION);
        if (StringUtils.isEmpty(ymDate)) {
            throw new RuntimeException("header参数：YmDate未传值");
        }
        if (StringUtils.isEmpty(authorSignature)) {
            throw new RuntimeException("header参数：" + Constants.AUTHORIZATION + "未传值");
        }
        DataInterfaceActionModel entity = new DataInterfaceActionModel();
        //判断是否多租户，取参数tenantId
        if (InterfaceUtil.checkParam(map, "tenantId")) {
            entity.setTenantId(map.get("tenantId"));
        }
        entity.setMap(map);
        return entity;
    }

    @Override
    public Object preview(String id) {
        return infoToId(id);
    }

    /**
     * 执行SQL
     *
     * @param entity
     * @param sqlType
     * @param map
     * @return
     * @throws DataException
     */
    private List<Map<String, Object>> executeSql(DataInterfaceEntity entity, String sqlType, Map<String, String> map, Pagination pagination, Map<String,Object> showMap) throws Exception {
        DataSourceUtil linkEntity = dblinkService.getInfo(entity.getDbLinkId());
        String sql = entity.getQuerySql();;
        if (entity.getCheckType() != null && entity.getCheckType() == 1) {
            if (StringUtil.isNotEmpty(entity.getPropertyJson())) {
                DataInterfacePageModel dataInterfacePageModel = JsonUtil.getJsonToBean(entity.getPropertyJson(), DataInterfacePageModel.class);
                if ("1".equals(sqlType)) {
                    sql = dataInterfacePageModel.getCountSql();
                } else if ("2".equals(sqlType)) {
                    sql = dataInterfacePageModel.getEchoSql();
                } else {

                }
            }
        }
        UserInfo userInfo = userProvider.get();
        if(linkEntity == null){
            linkEntity = dataSourceUtils;
        }
        // 系统内置参数替换
        Map<Double, DataInterfaceMarkModel> systemParameter = systemParameter(sql, userInfo, pagination, showMap);
        // 自定义参数替换
        if (StringUtil.isNotEmpty(sql) && Objects.nonNull(map)) {
            for (String key : map.keySet()) {
                // 验证参数key对比
                List<DataInterfaceModel> jsonToList = JsonUtil.getJsonToList(entity.getRequestParameters(), DataInterfaceModel.class);
                DataInterfaceModel dataInterfaceModel = jsonToList.stream().filter(t -> key.equals(t.getField()) && "int".equals(t.getDataType())).findFirst().orElse(null);
                String tmpValue = map.get(key);
                if(tmpValue != null){
                    //参数前方 上个参数后方的语句中是否有 in
                    String sqlarr1 = sql.split("\\{" + key + "\\}")[0];
                    String[] sqlarr2 = sqlarr1.split("\\}");
                    String sql1 = sqlarr2.length>1?sqlarr2[sqlarr2.length-1]:sqlarr2[0];
                    boolean isInSql = sql1.toLowerCase().contains(" in ");
                    List<String> valueList;
                    if(isInSql){
                        valueList = Arrays.asList(tmpValue.split(","));
                    }else{
                        valueList = Arrays.asList(new String[]{tmpValue});
                    }
                    String placeholder = "?";
                    for (int i = 1; i < valueList.size(); i++) {
                        placeholder += ",?";
                    }
                    String finalSql = sql;
                    if (dataInterfaceModel != null) {
                        valueList.forEach(t -> {
                            DataInterfaceParamUtil.getParamModel(systemParameter, finalSql, "{" + key + "}", StringUtil.isNotEmpty(tmpValue) ? Integer.valueOf(t) : 0);
                        });
                    } else {
                        valueList.forEach(t -> {
                            DataInterfaceParamUtil.getParamModel(systemParameter, finalSql, "{" + key + "}", t);
                        });
                    }
                    sql = sql.replaceAll("\\{\\s?" + key + "\\s?}", placeholder);

                }
            }
        }
        // 处理SQL
        List<Object> values = new ArrayList<>(systemParameter.size());
        sql = getHandleArraysSql(sql, values, systemParameter);
        if (showMap != null) {
            sql = sql.replace(DataInterfaceVarEnum.SHOWKEY.getCondition(), showMap.keySet().iterator().next());
        }
        log.info("当前执行SQL：" + sql);
        if (entity.getCheckType() == 1 && (sql.contains(";") && sql.trim().indexOf(";") != sql.trim().length() - 1))  {
            return null;
        }
        if ("2".equals(entity.getRequestMethod()) || "1".equals(entity.getRequestMethod()) || "4".equals(entity.getRequestMethod())) {
            JdbcUtil.creUpDe(new PrepSqlDTO(sql, values).withConn(linkEntity, null));
            return null;
        }
        return JdbcUtil.queryList(new PrepSqlDTO(sql, values).withConn(linkEntity, null)).setIsAlias(true).get();
    }

    private String getHandleArraysSql(String sql, List<Object> values, Map<Double, DataInterfaceMarkModel> systemParameter) {
        if (StringUtil.isNotEmpty(sql)) {
            for (Double aDouble : systemParameter.keySet()) {
                Object value = systemParameter.get(aDouble).getValue();
                values.add(value);
            }
            for (Double aDouble : systemParameter.keySet()) {
                DataInterfaceMarkModel dataInterfaceMarkModel = systemParameter.get(aDouble);
                if (DataInterfaceVarEnum.ORGANDSUB.getCondition().equals(dataInterfaceMarkModel.getMarkName())) {
                    if (dataInterfaceMarkModel.getValue() instanceof List) {
                        List<Object> list = (List) dataInterfaceMarkModel.getValue();
                        String placeholder = "?";
                        int index = 0;
                        boolean addOrSet = false;
                        for (Object obj: list) {
                            placeholder += ",?";
                            if (!addOrSet) {
                                // 得到下标
                                int i = values.indexOf(dataInterfaceMarkModel.getValue());
                                values.set(i, obj);
                                addOrSet = true;
                                index = i++;
                            } else {
                                values.add(index, obj);
                            }
                        }
                        sql = sql.replaceAll(DataInterfaceVarEnum.ORGANDSUB.getCondition(), placeholder);
                    }
                }
                if (DataInterfaceVarEnum.USERANDSUB.getCondition().equals(dataInterfaceMarkModel.getMarkName())) {
                    if (dataInterfaceMarkModel.getValue() instanceof List) {
                        List<Object> list = (List) dataInterfaceMarkModel.getValue();
                        String placeholder = "?";
                        int index = 0;
                        boolean addOrSet = false;
                        for (Object obj: list) {
                            placeholder += ",?";
                            if (!addOrSet) {
                                // 得到下标
                                int i = values.indexOf(dataInterfaceMarkModel.getValue());
                                values.set(i, obj);
                                addOrSet = true;
                                index = i++;
                            } else {
                                values.add(index, obj);
                            }
                        }
                        sql = sql.replaceAll(DataInterfaceVarEnum.USERANDSUB.getCondition(), placeholder);
                    }
                }
                if (DataInterfaceVarEnum.CHARORG.getCondition().equals(dataInterfaceMarkModel.getMarkName())) {
                    if (dataInterfaceMarkModel.getValue() instanceof List) {
                        List<Object> list = (List) dataInterfaceMarkModel.getValue();
                        String placeholder = "?";
                        int index = 0;
                        boolean addOrSet = false;
                        for (Object obj: list) {
                            placeholder += ",?";
                            if (!addOrSet) {
                                // 得到下标
                                int i = values.indexOf(dataInterfaceMarkModel.getValue());
                                values.set(i, obj);
                                addOrSet = true;
                                index = i++;
                            } else {
                                values.add(index, obj);
                            }
                        }
                        sql = sql.replaceAll(DataInterfaceVarEnum.CHARORG.getCondition(), placeholder);
                    }
                }
                if (DataInterfaceVarEnum.CHARORGANDSUB.getCondition().equals(dataInterfaceMarkModel.getMarkName())) {
                    if (dataInterfaceMarkModel.getValue() instanceof List) {
                        List<Object> list = (List) dataInterfaceMarkModel.getValue();
                        String placeholder = "?";
                        int index = 0;
                        boolean addOrSet = false;
                        for (Object obj: list) {
                            placeholder += ",?";
                            if (!addOrSet) {
                                // 得到下标
                                int i = values.indexOf(dataInterfaceMarkModel.getValue());
                                values.set(i, obj);
                                addOrSet = true;
                                index = i++;
                            } else {
                                values.add(index, obj);
                            }
                        }
                        sql = sql.replaceAll(DataInterfaceVarEnum.CHARORGANDSUB.getCondition(), placeholder);
                    }
                }
            }
            sql = sql.replaceAll(DataInterfaceVarEnum.USER.getCondition(), "?");
            sql = sql.replaceAll(DataInterfaceVarEnum.ORG.getCondition(), "?");
            sql = sql.replaceAll(DataInterfaceVarEnum.KEYWORD.getCondition(), "?");
            sql = sql.replaceAll(DataInterfaceVarEnum.OFFSETSIZE.getCondition(), "?");
            sql = sql.replaceAll(DataInterfaceVarEnum.PAGESIZE.getCondition(), "?");
//            sql = sql.replaceAll(DataInterfaceVarEnum.SHOWKEY.getCondition(), "?");
            sql = sql.replaceAll(DataInterfaceVarEnum.SHOWVALUE.getCondition(), "?");
        }
        return sql;
    }

    /**
     * HTTP调用
     *
     * @param entity
     * @return get
     */
    private JSONObject callHTTP(DataInterfaceEntity entity, Map<String, String> map, String token, Pagination pagination, Map<String, Object> showMap) throws UnsupportedEncodingException {
        JSONObject get = new JSONObject();
        String path = entity.getPath();
        DataInterfacePageModel pageModel = JsonUtil.getJsonToBean(entity.getPropertyJson(), DataInterfacePageModel.class);
        // 请求方法
        String requestMethod = entity.getRequestMethod();
        // 获取请求头参数
        String requestHeaders = entity.getRequestHeaders();
        // 自定义参数
        String requestParameters = entity.getRequestParameters();
        if (showMap != null) {
            path = pageModel.getEchoPath();
            requestMethod = pageModel.getEchoReqMethod();
            requestHeaders = JsonUtil.getObjectToString(pageModel.getEchoReqHeaders());
            requestParameters = JsonUtil.getObjectToString(pageModel.getEchoReqParameters());
        }
        // Post请求拼接参数
        JSONObject jsonObject = new JSONObject();
        //判断是否为http或https
        if (StringUtil.isNotEmpty(path) && path.startsWith("http")) {
            //请求参数解析
            List<DataInterfaceModel> jsonToListMap = JsonUtil.getJsonToList(requestParameters, DataInterfaceModel.class);
            if (jsonToListMap != null) {
                // 判断是否为get，get从url上拼接
                if ("6".equals(requestMethod)) {
                    path += !path.contains("?") ? "?" : "&";
                    if (Objects.nonNull(map)) {
                        for (String field : map.keySet()) {
                            String value = String.valueOf(map.get(field));
                            path = path + field + "=" + URLEncoder.encode(value.replaceAll("'", ""), "UTF-8") + "&";
                        }
                    } else {
                        path = parameterHandler(path, jsonToListMap);
                    }
                    requestMethod = "GET";
                } else {
                    // 判断是否使用默认值
                    if (Objects.nonNull(map)) {
                        for (String field : map.keySet()) {
                            String value = String.valueOf(map.get(field));
                            jsonObject.put(field, value);
                        }
                    } else {
                        for (DataInterfaceModel model : jsonToListMap) {
                            if (Objects.nonNull(model)) {
                                String field = String.valueOf(model.getField());
                                String value = String.valueOf(model.getDefaultValue());
                                jsonObject.put(field, value);
                            }
                        }
                    }
                    requestMethod = "POST";
                }
            }
            // 分页参数
            if (pagination != null) {
                List<PageParamModel> pageParameters = pageModel.getPageParameters();
                for (PageParamModel pageParameter : pageParameters) {
                    if ("6".equals(entity.getRequestMethod())) {
                        path += !path.contains("?") ? "?" : "";
                        if (pageParameter.getFieldName().equals(DataInterfaceVarEnum.PAGESIZE.getCondition().replace("@", ""))) {
                            path += pageParameter.getField() + "=" + pagination.getPageSize() + "&";
                        }
                        if (pageParameter.getFieldName().equals(DataInterfaceVarEnum.KEYWORD.getCondition().replace("@", ""))) {
                            path += pageParameter.getField() + "=" + URLEncoder.encode(pagination.getKeyword(), "UTF-8") + "&";
                        }
                        if (pageParameter.getFieldName().equals(DataInterfaceVarEnum.CURRENTPAGE.getCondition().replace("@", ""))) {
                            path += pageParameter.getField() + "=" + pagination.getCurrentPage() + "&";
                        }
                    } else {
                        if (pageParameter.getFieldName().equals(DataInterfaceVarEnum.PAGESIZE.getCondition().replace("@", ""))) {
                            jsonObject.put(pageParameter.getField(), pagination.getPageSize());
                        }
                        if (pageParameter.getFieldName().equals(DataInterfaceVarEnum.KEYWORD.getCondition().replace("@", ""))) {
                            jsonObject.put(pageParameter.getField(), pagination.getKeyword());
                        }
                        if (pageParameter.getFieldName().equals(DataInterfaceVarEnum.CURRENTPAGE.getCondition().replace("@", ""))) {
                            jsonObject.put(pageParameter.getField(), pagination.getCurrentPage());
                        }
                    }
                }
            }
            // 回显参数
            if (showMap != null) {
                // 回显参数
                List<PageParamModel> parameters = pageModel.getEchoParameters();
                String showKey = null;
                Object showValue = null;
                if (showMap.size() > 0) {
                    showKey = showMap.keySet().iterator().next();
                    showValue = showMap.values().iterator().next();
                }
                if ("6".equals(entity.getRequestMethod())) {
                    PageParamModel pageParamModel = parameters.stream().filter(t -> t.getFieldName().equals(DataInterfaceVarEnum.SHOWKEY.getCondition().replace("@", ""))).findFirst().orElse(new PageParamModel());
                    if (path.contains("{" + pageParamModel.getField() + "}") && path.indexOf("{" + pageParamModel.getField() + "}") < path.lastIndexOf("?")) {
                        path = path.replace("{" + pageParamModel.getField() + "}", showValue != null ? String.valueOf(showValue) : "");
                    }
                    else {
                        path += pageParamModel.getField() + "=" + showValue;
                    }
                } else {
                    jsonObject.put(showKey, showValue);
                }
            }
            //获取token
            if (StringUtil.isEmpty(token)) {
                token = EapUserProvider.getToken();
            }
            String jsonObjects = jsonObject.size() > 0 ? jsonObject.toJSONString() : null;
            // 请求头
            if (StringUtil.isNotEmpty(requestHeaders)) {
                List<Map<String, Object>> requestHeader = JsonUtil.getJsonToListMap(requestHeaders);
                for (Map<String, Object> maps : requestHeader) {
                    if (Objects.nonNull(maps)) {
                        String field = String.valueOf(maps.get("field"));
                        String value = String.valueOf(maps.get("defaultValue"));
                        jsonObject.put(field, value);
                    }
                }
            }
            get = HttpUtil.httpRequest(path, requestMethod, jsonObjects, token, jsonObject.size() > 0 ? JsonUtil.getObjectToString(jsonObject) : null);
            return get;
        } else {
            get.put("errorCode", "1");
            return get;
        }
    }

    /**
     * 处理参数
     *
     * @param path
     * @param jsonToListMap
     * @return
     */
    private String parameterHandler(String path, List<DataInterfaceModel> jsonToListMap) {
        for (DataInterfaceModel model : jsonToListMap) {
            if (model != null) {
                String field = String.valueOf(model.getField());
                String defaultValue = String.valueOf(model.getDefaultValue());
                try {
                    defaultValue = URLEncoder.encode(defaultValue, "UTF-8");
                    path = path + field + "=" + defaultValue + "&";
                } catch (UnsupportedEncodingException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return path;
    }

    /**
     * 处理sql参数
     *
     * @param sql
     * @return
     */
    private Map<Double, DataInterfaceMarkModel> systemParameter(String sql, UserInfo userInfo, Pagination pagination, Map<String,Object> showMap) {
        Map<Double, DataInterfaceMarkModel> paramValue = new TreeMap<>();
        //当前用户
        if (sql.contains(DataInterfaceVarEnum.USER.getCondition()) && StringUtil.isNotEmpty(userInfo.getUserId())) {
            String userId = userInfo.getUserId();
            DataInterfaceParamUtil.getParamModel(paramValue, sql, DataInterfaceVarEnum.USER.getCondition(), userId);
        }
        //当前组织及子组织
        if (sql.contains(DataInterfaceVarEnum.ORGANDSUB.getCondition())) {
            String orgId = userInfo.getOrganizeId();
            if (StringUtil.isNotEmpty(userInfo.getDepartmentId())) {
                orgId = userInfo.getDepartmentId();
            }
            List<String> underOrganizations = organizeService.getUnderOrganizations(orgId);
            underOrganizations.add(orgId);
            DataInterfaceParamUtil.getParamModel(paramValue, sql, DataInterfaceVarEnum.ORGANDSUB.getCondition(), underOrganizations);
        }
        //当前用户及下属
        if (sql.contains(DataInterfaceVarEnum.USERANDSUB.getCondition()) && StringUtil.isNotEmpty(userInfo.getSubordinateIds())) {
            List<String> subOrganizeIds = new ArrayList<>();
            if (userInfo.getSubordinateIds().size() > 0) {
                subOrganizeIds = userInfo.getSubordinateIds();
            }
            subOrganizeIds.add(userInfo.getUserId());
            DataInterfaceParamUtil.getParamModel(paramValue, sql, DataInterfaceVarEnum.USERANDSUB.getCondition(), subOrganizeIds);
        }
        //当前分管组织
        if (sql.contains(DataInterfaceVarEnum.CHARORG.getCondition()) && StringUtil.isNotEmpty(userInfo.getUserId())) {
            List<OrganizeAdministratorEntity> organizeAdministratorEntity = organizeAdminIsTratorService.getListByUserID(userInfo.getUserId());
            //子
            List<OrganizeAdministratorEntity> organizeAdministratorEntity1 = new ArrayList<>(organizeAdministratorEntity);
            //父
            List<OrganizeAdministratorEntity> organizeAdministratorEntity2 = new ArrayList<>(organizeAdministratorEntity);

            List<String> allIdList = new ArrayList<>();

            //子
            List<String> childList = organizeAdministratorEntity1.stream().filter(orgAdmin -> orgAdmin.getSubLayerSelect() == 1).map(orgAdmin -> orgAdmin.getOrganizeId()).collect(Collectors.toList());
            //父
            List<String> fathetList = organizeAdministratorEntity2.stream().filter(orgAdmin -> orgAdmin.getThisLayerSelect() == 1).map(orgAdmin -> orgAdmin.getOrganizeId()).collect(Collectors.toList());

            for (String org : childList) {
                List<String> underOrganizations = organizeService.getUnderOrganizations(org);
                if (underOrganizations.size() > 0) {
                    allIdList.addAll(underOrganizations);
                }
            }

            if (fathetList.size() > 0) {
                allIdList.addAll(fathetList);
            }
            DataInterfaceParamUtil.getParamModel(paramValue, sql, DataInterfaceVarEnum.CHARORG.getCondition(), allIdList);
        }
        //当前组织
        if (sql.contains(DataInterfaceVarEnum.ORG.getCondition())) {
            String orgId = userInfo.getOrganizeId();
            if (StringUtil.isNotEmpty(userInfo.getDepartmentId())) {
                orgId = userInfo.getDepartmentId();
            }
            DataInterfaceParamUtil.getParamModel(paramValue, sql, DataInterfaceVarEnum.ORG.getCondition(), orgId);
        }
        //当前分管组织及子组织
        if (sql.contains(DataInterfaceVarEnum.CHARORGANDSUB.getCondition()) && StringUtil.isNotEmpty(userInfo.getUserId())) {
            List<OrganizeAdministratorEntity> organizeAdministratorEntity = organizeAdminIsTratorService.getListByUserID(userInfo.getUserId());

            List<OrganizeAdministratorEntity> organizeAdministratorEntity1 = new ArrayList<>(organizeAdministratorEntity);

            List<OrganizeAdministratorEntity> organizeAdministratorEntity2 = new ArrayList<>(organizeAdministratorEntity);

            List<String> allIdList = new ArrayList<>();
            //需要子集
            List<String> childList = new ArrayList<>();

            List<String> thisList = organizeAdministratorEntity1.stream().filter(orgAdmin -> orgAdmin.getThisLayerSelect() == 1)
                    .map(orgAdmin -> orgAdmin.getOrganizeId()).collect(Collectors.toList());

            List<String> subList = organizeAdministratorEntity2.stream().filter(orgAdmin -> orgAdmin.getSubLayerSelect() == 1)
                    .map(orgAdmin -> orgAdmin.getOrganizeId()).collect(Collectors.toList());

            if (thisList.size() > 0) {
                allIdList.addAll(thisList);
                childList.addAll(thisList);
            }
            if (subList.size() > 0) {
                childList.addAll(subList);
            }

            for (String orgID : childList) {
                List<String> underOrganizations = organizeService.getUnderOrganizations(orgID);
                if (underOrganizations.size() > 0) {
                    allIdList.addAll(underOrganizations);
                }
            }
            List<String> orgAndSub = allIdList.stream().distinct().collect(Collectors.toList());
            DataInterfaceParamUtil.getParamModel(paramValue, sql, DataInterfaceVarEnum.CHARORGANDSUB.getCondition(), orgAndSub);
        }
        //关键字
        if (sql.contains(DataInterfaceVarEnum.KEYWORD.getCondition())) {
            DataInterfaceParamUtil.getParamModel(paramValue, sql, DataInterfaceVarEnum.KEYWORD.getCondition(), pagination.getKeyword());
        }
        // 当前页数
        if (sql.contains(DataInterfaceVarEnum.OFFSETSIZE.getCondition())) {
            DataInterfaceParamUtil.getParamModel(paramValue, sql, DataInterfaceVarEnum.OFFSETSIZE.getCondition(), pagination.getPageSize()*(pagination.getCurrentPage() - 1));
        }
        // 每页行数
        if (sql.contains(DataInterfaceVarEnum.PAGESIZE.getCondition())) {
            DataInterfaceParamUtil.getParamModel(paramValue, sql, DataInterfaceVarEnum.PAGESIZE.getCondition(), pagination.getPageSize());
        }
//        // 每页行数
//        if (sql.contains(DataInterfaceVarEnum.SHOWKEY.getCondition())) {
//            DataInterfaceParamUtil.getParamModel(paramValue, sql, DataInterfaceVarEnum.SHOWKEY.getCondition(), showMap.keySet().iterator().next());
//        }
        // 每页行数
        if (sql.contains(DataInterfaceVarEnum.SHOWVALUE.getCondition())) {
            DataInterfaceParamUtil.getParamModel(paramValue, sql, DataInterfaceVarEnum.SHOWVALUE.getCondition(), showMap.values().iterator().next());
        }
        return paramValue;
    }

    /**
     * 判断如果是内部接口的话直接返回
     *
     * @param jsonObject
     * @return
     */
    private ActionResult returnAction(JSONObject jsonObject) {
        if (jsonObject != null) {
            if (jsonObject.get("data") != null) {
                return ActionResult.success(jsonObject.get("data") != null ? jsonObject.get("data") : new ArrayList<>());
            }
        }
        return ActionResult.success(jsonObject != null ? jsonObject : new ArrayList<>());
    }

    /**
     * 判断是不是内部接口
     *
     * @param jsonObject
     * @return
     */
    private Boolean isInternal(JSONObject jsonObject) {
        if (jsonObject != null) {
            if (jsonObject.size() == 3 && jsonObject.get("code") != null && jsonObject.get("msg") != null && jsonObject.get("data") != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * 计算执行时间
     *
     * @param dateTime
     * @return
     */
    public int invokTime(LocalDateTime dateTime) {
        //调用时间
        int invokWasteTime = Integer.valueOf((int) (System.currentTimeMillis() - dateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli()));
        return invokWasteTime;
    }

}
