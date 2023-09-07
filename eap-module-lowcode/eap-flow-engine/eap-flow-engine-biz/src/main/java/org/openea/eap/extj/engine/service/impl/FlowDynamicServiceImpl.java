package org.openea.eap.extj.engine.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.engine.entity.FlowTemplateEntity;
import org.openea.eap.extj.engine.entity.FlowTemplateJsonEntity;
import org.openea.eap.extj.engine.enums.FlowStatusEnum;
import org.openea.eap.extj.engine.model.flowbefore.FlowTemplateAllModel;
import org.openea.eap.extj.engine.model.flowengine.FlowModel;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.ChildNode;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.permission.entity.UserEntity;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.RandomUtil;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.engine.service.FlowDynamicService;
import org.openea.eap.extj.engine.service.FlowTaskNewService;
import org.openea.eap.extj.engine.util.FlowTaskUtil;
import org.openea.eap.extj.util.ServiceAllUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 在线开发工作流
 *
 *
 */
@Slf4j
@Service
public class FlowDynamicServiceImpl implements FlowDynamicService {


    @Autowired
    public ServiceAllUtil serviceUtil;
    @Autowired
    private FlowTaskUtil flowTaskUtil;
    @Autowired
    private FlowTaskNewService flowTaskNewService;

    @Override
    @DSTransactional
    public void flowTask(FlowModel flowModel, FlowStatusEnum flowStatus, String formId) throws WorkFlowException {
        //流程数据
        switch (flowStatus) {
            case save:
                flowTaskNewService.save(flowModel);
                break;
            case submit:
                //todo 获取表单最新数据
                flowModel.setFormData(flowTaskUtil.infoData(formId, flowModel.getProcessId()));
                flowTaskNewService.submitAll(flowModel);
                break;
            default:
                break;
        }
    }

    @Override
    @DSTransactional
    public void createOrUpdate(FlowModel flowModel) throws WorkFlowException {
        FlowTemplateAllModel model = flowTaskUtil.templateJson(flowModel.getFlowId());
        FlowTemplateJsonEntity templateJson = model.getTemplateJson();
        FlowTemplateEntity template = model.getTemplate();
        ChildNode childNode = JsonUtil.getJsonToBean(templateJson.getFlowTemplateJson(), ChildNode.class);
        String formId = childNode.getProperties().getFormId();
        String processId = flowModel.getProcessId();
        Map<String, Object> formData = flowModel.getFormData();
        formData.put("flowId", flowModel.getFlowId());
        //todo 调用表单保存数据
        serviceUtil.createOrUpdateDelegateUser(formId, processId, formData, serviceUtil.getUserInfo(flowModel.getUserId()));
        FlowStatusEnum statusEnum = FlowStatusEnum.submit.getMessage().equals(flowModel.getStatus()) ? FlowStatusEnum.submit :
                template.getType() == 0 ? FlowStatusEnum.save : FlowStatusEnum.none;
        this.flowTask(flowModel, statusEnum, formId);
    }

    @Override
    @DSTransactional
    public void batchCreateOrUpdate(FlowModel flowModel) throws WorkFlowException {
        UserInfo userInfo = flowModel.getUserInfo();
        List<String> batchUserId = flowModel.getDelegateUserList();
        boolean isBatchUser = batchUserId.size() == 0;
        if (isBatchUser) {
            batchUserId.add(userInfo.getUserId());
        }
        for (String id : batchUserId) {
            FlowModel model = JsonUtil.getJsonToBean(flowModel, FlowModel.class);
            model.setDelegateUser(isBatchUser ? model.getDelegateUser() : userInfo.getUserId());
            model.setProcessId(StringUtil.isNotEmpty(model.getId()) ? model.getId() : RandomUtil.uuId());
            if (!isBatchUser) {
                UserEntity userEntity = serviceUtil.getUserInfo(id);
                if (userEntity != null) {
                    UserInfo info = new UserInfo();
                    info.setUserName(userEntity.getRealName());
                    info.setUserId(userEntity.getId());
                    model.setUserInfo(info);
                }
            }
            model.setUserId(id);
            this.createOrUpdate(model);
        }
    }


}
