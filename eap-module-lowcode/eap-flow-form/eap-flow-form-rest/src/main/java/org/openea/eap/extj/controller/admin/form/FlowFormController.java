package org.openea.eap.extj.controller.admin.form;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.controller.SuperController;
import org.openea.eap.extj.base.vo.DownloadVO;
import org.openea.eap.extj.base.vo.PaginationVO;
import org.openea.eap.extj.config.ConfigValueUtil;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.form.entity.FlowFormEntity;
import org.openea.eap.extj.form.entity.FlowFormRelationEntity;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.form.model.flow.FlowTempInfoModel;
import org.openea.eap.extj.form.model.form.*;
import org.openea.eap.extj.model.visualJson.FieLdsModel;
import org.openea.eap.extj.model.visualJson.FormCloumnUtil;
import org.openea.eap.extj.model.visualJson.FormDataModel;
import org.openea.eap.extj.model.visualJson.TableModel;
import org.openea.eap.extj.model.visualJson.analysis.RecursionForm;
import org.openea.eap.extj.permission.entity.UserEntity;
import org.openea.eap.extj.permission.service.UserService;
import org.openea.eap.extj.form.service.FlowFormRelationService;
import org.openea.eap.extj.form.service.FlowFormService;
import org.openea.eap.extj.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.openea.eap.extj.util.enums.ModuleTypeEnum;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 流程设计
 *
 */
@Tag(name = "流程表单控制器", description = "FlowForm")
@RestController
@RequestMapping("/api/flowForm/Form")
public class FlowFormController extends SuperController<FlowFormService, FlowFormEntity> {
    @Autowired
    private FlowFormService flowFormService;

    @Autowired
    private UserProvider userProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private DataFileExport fileExport;

    @Autowired
    private ConfigValueUtil configValueUtil;

    @Autowired
    private FlowFormRelationService flowFormRelationService;

    @Operation(summary = "表单列表")
    @GetMapping
    @SaCheckPermission("formDesign" )
    public ActionResult getList(FlowFormPage flowFormPage) {
        List<FlowFormEntity> list = flowFormService.getList(flowFormPage);
        List<FlowFormVo> listVo = JsonUtil.getJsonToList(list, FlowFormVo.class);
        listVo.stream().forEach(item -> {
            if (StringUtil.isNotEmpty(item.getCreatorUserId())) {
                UserEntity info = userService.getInfo(item.getCreatorUserId());
                if (info != null) item.setCreatorUser(info.getRealName() + "/" + info.getAccount());
            }
            if (StringUtil.isNotEmpty(item.getLastModifyUserId())) {
                UserEntity info = userService.getInfo(item.getLastModifyUserId());
                if (info != null) item.setLastModifyUser(info.getRealName() + "/" + info.getAccount());
            }
        });
        PaginationVO paginationVO = JsonUtil.getJsonToBean(flowFormPage, PaginationVO.class);
        return ActionResult.page(listVo, paginationVO);
    }

    @Operation(summary = "表单下拉列表")
    @GetMapping("/select")
        public ActionResult getListForSelect(FlowFormPage flowFormPage) {
        List<FlowFormEntity> list = flowFormService.getListForSelect(flowFormPage);
        List<FlowSelectVo> listVo = JsonUtil.getJsonToList(list, FlowSelectVo.class);
        PaginationVO paginationVO = JsonUtil.getJsonToBean(flowFormPage, PaginationVO.class);
        return ActionResult.page(listVo, paginationVO);
    }

    @Operation(summary = "查看")
    @Parameters({
            @Parameter(name = "id" , description = "主键" , required = true)
    })
    @GetMapping("/{id}")
    @SaCheckPermission(value = {"formDesign" , "onlineDev.webDesign","generator.webForm","generator.flowForm"}, mode = SaMode.OR)
    public ActionResult getInfo(@PathVariable("id") String id) {
        FlowFormEntity entity = flowFormService.getById(id);
        FlowFormVo vo = JsonUtil.getJsonToBean(entity, FlowFormVo.class);
        if (ObjectUtil.isNotEmpty(entity.getDraftJson())) {
            FormDraftJsonModel formDraft = JsonUtil.getJsonToBean(entity.getDraftJson(), FormDraftJsonModel.class);
            vo.setDraftJson(Optional.ofNullable(formDraft.getDraftJson()).orElse(null));
            vo.setTableJson(formDraft.getTableJson());
        }
        return ActionResult.success(vo);
    }

    @Operation(summary = "保存表单")
    @PostMapping
    @SaCheckPermission("formDesign" )
    public ActionResult save(@RequestBody FlowFormModel formModel) throws WorkFlowException {
        FlowFormEntity entity = JsonUtil.getJsonToBean(formModel, FlowFormEntity.class);
        //判断子表是否复用
        if (formModel.getFormType() == 2 && entity.getDraftJson() != null) {
            RecursionForm recursionForm = new RecursionForm();
            FormDataModel formData = JsonUtil.getJsonToBean(entity.getDraftJson(), FormDataModel.class);
            List<FieLdsModel> list = JsonUtil.getJsonToList(formData.getFields(), FieLdsModel.class);
            recursionForm.setList(list);
            List<TableModel> tableModelList = JsonUtil.getJsonToList(entity.getTableJson(), TableModel.class);
            recursionForm.setTableModelList(tableModelList);
            if (FormCloumnUtil.repetition(recursionForm, new ArrayList<>())) {
                return ActionResult.fail("子表重复");
            }
        }
        //判断名称是否重复
        if (flowFormService.isExistByFullName(entity.getFullName(), entity.getId())) {
            return ActionResult.fail(MsgCode.EXIST001.get());
        }
        //判断编码是否重复
        if (flowFormService.isExistByEnCode(entity.getEnCode(), entity.getId())) {
            return ActionResult.fail(MsgCode.EXIST002.get());
        }
        entity.setCreatorUserId(userProvider.get().getUserId());
        entity.setCreatorTime(new Date());
        entity.setEnabledMark(0);//首次创建为未发布
        flowFormService.create(entity);
        return ActionResult.success(MsgCode.SU002.get());
    }

    @Operation(summary = "修改表单")
    @PutMapping
    @SaCheckPermission("formDesign" )
    public ActionResult update(@RequestBody FlowFormModel formModel) throws Exception {
        FlowFormEntity entity = JsonUtil.getJsonToBean(formModel, FlowFormEntity.class);
        if (flowFormService.isExistByFullName(entity.getFullName(), entity.getId())) {
            return ActionResult.fail(MsgCode.EXIST001.get());
        }
        if (flowFormService.isExistByEnCode(entity.getEnCode(), entity.getId())) {
            return ActionResult.fail(MsgCode.EXIST002.get());
        }
        entity.setLastModifyUserId(userProvider.get().getUserId());
        entity.setLastModifyTime(new Date());
        //判断子表是否复用
        List<TableModel> tableModelList = JsonUtil.getJsonToList(entity.getTableJson(), TableModel.class);
        //在已发布的状态下 删表动作禁用
        if (formModel.getFormType() == 2 && entity.getEnabledMark() == 1 && tableModelList.size() == 0) {
            return ActionResult.fail(MsgCode.VS408.get());
        }
        if (formModel.getFormType() == 2 && tableModelList.size() > 0) {
            RecursionForm recursionForm = new RecursionForm();
            FormDataModel formData = JsonUtil.getJsonToBean(entity.getDraftJson(), FormDataModel.class);
            List<FieLdsModel> list = JsonUtil.getJsonToList(formData.getFields(), FieLdsModel.class);
            recursionForm.setList(list);
            recursionForm.setTableModelList(tableModelList);
            if (FormCloumnUtil.repetition(recursionForm, new ArrayList<>())) {
                return ActionResult.fail("子表重复");
            }

        }
        boolean b = flowFormService.update(entity);
        if (b) {
            return ActionResult.success(MsgCode.SU004.get());
        }
        return ActionResult.fail(MsgCode.FA002.get());
    }

    @Operation(summary = "发布/回滚")
    @Parameters({
            @Parameter(name = "id" , description = "主键" , required = true),
            @Parameter(name = "isRelease" , description = "是否发布：是否发布：1-发布 0-回滚" )
    })
    @PostMapping("/Release/{id}")
    @SaCheckPermission("formDesign" )
    public ActionResult release(@PathVariable("id") String id, @RequestParam("isRelease") Integer isRelease) throws WorkFlowException {
        return flowFormService.release(id, isRelease);
    }

    @Operation(summary = "复制表单")
    @Parameters({
            @Parameter(name = "id" , description = "主键" , required = true)
    })
    @GetMapping("/{id}/Actions/Copy")
    @SaCheckPermission("formDesign" )
    public ActionResult copyForm(@PathVariable("id") String id) {
        try {
            boolean b = flowFormService.copyForm(id);
            if (b) {
                return ActionResult.success(MsgCode.SU007.get());
            }
        } catch (Exception e) {
            return ActionResult.fail("已到达该模板复制上限，请复制源模板!");
        }

        return ActionResult.fail("复制失败");
    }

    @Operation(summary = "删除表单")
    @Parameters({
            @Parameter(name = "id" , description = "主键" , required = true)
    })
    @DeleteMapping("/{id}")
    @SaCheckPermission("formDesign" )
    public ActionResult delete(@PathVariable("id") String id) {
        //todo 该表单已被流程引用，无法删除 -完成
        List<FlowFormRelationEntity> listByFormId = flowFormRelationService.getListByFormId(id);
        if (CollectionUtils.isNotEmpty(listByFormId)) {
            return ActionResult.fail("该表单已被流程引用，无法删除！");
        }
        boolean b = flowFormService.removeById(id);
        if (b) {
            return ActionResult.success(MsgCode.SU003.get());
        }
        return ActionResult.fail(MsgCode.FA003.get());
    }


    @Operation(summary = "工作流表单导出")
    @Parameters({
            @Parameter(name = "id" , description = "主键" , required = true)
    })
    @GetMapping("/{id}/Actions/ExportData")
    @SaCheckPermission("formDesign" )
    public ActionResult exportData(@PathVariable("id") String id) throws WorkFlowException {
        FlowFormEntity entity = flowFormService.getById(id);
        DownloadVO downloadVO = fileExport.exportFile(entity, configValueUtil.getTemporaryFilePath(), entity.getFullName(), ModuleTypeEnum.FLOW_FLOWDFORM.getTableName());
        return ActionResult.success(downloadVO);
    }


    @Operation(summary = "工作流表单导入")
    @PostMapping(value = "/Actions/ImportData", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SaCheckPermission("formDesign" )
    public ActionResult ImportData(@RequestPart("file") MultipartFile multipartFile) throws WorkFlowException {
        //判断是否为.json结尾
        if (FileUtil.existsSuffix(multipartFile, ModuleTypeEnum.FLOW_FLOWDFORM.getTableName())) {
            return ActionResult.fail(MsgCode.IMP002.get());
        }
        //获取文件内容
        String fileContent = FileUtil.getFileContent(multipartFile);
        FlowFormEntity vo = JsonUtil.getJsonToBean(fileContent, FlowFormEntity.class);
        return flowFormService.ImportData(vo);
    }

    @Operation(summary = "获取表单字段列表")
    @Parameters({
            @Parameter(name = "id" , description = "主键" , required = true)
    })
    @GetMapping(value = "/{id}/getField")
    @SaCheckPermission("formDesign" )
    public ActionResult getField(@PathVariable("id") String id) {
        FlowFormEntity entity = flowFormService.getById(id);
        if (entity == null || entity.getEnabledMark() != 1) return ActionResult.fail("表单不存在或者未发布！");
        FlowFormVo vo = JsonUtil.getJsonToBean(entity, FlowFormVo.class);
        List<FlowFieldModel> list = new ArrayList<>();
        if (vo.getFormType() == 0) {//0系统表单
            list = JsonUtil.getJsonToList(vo.getPropertyJson(), FlowFieldModel.class);
        } else {
            JSONObject objects = JSONObject.parseObject(vo.getPropertyJson());
            JSONArray arr = objects.getJSONArray("fields");
            for (Object obj : arr) {
                JSONObject object = (JSONObject) obj;
                FlowFieldModel flowFieldModel = new FlowFieldModel();
                JSONObject config = object.getJSONObject("__config__");
                flowFieldModel.setFiledId(object.get("__vModel__").toString())
                        .setFiledName(config.get("label").toString())
                        .setJnpfKey(config.get("jnpfKey").toString())
                        .setRequired(config.get("required").toString());
                list.add(flowFieldModel);
            }
        }
        return ActionResult.success(list);
    }

    @Operation(summary = "流程引用表单保存")
    @Parameters({
            @Parameter(name = "flowId" , description = "流程主键" , required = true),
            @Parameter(name = "formIds" , description = "表单主键数组" , required = true)
    })
    @PostMapping("/saveFlowId")
    public ActionResult saveFlowIdByFormIds(@RequestParam("flowId") String flowId, @RequestBody List<String> formIds) {
        flowFormRelationService.saveFlowIdByFormIds(flowId, formIds);
        return ActionResult.success();
    }

    /**
     * 获取引擎id
     *
     * @return
     */
    @Operation(summary = "获取引擎id")
    @Parameters({
            @Parameter(name = "id" , description = "主键" , required = true)
    })
    @GetMapping("/getFormById/{id}")
    public ActionResult getFormById(@PathVariable("id") String id) throws WorkFlowException {
        FlowTempInfoModel model = flowFormService.getFormById(id);
        if (model != null && model.getId() != null) {
            return ActionResult.success("获取成功", model);
        }
        return ActionResult.fail("未找到流程引擎");
    }

}
