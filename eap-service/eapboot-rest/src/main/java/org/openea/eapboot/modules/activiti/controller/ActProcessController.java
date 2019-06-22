package org.openea.eapboot.modules.activiti.controller;

import org.openea.eapboot.common.constant.ActivitiConstant;
import org.openea.eapboot.common.exception.EapbootException;
import org.openea.eapboot.common.utils.PageUtil;
import org.openea.eapboot.common.utils.ResultUtil;
import org.openea.eapboot.common.vo.PageVo;
import org.openea.eapboot.modules.activiti.service.*;
import org.openea.eapboot.modules.activiti.entity.ActCategory;
import org.openea.eapboot.modules.activiti.entity.ActModel;
import org.openea.eapboot.modules.activiti.entity.ActNode;
import org.openea.eapboot.modules.activiti.entity.ActProcess;
import org.openea.eapboot.modules.activiti.vo.ProcessNodeVo;
import org.openea.eapboot.common.vo.Result;
import org.openea.eapboot.common.vo.SearchVo;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.*;

/**
 */
@Slf4j
@RestController
@Api(description = "流程定义管理接口")
@RequestMapping("/eapboot/actProcess")
@Transactional
public class ActProcessController {

    @Autowired
    private ActModelService actModelService;

    @Autowired
    private ActProcessService actProcessService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ActNodeService actNodeService;

    @Autowired
    private ActCategoryService actCategoryService;

    @Autowired
    private ActBusinessService actBusinessService;

    @RequestMapping(value = "/getByCondition", method = RequestMethod.GET)
    @ApiOperation(value = "多条件分页获取流程列表")
    public Result<Page<ActProcess>> getByCondition(@ApiParam("是否显示所有版本") @RequestParam(required = false) Boolean showLatest,
                                                   @ModelAttribute ActProcess actProcess,
                                                   @ModelAttribute SearchVo searchVo,
                                                   @ModelAttribute PageVo pageVo){

        Page<ActProcess> page = actProcessService.findByCondition(showLatest, actProcess, searchVo, PageUtil.initPage(pageVo));
        page.getContent().forEach(e -> {
            if(StrUtil.isNotBlank(e.getCategoryId())){
                ActCategory category = actCategoryService.get(e.getCategoryId());
                if(category!=null){
                    e.setCategoryTitle(category.getTitle());
                }
            }
        });
        return new ResultUtil<Page<ActProcess>>().setData(page);
    }

    @RequestMapping(value = "/getByKey/{key}", method = RequestMethod.GET)
    @ApiOperation(value = "通过key获取最新流程")
    public Result<ActProcess> getByCondition(@PathVariable String key){

        ActProcess actProcess = actProcessService.findByProcessKeyAndLatest(key, true);
        return new ResultUtil<ActProcess>().setData(actProcess);
    }

    @RequestMapping(value = "/updateInfo", method = RequestMethod.POST)
    @ApiOperation(value = "修改所属分类或备注")
    public Result<Object> updateInfo(@ModelAttribute ActProcess actProcess){

        ProcessDefinition pd = repositoryService.getProcessDefinition(actProcess.getId());
        if(pd==null){
            return new ResultUtil<Object>().setErrorMsg("流程定义不存在");
        }
        if(StrUtil.isNotBlank(actProcess.getCategoryId())){
            repositoryService.setProcessDefinitionCategory(actProcess.getId(), actProcess.getCategoryId());
            repositoryService.setDeploymentCategory(pd.getDeploymentId(), actProcess.getCategoryId());
        }
        actProcessService.update(actProcess);
        return new ResultUtil<Object>().setData("修改成功");
    }

    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @ApiOperation(value = "激活或挂起流程定义")
    public Result<Object> updateStatus(@ApiParam("流程定义id") @RequestParam String id,
                                       @RequestParam Integer status){

        if(ActivitiConstant.PROCESS_STATUS_ACTIVE.equals(status)){
            repositoryService.activateProcessDefinitionById(id, true, new Date());
        }else if(ActivitiConstant.PROCESS_STATUS_SUSPEND.equals(status)){
            repositoryService.suspendProcessDefinitionById(id, true, new Date());
        }

        ActProcess actProcess = actProcessService.get(id);
        actProcess.setStatus(status);
        actProcessService.update(actProcess);
        return new ResultUtil<Object>().setData("修改成功");
    }

    @RequestMapping(value = "/export", method = RequestMethod.GET)
    @ApiOperation(value = "导出部署流程资源")
    public void exportResource(@ApiParam("流程定义id") @RequestParam String id,
                               @RequestParam Integer type,
                               HttpServletResponse response){

        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(id).singleResult();

        String resourceName = "";
        if (ActivitiConstant.RESOURCE_TYPE_XML.equals(type)) {
            resourceName = pd.getResourceName();
        } else if (ActivitiConstant.RESOURCE_TYPE_IMAGE.equals(type)) {
            resourceName = pd.getDiagramResourceName();
        }
        InputStream inputStream = repositoryService.getResourceAsStream(pd.getDeploymentId(),
                resourceName);

        try {
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(resourceName, "UTF-8"));
            byte[] b = new byte[1024];
            int len = -1;
            while ((len = inputStream.read(b, 0, 1024)) != -1) {
                response.getOutputStream().write(b, 0, len);
            }
            response.flushBuffer();
        } catch (IOException e) {
            log.error(e.toString());
            throw new EapbootException("导出部署流程资源失败");
        }
    }

    @RequestMapping(value = "/convertToModel/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "转化流程为模型")
    public Result<Object> convertToModel(@ApiParam("流程定义id") @PathVariable String id){

        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionId(id).singleResult();
        InputStream bpmnStream = repositoryService.getResourceAsStream(pd.getDeploymentId(), pd.getResourceName());
        ActProcess actProcess = actProcessService.get(id);

        try {
            XMLInputFactory xif = XMLInputFactory.newInstance();
            InputStreamReader in = new InputStreamReader(bpmnStream, "UTF-8");
            XMLStreamReader xtr = xif.createXMLStreamReader(in);
            BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xtr);
            BpmnJsonConverter converter = new BpmnJsonConverter();

            ObjectNode modelNode = converter.convertToJson(bpmnModel);
            Model modelData = repositoryService.newModel();
            modelData.setKey(pd.getKey());
            modelData.setName(pd.getResourceName());

            ObjectNode modelObjectNode = new ObjectMapper().createObjectNode();
            modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, actProcess.getName());
            modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, modelData.getVersion());
            modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, actProcess.getDescription());
            modelData.setMetaInfo(modelObjectNode.toString());

            repositoryService.saveModel(modelData);
            repositoryService.addModelEditorSource(modelData.getId(), modelNode.toString().getBytes("utf-8"));

            // 保存扩展模型至数据库
            ActModel actModel = new ActModel();
            actModel.setId(modelData.getId());
            actModel.setName(modelData.getName());
            actModel.setModelKey(modelData.getKey());
            actModel.setDescription(actProcess.getDescription());
            actModel.setVersion(modelData.getVersion());
            actModelService.save(actModel);
        }catch (Exception e){
            log.error(e.toString());
            return new ResultUtil<Object>().setErrorMsg("转化流程为模型失败");
        }
        return new ResultUtil<Object>().setData("修改成功");
    }

    @RequestMapping(value = "/getProcessNode/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "通过流程定义id获取流程节点")
    public Result<List<ProcessNodeVo>> getProcessNode(@ApiParam("流程定义id") @PathVariable String id){

        BpmnModel bpmnModel = repositoryService.getBpmnModel(id);

        List<ProcessNodeVo> list = new ArrayList<>();

        List<Process> processes = bpmnModel.getProcesses();
        if(processes==null||processes.size()==0){
            return new ResultUtil<List<ProcessNodeVo>>().setData(null);
        }
        for(Process process : processes){
            Collection<FlowElement> elements = process.getFlowElements();
            for(FlowElement element : elements){
                ProcessNodeVo node = new ProcessNodeVo();
                node.setId(element.getId());
                node.setTitle(element.getName());
                if(element instanceof StartEvent){
                    // 开始节点
                    node.setType(ActivitiConstant.NODE_TYPE_START);
                }else if(element instanceof UserTask){
                    // 用户任务
                    node.setType(ActivitiConstant.NODE_TYPE_TASK);
                    // 设置关联用户
                    node.setUsers(actNodeService.findUserByNodeId(element.getId()));
                    // 设置关联角色
                    node.setRoles(actNodeService.findRoleByNodeId(element.getId()));
                    // 设置关联部门
                    node.setDepartments(actNodeService.findDepartmentByNodeId(element.getId()));
                }else if(element instanceof EndEvent){
                    // 结束
                    node.setType(ActivitiConstant.NODE_TYPE_END);
                }else{
                    // 排除其他连线或节点
                    continue;
                }
                list.add(node);
            }
        }
        return new ResultUtil<List<ProcessNodeVo>>().setData(list);
    }

    @RequestMapping(value = "/editNodeUser", method = RequestMethod.POST)
    @ApiOperation(value = "编辑节点分配用户")
    public Result<Object> editNodeUser(@RequestParam String nodeId,
                                       @RequestParam(required = false) String[] userIds,
                                       @RequestParam(required = false) String[] roleIds,
                                       @RequestParam(required = false) String[] departmentIds){

        // 删除其关联权限
        actNodeService.deleteByNodeId(nodeId);
        // 分配新用户
        for(String userId : userIds){
            ActNode actNode = new ActNode();
            actNode.setNodeId(nodeId);
            actNode.setRelateId(userId);
            actNode.setType(ActivitiConstant.NODE_USER);
            actNodeService.save(actNode);
        }
        // 分配新角色
        for(String roleId : roleIds){
            ActNode actNode = new ActNode();
            actNode.setNodeId(nodeId);
            actNode.setRelateId(roleId);
            actNode.setType(ActivitiConstant.NODE_ROLE);
            actNodeService.save(actNode);
        }
        // 分配新部门
        for(String departmentId : departmentIds){
            ActNode actNode = new ActNode();
            actNode.setNodeId(nodeId);
            actNode.setRelateId(departmentId);
            actNode.setType(ActivitiConstant.NODE_DEPARTMENT);
            actNodeService.save(actNode);
        }
        return new ResultUtil<Object>().setSuccessMsg("操作成功");
    }

    @RequestMapping(value = "/delByIds/{ids}", method = RequestMethod.DELETE)
    @ApiOperation(value = "通过id删除流程")
    public Result<Object> delByIds(@PathVariable String[] ids){

        for(String id : ids){
            if(CollectionUtil.isNotEmpty(actBusinessService.findByProcDefId(id))){
                return new ResultUtil<Object>().setErrorMsg("包含已发起申请的流程，无法删除");
            }
            ActProcess actProcess = actProcessService.get(id);
            // 当删除最后一个版本时 删除关联数据
            if(actProcess.getVersion()==1){
                deleteNodeUsers(id);
            }
            // 级联删除
            repositoryService.deleteDeployment(actProcess.getDeploymentId(), true);
            actProcessService.delete(id);
            // 更新最新版本
            actProcessService.setLatestByProcessKey(actProcess.getProcessKey());
        }
        return new ResultUtil<Object>().setData("删除成功");
    }

    public void deleteNodeUsers(String processId){

        BpmnModel bpmnModel = repositoryService.getBpmnModel(processId);
        List<Process> processes = bpmnModel.getProcesses();
        for(Process process : processes){
            Collection<FlowElement> elements = process.getFlowElements();
            for(FlowElement element : elements) {
                actNodeService.deleteByNodeId(element.getId());
            }
        }
    }
}
