


package org.openea.eap.extj.message.controller.admin.message;

//import cn.dev33.satoken.annotation.SaCheckPermission;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.base.controller.SuperController;
import org.openea.eap.extj.base.util.TestSendConfigUtil;
import org.openea.eap.extj.base.vo.DownloadVO;
import org.openea.eap.extj.base.vo.PageListVO;
import org.openea.eap.extj.base.vo.PaginationVO;
import org.openea.eap.extj.config.ConfigValueUtil;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.extend.service.BillRuleService;
import org.openea.eap.extj.message.entity.*;
import org.openea.eap.extj.message.model.messagetemplateconfig.TemplateParamModel;
import org.openea.eap.extj.message.model.sendmessageconfig.*;
import org.openea.eap.extj.message.service.*;
import org.openea.eap.extj.permission.entity.UserEntity;
import org.openea.eap.extj.permission.service.UserService;
import org.openea.eap.extj.util.*;
import org.openea.eap.extj.util.enums.ModuleTypeEnum;
import org.openea.eap.extj.util.file.FileExport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 消息发送配置
 *
 *
 */
@Slf4j
@RestController
@Tag(name = "消息发送配置", description = "message")
@RequestMapping("/api/message/SendMessageConfig")
public class SendMessageConfigController extends SuperController<SendMessageConfigService, SendMessageConfigEntity> {
    @Autowired
    private FileExport fileExport;

    @Autowired
    private BillRuleService billRuleService;

    @Autowired
    private ConfigValueUtil configValueUtil;
    
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private UserService userService;


    @Autowired
    private SendMessageConfigService sendMessageConfigService;

    @Autowired
    private SendConfigTemplateService sendConfigTemplateService;

    @Autowired
    private AccountConfigService accountConfigService;

    @Autowired
    private MessageTemplateConfigService messageTemplateConfigService;

    @Autowired
    private MessageDataTypeService messageDataTypeService;

    @Autowired
    private TestSendConfigUtil testSendConfigUtil;

    /**
     * 列表
     *
     * @param sendMessageConfigPagination 分页模型
     * @return
     */
    @Operation(summary = "消息发送配置列表")
    //@SaCheckPermission("msgCenter.sendConfig")
    @GetMapping
    public ActionResult<PageListVO<SendMessageConfigListVO>> list(SendMessageConfigPagination sendMessageConfigPagination) throws IOException {
        List<SendMessageConfigEntity> list = sendMessageConfigService.getList(sendMessageConfigPagination);
        //处理id字段转名称，若无需转或者为空可删除
        UserEntity userEntity = new UserEntity();
        List<SendMessageConfigListVO> listVO = JsonUtil.getJsonToList(list, SendMessageConfigListVO.class);
        for (SendMessageConfigListVO sendMessageConfigVO : listVO) {
            List<Map<String,String>> mapList = new ArrayList<>();
            //子表数据转换
            List<SendConfigTemplateEntity> sendConfigTemplateList = sendConfigTemplateService.getDetailListByParentId(sendMessageConfigVO.getId());
            if (sendConfigTemplateList != null && sendConfigTemplateList.size()>0) {
                sendConfigTemplateList = sendConfigTemplateList.stream().sorted((a,b)->a.getMessageType().compareTo(b.getMessageType())).collect(Collectors.toList());
                List<String> typeList = sendConfigTemplateList.stream().map(t -> t.getMessageType()).distinct().collect(Collectors.toList());
                if (typeList != null && typeList.size()>0) {
                    for(String type : typeList) {
                        String messageType = "";
                        Map<String,String> map = new HashMap<>();
                        MessageDataTypeEntity dataTypeEntity = messageDataTypeService.getInfo("1", type);
                        if (dataTypeEntity != null) {
                            messageType = dataTypeEntity.getFullName();
                            map.put("fullName",messageType);
                            map.put("type",type);
                            mapList.add(map);
                        }
                    }
                    sendMessageConfigVO.setMessageType(mapList);
                }
            }
            if(StringUtil.isNotBlank(sendMessageConfigVO.getCreatorUserId()) && !"null".equals(sendMessageConfigVO.getCreatorUserId())){
                userEntity = userService.getInfo(sendMessageConfigVO.getCreatorUserId());
                if(userEntity != null){
                    sendMessageConfigVO.setCreatorUser(userEntity.getRealName() +"/"+ userEntity.getAccount());
                }
            }
            //消息来源
            if(StringUtil.isNotBlank(sendMessageConfigVO.getMessageSource())) {
                MessageDataTypeEntity dataTypeEntity1 = messageDataTypeService.getInfo("4", sendMessageConfigVO.getMessageSource());
                if (dataTypeEntity1 != null) {
                    sendMessageConfigVO.setMessageSource(dataTypeEntity1.getFullName());
                }
            }
        }

        PageListVO vo = new PageListVO();
        vo.setList(listVO);
        PaginationVO page = JsonUtil.getJsonToBean(sendMessageConfigPagination, PaginationVO.class);
        vo.setPagination(page);
        return ActionResult.success(vo);

    }

    /**
     * 获取发送配置下拉框
     *
     * @return
     */
    @Operation(summary = "获取发送配置下拉框")
    @GetMapping("/Selector")
    public ActionResult<PageListVO<SendMessageConfigListVO>> selector(SendMessageConfigPagination sendMessageConfigPagination) {
        List<SendMessageConfigEntity> list = sendMessageConfigService.getSelectorList(sendMessageConfigPagination);
        List<SendMessageConfigListVO> listVO = JsonUtil.getJsonToList(list, SendMessageConfigListVO.class);
        PageListVO vo = new PageListVO();
        vo.setList(listVO);
        PaginationVO page = JsonUtil.getJsonToBean(sendMessageConfigPagination, PaginationVO.class);
        vo.setPagination(page);
        return ActionResult.success(vo);
    }

    /**
     * 消息发送配置弹窗列表
     *
     * @param sendMessageConfigPagination 分页模型
     * @return
     */
    @Operation(summary = "消息发送配置弹窗列表")
    @GetMapping("/getSendConfigList")
    public ActionResult<PageListVO<SendConfigListVO>> getSendConfigList(SendMessageConfigPagination sendMessageConfigPagination) throws IOException {
        if(StringUtil.isBlank(sendMessageConfigPagination.getEnabledMark())) {
            sendMessageConfigPagination.setEnabledMark("1");
        }
        if(StringUtil.isBlank(sendMessageConfigPagination.getTemplateType())){
            sendMessageConfigPagination.setTemplateType("0");
        }
        List<SendMessageConfigEntity> list = sendMessageConfigService.getList(sendMessageConfigPagination);
        //处理id字段转名称，若无需转或者为空可删除

        List<SendConfigListVO> listVO = JsonUtil.getJsonToList(list, SendConfigListVO.class);
        for (SendConfigListVO sendConfigVO : listVO) {
            //子表数据转换
            List<SendConfigTemplateEntity> sendConfigTemplateList = sendConfigTemplateService.getDetailListByParentId(sendConfigVO.getId());
            sendConfigTemplateList = sendConfigTemplateList.stream().filter(t->"1".equals(String.valueOf(t.getEnabledMark()))).collect(Collectors.toList());
            List<SendConfigTemplateModel> modelList = JsonUtil.getJsonToList(sendConfigTemplateList, SendConfigTemplateModel.class);
            for(SendConfigTemplateModel model:modelList) {
                if (modelList != null && modelList.size() > 0) {
                    List<TemplateParamModel> list1 = messageTemplateConfigService.getParamJson(model.getTemplateId());
//                    if (list != null && list.size() > 0) {
//                        model.setParamJson(JsonUtil.getObjectToString(list1));
//                    }
                    List<MsgTemplateJsonModel> jsonModels = new ArrayList<>();
                    for(TemplateParamModel paramModel : list1){
                        MsgTemplateJsonModel jsonModel = new MsgTemplateJsonModel();
                        jsonModel.setField(paramModel.getField());
                        jsonModel.setFieldName(paramModel.getFieldName());
                        jsonModel.setMsgTemplateId(model.getId());
                        jsonModels.add(jsonModel);
                    }
                    model.setParamJson(jsonModels);
                    MessageTemplateConfigEntity msgTemEntity = messageTemplateConfigService.getInfo(model.getTemplateId());
                    if (msgTemEntity != null) {
                        model.setMsgTemplateName(msgTemEntity.getFullName());
                    }
                    if (StringUtil.isNotBlank(model.getMessageType())) {
                        MessageDataTypeEntity dataTypeEntity = messageDataTypeService.getInfo("1", model.getMessageType());
                        if (dataTypeEntity != null) {
                            model.setMessageType(dataTypeEntity.getFullName());
                        }
                    }
                }
//            List<TemplateParamModel> paramModelList = new ArrayList<>();
//            List<SendConfigTemplateEntity> sendConfigTemplateList = sendConfigTemplateService.getDetailListByParentId(sendConfigVO.getId());
//            if (sendConfigTemplateList != null && sendConfigTemplateList.size()>0) {
//                for(SendConfigTemplateEntity entity : sendConfigTemplateList){
//                    List<TemplateParamModel> modelList = messageTemplateConfigService.getParamJson(entity.getTemplateId());
//                    if(modelList!=null && modelList.size()>0){
//                       paramModelList.addAll(modelList);
//                    }
//                }
//            }
//                List<String> list1 = JsonUtil.getJsonToList(paramModelList,String.class);
                sendConfigVO.setTemplateJson(modelList);
            }
        }

        PageListVO vo = new PageListVO();
        vo.setList(listVO);
        PaginationVO page = JsonUtil.getJsonToBean(sendMessageConfigPagination, PaginationVO.class);
        vo.setPagination(page);
        return ActionResult.success(vo);
    }

    /**
     * 创建
     *
     * @param sendMessageConfigForm 发送消息配置模型
     * @return
     */
    @Operation(summary = "创建")
    @Parameters({
            @Parameter(name = "sendMessageConfigForm", description = "发送消息配置模型", required = true)
    })
    //@SaCheckPermission("msgCenter.sendConfig")
    @PostMapping
    @Transactional
    public ActionResult create(@RequestBody @Valid SendMessageConfigForm sendMessageConfigForm) throws DataException {
        boolean b = sendMessageConfigService.checkForm(sendMessageConfigForm, 0,"");
        if (b) {
            return ActionResult.fail("编码不能重复");
        }
        if(!"1".equals(sendMessageConfigForm.getTemplateType())){
            if(sendMessageConfigForm.getEnCode().contains("PZXT")){
                return ActionResult.fail("自定义模板编码不能使用系统模板编码规则");
            }
        }
        String mainId = RandomUtil.uuId();
        UserInfo userInfo = userProvider.get();
        SendMessageConfigEntity entity = JsonUtil.getJsonToBean(sendMessageConfigForm, SendMessageConfigEntity.class);
        entity.setCreatorUserId(userInfo.getUserId());
        entity.setCreatorTime(DateUtil.getNowDate());
        entity.setId(mainId);
        sendMessageConfigService.save(entity);
        if (sendMessageConfigForm.getSendConfigTemplateList() != null) {
            List<SendConfigTemplateEntity> SendConfigTemplateList = JsonUtil.getJsonToList(sendMessageConfigForm.getSendConfigTemplateList(), SendConfigTemplateEntity.class);
            for (SendConfigTemplateEntity entitys : SendConfigTemplateList) {
                entitys.setId(RandomUtil.uuId());
                entitys.setSendConfigId(entity.getId());
                sendConfigTemplateService.save(entitys);
            }
        }

        return ActionResult.success("创建成功");
    }


    /**
     * 信息
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "信息")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @GetMapping("/{id}")
    public ActionResult<SendMessageConfigInfoVO> info(@PathVariable("id") String id) {
        SendMessageConfigEntity entity = sendMessageConfigService.getInfo(id);
        SendMessageConfigInfoVO vo = JsonUtil.getJsonToBean(entity, SendMessageConfigInfoVO.class);
        MessageDataTypeEntity dataTypeEntity = messageDataTypeService.getInfo("4",vo.getMessageSource());
        if(dataTypeEntity != null){
            vo.setMessageSourceName(dataTypeEntity.getFullName());
        }
        //子表
        List<SendConfigTemplateEntity> sendConfigTemplateList = sendMessageConfigService.getSendConfigTemplateList(id);
        for (SendConfigTemplateEntity sendconfigtemplateEntity : sendConfigTemplateList) {
            AccountConfigEntity accountConfigEntity = accountConfigService.getInfo(sendconfigtemplateEntity.getAccountConfigId());
            if(accountConfigEntity != null){
                sendconfigtemplateEntity.setAccountCode(accountConfigEntity.getEnCode());
                sendconfigtemplateEntity.setAccountName(accountConfigEntity.getFullName());
            }
            MessageTemplateConfigEntity messageTemplateConfigEntity = messageTemplateConfigService.getInfo(sendconfigtemplateEntity.getTemplateId());
            if(messageTemplateConfigEntity != null){
                sendconfigtemplateEntity.setTemplateCode(messageTemplateConfigEntity.getEnCode());
                sendconfigtemplateEntity.setTemplateName(messageTemplateConfigEntity.getFullName());
            }
        }
        vo.setSendConfigTemplateList(sendConfigTemplateList);
        //副表
        return ActionResult.success(vo);
    }

    /**
     * 根据编码获取信息
     *
     * @param enCode 编码
     * @return
     */
    @Operation(summary = "根据编码获取信息")
    @Parameters({
            @Parameter(name = "enCode", description = "编码", required = true)
    })
//    @SaCheckPermission("msgCenter.sendConfig")
    @GetMapping("/getInfoByEnCode/{enCode}")
    public ActionResult<SendMessageConfigInfoVO> getInfo(@PathVariable("enCode") String enCode) {
        SendMessageConfigEntity entity = sendMessageConfigService.getInfoByEnCode(enCode);
        SendMessageConfigInfoVO vo = JsonUtil.getJsonToBean(entity, SendMessageConfigInfoVO.class);
        //子表
        List<SendConfigTemplateEntity> sendConfigTemplateList = sendMessageConfigService.getSendConfigTemplateList(entity.getId());
        for (SendConfigTemplateEntity sendconfigtemplateEntity : sendConfigTemplateList) {
            AccountConfigEntity accountConfigEntity = accountConfigService.getInfo(sendconfigtemplateEntity.getAccountConfigId());
            if(accountConfigEntity != null){
                sendconfigtemplateEntity.setAccountCode(accountConfigEntity.getEnCode());
                sendconfigtemplateEntity.setAccountName(accountConfigEntity.getFullName());
            }
            MessageTemplateConfigEntity messageTemplateConfigEntity = messageTemplateConfigService.getInfo(sendconfigtemplateEntity.getTemplateId());
            if(messageTemplateConfigEntity != null){
                sendconfigtemplateEntity.setTemplateCode(messageTemplateConfigEntity.getEnCode());
                sendconfigtemplateEntity.setTemplateName(messageTemplateConfigEntity.getFullName());
            }
        }
        vo.setSendConfigTemplateList(sendConfigTemplateList);
        //副表
        return ActionResult.success(vo);
    }

    /**
     * 表单信息(详情页)
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "表单信息(详情页)")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    //@SaCheckPermission("msgCenter.sendConfig")
    @GetMapping("/detail/{id}")
    public ActionResult<SendMessageConfigInfoVO> detailInfo(@PathVariable("id") String id) {
        SendMessageConfigEntity entity = sendMessageConfigService.getInfo(id);
        SendMessageConfigInfoVO vo = JsonUtil.getJsonToBean(entity, SendMessageConfigInfoVO.class);

        //子表数据转换
        List<SendConfigTemplateEntity> sendConfigTemplateList = sendMessageConfigService.getSendConfigTemplateList(id);
        for(SendConfigTemplateEntity sendconfigtemplateEntity : sendConfigTemplateList){
            AccountConfigEntity accountConfigEntity = accountConfigService.getInfo(sendconfigtemplateEntity.getAccountConfigId());
            if(accountConfigEntity != null){
                sendconfigtemplateEntity.setAccountCode(accountConfigEntity.getEnCode());
                sendconfigtemplateEntity.setAccountName(accountConfigEntity.getFullName());
            }
            MessageTemplateConfigEntity messageTemplateConfigEntity = messageTemplateConfigService.getInfo(sendconfigtemplateEntity.getTemplateId());
            if(messageTemplateConfigEntity != null){
                sendconfigtemplateEntity.setTemplateCode(messageTemplateConfigEntity.getEnCode());
                sendconfigtemplateEntity.setTemplateName(messageTemplateConfigEntity.getFullName());
            }
        }
        vo.setSendConfigTemplateList(sendConfigTemplateList);
        return ActionResult.success(vo);
    }


    /**
     * 更新
     *
     * @param id 主键
     * @param sendMessageConfigForm 发送信息配置模型
     * @return
     */
    @Operation(summary = "更新")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "sendMessageConfigForm", description = "发送信息配置模型", required = true)
    })
    //@SaCheckPermission("msgCenter.sendConfig")
    @PutMapping("/{id}")
    @Transactional
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid SendMessageConfigForm sendMessageConfigForm) throws DataException {

        boolean b = sendMessageConfigService.checkForm(sendMessageConfigForm, 0,sendMessageConfigForm.getId());
        if (b) {
            return ActionResult.fail("编码不能重复");
        }
        if("0".equals(sendMessageConfigForm.getEnabledMark())){
            if(sendMessageConfigService.idUsed(id)){
                return ActionResult.fail("此记录与“流程通知”关联引用，不允许被禁用");
            }
        }
        if(!"1".equals(sendMessageConfigForm.getTemplateType())){
            if(sendMessageConfigForm.getEnCode().contains("PZXT")){
                return ActionResult.fail("自定义模板编码不能使用系统模板编码规则");
            }
        }
        UserInfo userInfo = userProvider.get();
        SendMessageConfigEntity entity = sendMessageConfigService.getInfo(id);
        if (entity != null) {
            SendMessageConfigEntity subentity = JsonUtil.getJsonToBean(sendMessageConfigForm, SendMessageConfigEntity.class);
            subentity.setCreatorUserId(entity.getCreatorUserId());
            subentity.setCreatorTime(entity.getCreatorTime());
            subentity.setLastModifyUserId(userInfo.getUserId());
            subentity.setLastModifyTime(DateUtil.getNowDate());
            boolean b1 = sendMessageConfigService.updateById(subentity);
            if (!b1) {
                return ActionResult.fail("当前表单原数据已被调整，请重新进入该页面编辑并提交数据");
            }

            //明细表数据更新
            List<SendConfigTemplateEntity> addTemplateList = new ArrayList<>();
            List<SendConfigTemplateEntity> updTemplateList = new ArrayList<>();
            List<SendConfigTemplateEntity> delTemplateList = new ArrayList<>();
            if (sendMessageConfigForm.getSendConfigTemplateList() != null) {
                List<SendConfigTemplateEntity> sendConfigTemplateEntityList = JsonUtil.getJsonToList(sendMessageConfigForm.getSendConfigTemplateList(), SendConfigTemplateEntity.class);
                for (SendConfigTemplateEntity entitys : sendConfigTemplateEntityList) {
                    SendConfigTemplateEntity templateEntity = sendConfigTemplateService.getInfo(entitys.getId());
                    if(templateEntity != null){
                        templateEntity.setSendConfigId(entity.getId());
                        templateEntity.setId(entitys.getId());
                        templateEntity.setEnabledMark(entitys.getEnabledMark());
                        templateEntity.setCreatorTime(entitys.getCreatorTime());
                        templateEntity.setCreatorUserId(entitys.getCreatorUserId());
                        templateEntity.setDescription(entitys.getDescription());
                        templateEntity.setAccountConfigId(entitys.getAccountConfigId());
                        templateEntity.setSortCode(entitys.getSortCode());
                        templateEntity.setLastModifyTime(DateUtil.getNowDate());
                        templateEntity.setLastModifyUserId(userInfo.getUserId());
                        templateEntity.setTemplateId(entitys.getTemplateId());
                        updTemplateList.add(templateEntity);
                    }else {
                        entitys.setId(RandomUtil.uuId());
                        entitys.setSendConfigId(entity.getId());
                        entitys.setCreatorUserId(userInfo.getUserId());
                        entitys.setCreatorTime(DateUtil.getNowDate());
                        addTemplateList.add(entitys);
                    }
                }
                //删除参数记录
                List<SendConfigTemplateEntity> paramEntityList = sendConfigTemplateService.getDetailListByParentId(entity.getId());
                if (paramEntityList != null) {
                    for (SendConfigTemplateEntity templateEntity : paramEntityList) {
                        SendConfigTemplateEntity templateEntity1 = sendConfigTemplateEntityList.stream().filter(t -> t.getId().equals(templateEntity.getId())).findFirst().orElse(null);
                        if (templateEntity1 == null) {
                            delTemplateList.add(templateEntity);
                        }
                    }
                }
                if (addTemplateList != null && addTemplateList.size() > 0) {
                    sendConfigTemplateService.saveBatch(addTemplateList);
                }
                if (updTemplateList != null && updTemplateList.size() > 0) {
                    sendConfigTemplateService.updateBatchById(updTemplateList);
                }
                if (delTemplateList != null && delTemplateList.size() > 0) {
                    sendConfigTemplateService.removeByIds(delTemplateList.stream().map(SendConfigTemplateEntity::getId).collect(Collectors.toList()));
                }
            }
            return ActionResult.success("更新成功");
        } else {
            return ActionResult.fail("更新失败，数据不存在");
        }
    }


    /**
     * 删除
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "删除")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    //@SaCheckPermission("msgCenter.sendConfig")
    @DeleteMapping("/{id}")
    @Transactional
    public ActionResult delete(@PathVariable("id") String id) {
        SendMessageConfigEntity entity = sendMessageConfigService.getInfo(id);
        if (entity != null) {
            if(sendMessageConfigService.idUsed(id)){
                return ActionResult.fail("删除失败，此记录与“流程通知”关联引用，不允许被删除");
            }
            sendMessageConfigService.delete(entity);
            QueryWrapper<SendConfigTemplateEntity> queryWrapperSendConfigTemplate = new QueryWrapper<>();
            queryWrapperSendConfigTemplate.lambda().eq(SendConfigTemplateEntity::getSendConfigId, entity.getId());
            sendConfigTemplateService.remove(queryWrapperSendConfigTemplate);

        }
        return ActionResult.success("删除成功");
    }

    /**
     * 获取消息发送配置
     *
     * @param id 发送配置id
     * @return
     */
    @Operation(summary = "获取消息发送配置")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    //@SaCheckPermission("msgCenter.sendConfig")
    @PostMapping("/getTestConfig/{id}")
    @Transactional
    public ActionResult getTestConfig(@PathVariable("id") String id){
        List<SendConfigTemplateEntity> configTemplateList = sendConfigTemplateService.getConfigTemplateListByConfigId(id);
        if(configTemplateList != null && configTemplateList.size()>0){
            List<SendConfigTemplateModel> modelList = JsonUtil.getJsonToList(configTemplateList, SendConfigTemplateModel.class);
            for(SendConfigTemplateModel model:modelList){
                List<TemplateParamModel> list = messageTemplateConfigService.getParamJson(model.getTemplateId());
                if(list!=null && list.size()>0){
                    model.setParamJson(list);
                }
                MessageTemplateConfigEntity msgTemEntity = messageTemplateConfigService.getInfo(model.getTemplateId());
                if(msgTemEntity != null){
                    model.setMsgTemplateName(msgTemEntity.getFullName());
                }
                if(StringUtil.isNotBlank(model.getMessageType())){
                    MessageDataTypeEntity dataTypeEntity = messageDataTypeService.getInfo("1",model.getMessageType());
                    if(dataTypeEntity != null){
                        model.setMessageType(dataTypeEntity.getFullName());
                    }
                }
            }
            return ActionResult.success(modelList);
        }else {
            return ActionResult.fail("配置模板无数据，无法测试");
        }
    }

    /**
     * 测试消息发送配置
     *
     * @param modelList 发送配置
     * @return
     */
    @Operation(summary = "测试消息发送配置")
    @Parameters({
            @Parameter(name = "modelList", description = "发送配置", required = true)
    })
    //@SaCheckPermission("msgCenter.sendConfig")
    @PostMapping("/testSendConfig")
    @Transactional
    public ActionResult testSendConfig(@RequestBody @Valid List<SendConfigTemplateModel> modelList)  throws NoSuchAlgorithmException, InvalidKeyException  {
        UserInfo userInfo = userProvider.get();
        List<SendConfigTestResultModel> resultList = new ArrayList<>();
        if(modelList != null && modelList.size()>0){
            for(SendConfigTemplateModel model: modelList){
                SendConfigTestResultModel resultModel = new SendConfigTestResultModel();
                String result = testSendConfigUtil.sendMessage(model,userInfo);
                MessageTemplateConfigEntity msgTemEntity = messageTemplateConfigService.getInfo(model.getTemplateId());
                if(msgTemEntity != null) {
                    MessageDataTypeEntity dataTypeEntity = messageDataTypeService.getInfo("1", msgTemEntity.getMessageType());
                    resultModel.setMessageType("消息类型：" + dataTypeEntity.getFullName());
                    resultModel.setResult(result);
                    if (result != null) {
                        resultModel.setIsSuccess("0");
                    } else {
                        resultModel.setIsSuccess("1");
                    }
                }
                resultList.add(resultModel);
            }
        }
        return ActionResult.success(resultList);
    }

    /**
     * 复制
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "复制")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    //@SaCheckPermission("msgCenter.sendConfig")
    @PostMapping("/copy/{id}")
    @Transactional
    public ActionResult copy(@PathVariable("id") String id) {
        UserInfo userInfo = userProvider.get();
        SendMessageConfigEntity entity = sendMessageConfigService.getInfo(id);
        if (entity != null) {
            entity.setEnabledMark(0);
            String copyNum = UUID.randomUUID().toString().substring(0, 5);
            entity.setFullName(entity.getFullName()+".副本"+copyNum);
            entity.setEnCode(entity.getEnCode()+copyNum);
            entity.setCreatorTime(DateUtil.getNowDate());
            entity.setCreatorUserId(userInfo.getUserId());
            entity.setLastModifyTime(null);
            entity.setLastModifyUserId(null);
            entity.setTemplateType("0");
            entity.setUsedId(null);
            entity.setId(RandomUtil.uuId());
            SendMessageConfigEntity copyEntity = JsonUtil.getJsonToBean(entity, SendMessageConfigEntity.class);
            if(copyEntity.getEnCode().length()>50 || copyEntity.getFullName().length()>50){
                return ActionResult.fail("已到达该模板复制上限，请复制源模板");
            }
            sendMessageConfigService.create(copyEntity);
            List<SendConfigTemplateEntity> copyConfigTemplateList = new ArrayList<>();
            List<SendConfigTemplateEntity> configTemplateList = sendConfigTemplateService.getDetailListByParentId(id);
            if(configTemplateList != null && configTemplateList.size()>0){
                for(SendConfigTemplateEntity entitys : configTemplateList){
                    entitys.setId(RandomUtil.uuId());
                    entitys.setSendConfigId(copyEntity.getId());
                    entitys.setCreatorTime(DateUtil.getNowDate());
                    entitys.setCreatorUserId(userInfo.getUserId());
                    copyConfigTemplateList.add(entitys);
                }
            }
            if(copyConfigTemplateList.size()>0) {
                sendConfigTemplateService.saveBatch(copyConfigTemplateList);
            }
            return ActionResult.success("复制数据成功");
        }else {
            return ActionResult.fail("复制失败，数据不存在");
        }
    }

    /**
     * 导出消息发送配置
     *
     * @param id 账号配置id
     * @return ignore
     */
    @Operation(summary = "导出")
    @GetMapping("/{id}/Action/Export")
    public ActionResult export(@PathVariable String id) {
        SendMessageConfigEntity entity = sendMessageConfigService.getInfo(id);
        SendMessageConfigInfoVO vo = JsonUtil.getJsonToBean(entity, SendMessageConfigInfoVO.class);

        //子表数据
        List<SendConfigTemplateEntity> sendConfigTemplateList = sendMessageConfigService.getSendConfigTemplateList(id);
        vo.setSendConfigTemplateList(sendConfigTemplateList);
        //导出文件
        DownloadVO downloadVO = fileExport.exportFile(vo, configValueUtil.getTemporaryFilePath(), entity.getFullName(), ModuleTypeEnum.MESSAGE_SEND_CONFIG.getTableName());
        return ActionResult.success(downloadVO);
    }

    /**
     * 导入账号配置
     *
     * @param multipartFile 备份json文件
     * @return 执行结果标识
     */
    @Operation(summary = "导入")
    @PostMapping(value = "/Action/Import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ActionResult importData(@RequestPart("file") MultipartFile multipartFile) throws DataException {
        UserInfo userInfo = userProvider.get();
        //判断是否为.json结尾
        if (FileUtil.existsSuffix(multipartFile, ModuleTypeEnum.MESSAGE_SEND_CONFIG.getTableName())) {
            return ActionResult.fail(MsgCode.IMP002.get());
        }
        //获取文件内容
        String fileContent = FileUtil.getFileContent(multipartFile);
        SendMessageConfigInfoVO infoVO = JsonUtil.getJsonToBean(fileContent, SendMessageConfigInfoVO.class);
        SendMessageConfigEntity entity = JsonUtil.getJsonToBean(infoVO, SendMessageConfigEntity.class);
        entity.setCreatorUserId(userInfo.getUserId());
        entity.setCreatorTime(DateUtil.getNowDate());
        if (infoVO.getSendConfigTemplateList() != null) {
            List<SendConfigTemplateEntity> sendConfigTemplateList = JsonUtil.getJsonToList(infoVO.getSendConfigTemplateList(), SendConfigTemplateEntity.class);
            sendConfigTemplateService.saveBatch(sendConfigTemplateList);
        }
        return sendMessageConfigService.ImportData(entity);
    }


}
