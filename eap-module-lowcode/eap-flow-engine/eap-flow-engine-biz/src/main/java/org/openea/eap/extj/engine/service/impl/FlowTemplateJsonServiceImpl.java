package org.openea.eap.extj.engine.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.engine.entity.FlowTemplateEntity;
import org.openea.eap.extj.engine.entity.FlowTemplateJsonEntity;
import org.openea.eap.extj.engine.model.flowengine.FlowPagination;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.ChildNode;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.MsgConfig;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.nodejson.ChildNodeList;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.nodejson.ConditionList;
import org.openea.eap.extj.engine.model.flowtemplatejson.FlowTemplateJsonPage;
import org.openea.eap.extj.form.entity.FlowFormEntity;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.util.*;
import org.openea.eap.extj.engine.mapper.FlowTemplateJsonMapper;
import org.openea.eap.extj.engine.service.FlowTemplateJsonService;
import org.openea.eap.extj.engine.service.FlowTemplateService;
import org.openea.eap.extj.engine.util.FlowJsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 流程引擎
 *
 *
 */
@Service
public class FlowTemplateJsonServiceImpl extends SuperServiceImpl<FlowTemplateJsonMapper, FlowTemplateJsonEntity> implements FlowTemplateJsonService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private ServiceAllUtil serviceUtil;
    @Autowired
    private FlowTemplateService flowTemplateService;

    @Override
    public List<FlowTemplateJsonEntity> getMonitorList() {
        UserInfo userInfo = userProvider.get();
        QueryWrapper<FlowTemplateJsonEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTemplateJsonEntity::getCreatorUserId, userInfo.getUserId());
        queryWrapper.lambda().select(FlowTemplateJsonEntity::getTemplateId);
        return this.list(queryWrapper);
    }

    @Override
    public List<FlowTemplateJsonEntity> getTemplateList(List<String> id) {
        List<FlowTemplateJsonEntity> list = new ArrayList<>();
        if (id.size() > 0) {
            QueryWrapper<FlowTemplateJsonEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(FlowTemplateJsonEntity::getTemplateId, id);
            queryWrapper.lambda().orderByDesc(FlowTemplateJsonEntity::getDeleteMark);
            list = this.list(queryWrapper);
        }
        return list;
    }

    @Override
    public List<FlowTemplateJsonEntity> getTemplateJsonList(List<String> id) {
        List<FlowTemplateJsonEntity> list = new ArrayList<>();
        if (id.size() > 0) {
            QueryWrapper<FlowTemplateJsonEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(FlowTemplateJsonEntity::getId, id);
            queryWrapper.lambda().orderByDesc(FlowTemplateJsonEntity::getDeleteMark);
            list = this.list(queryWrapper);
        }
        return list;
    }

    @Override
    public List<FlowTemplateJsonEntity> getListPage(FlowTemplateJsonPage pagination, boolean isPage) {
        FlowTemplateJsonEntity info = getJsonInfo(pagination.getFlowId());
        if (info != null) {
            pagination.setGroupId(info.getGroupId());
        }
        QueryWrapper<FlowTemplateJsonEntity> queryWrapper = new QueryWrapper<>();
        //关键字（流程名称、流程编码）
        String keyWord = pagination.getKeyword() != null ? pagination.getKeyword() : null;
        if (ObjectUtil.isNotEmpty(keyWord)) {
            queryWrapper.lambda().like(FlowTemplateJsonEntity::getVersion, keyWord);
        }
        //流程id
        String templateId = pagination.getTemplateId() != null ? pagination.getTemplateId() : null;
        if (ObjectUtil.isNotEmpty(templateId)) {
            queryWrapper.lambda().eq(FlowTemplateJsonEntity::getTemplateId, templateId);
        }
        //流程状态
        Integer enableMark = pagination.getEnabledMark() != null ? pagination.getEnabledMark() : null;
        if (ObjectUtil.isNotEmpty(enableMark)) {
            queryWrapper.lambda().eq(FlowTemplateJsonEntity::getEnabledMark, enableMark);
        }
        //流程分组
        String groupId = pagination.getGroupId() != null ? pagination.getGroupId() : null;
        if (ObjectUtil.isNotEmpty(groupId)) {
            queryWrapper.lambda().eq(FlowTemplateJsonEntity::getGroupId, groupId);
        }
        //日期范围（近7天、近1月、近3月、自定义）
        String startTime = pagination.getStartTime() != null ? pagination.getStartTime() : null;
        String endTime = pagination.getEndTime() != null ? pagination.getEndTime() : null;
        if (ObjectUtil.isNotEmpty(startTime) && ObjectUtil.isNotEmpty(endTime)) {
            Date startTimes = DateUtil.stringToDate(DateUtil.daFormatYmd(Long.parseLong(startTime)) + " 00:00:00");
            Date endTimes = DateUtil.stringToDate(DateUtil.daFormatYmd(Long.parseLong(endTime)) + " 23:59:59");
            queryWrapper.lambda().between(FlowTemplateJsonEntity::getCreatorTime, startTimes, endTimes);
        }
        queryWrapper.lambda().orderByDesc(FlowTemplateJsonEntity::getEnabledMark).orderByDesc(FlowTemplateJsonEntity::getCreatorTime);
        if (isPage) {
            Page<FlowTemplateJsonEntity> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
            IPage<FlowTemplateJsonEntity> userPage = this.page(page, queryWrapper);
            return pagination.setData(userPage.getRecords(), page.getTotal());
        } else {
            queryWrapper.lambda().select(FlowTemplateJsonEntity::getId,FlowTemplateJsonEntity::getGroupId);
            return list(queryWrapper);
        }
    }

    @Override
    public List<FlowTemplateJsonEntity> getChildListPage(FlowPagination pagination) {
        String keyword = pagination.getKeyword();
        pagination.setKeyword(null);
        List<String> templateId = flowTemplateService.getListAll(pagination, false).stream().map(FlowTemplateEntity::getId).collect(Collectors.toList());
        if (templateId.size() == 0) {
            return new ArrayList<>();
        }
        QueryWrapper<FlowTemplateJsonEntity> queryWrapper = new QueryWrapper<>();
        if (StringUtil.isNotEmpty(keyword)) {
            queryWrapper.lambda().like(FlowTemplateJsonEntity::getFullName, keyword);
        }
        queryWrapper.lambda().eq(FlowTemplateJsonEntity::getEnabledMark, 1);
        queryWrapper.lambda().eq(FlowTemplateJsonEntity::getVisibleType, 0);
        queryWrapper.lambda().in(FlowTemplateJsonEntity::getTemplateId, templateId);
        queryWrapper.lambda().select(FlowTemplateJsonEntity::getId, FlowTemplateJsonEntity::getTemplateId, FlowTemplateJsonEntity::getFullName);
        Page<FlowTemplateJsonEntity> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<FlowTemplateJsonEntity> userPage = this.page(page, queryWrapper);
        return pagination.setData(userPage.getRecords(), page.getTotal());
    }

    @Override
    public List<FlowTemplateJsonEntity> getMainList(List<String> templaIdList) {
        List<FlowTemplateJsonEntity> list = new ArrayList<>();
        if (templaIdList.size() > 0) {
            QueryWrapper<FlowTemplateJsonEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(FlowTemplateJsonEntity::getTemplateId, templaIdList);
            queryWrapper.lambda().eq(FlowTemplateJsonEntity::getEnabledMark, 1);
            queryWrapper.lambda().orderByAsc(FlowTemplateJsonEntity::getSortCode);
            list.addAll(this.list(queryWrapper));
        }
        return list;
    }

    @Override
    public FlowTemplateJsonEntity getInfo(String id) throws WorkFlowException {
        QueryWrapper<FlowTemplateJsonEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTemplateJsonEntity::getId, id);
        FlowTemplateJsonEntity templateJsonEntity = this.getOne(queryWrapper);
        if (templateJsonEntity == null) {
            throw new WorkFlowException(MsgCode.WF113.get());
        }
        return templateJsonEntity;
    }

    @Override
    public FlowTemplateJsonEntity getJsonInfo(String id) {
        QueryWrapper<FlowTemplateJsonEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTemplateJsonEntity::getId, id);
        FlowTemplateJsonEntity templateJsonEntity = this.getOne(queryWrapper);
        return templateJsonEntity;
    }

    @Override
    public void create(FlowTemplateJsonEntity entity) {
        if (entity.getId() == null) {
            entity.setId(RandomUtil.uuId());
        }
        entity.setCreatorTime(new Date());
        entity.setCreatorUserId(userProvider.get().getUserId());
        this.save(entity);
    }

    @Override
    public void update(String id, FlowTemplateJsonEntity entity) {
        entity.setId(id);
        entity.setLastModifyTime(new Date());
        entity.setLastModifyUserId(userProvider.get().getUserId());
        this.updateById(entity);
    }

    @Override
    public void delete(FlowTemplateJsonEntity entity) {
        if (entity != null) {
            this.removeById(entity.getId());
        }
    }

    @Override
    public void deleteFormFlowId(FlowTemplateJsonEntity entity) {
        if (entity != null) {
            List<FlowFormEntity> flowIdList = serviceUtil.getFlowIdList(entity.getTemplateId());
            for (FlowFormEntity formEntity : flowIdList) {
                formEntity.setFlowId(null);
                serviceUtil.updateForm(formEntity);
            }
            this.removeById(entity.getId());
        }
    }

    @Override
    public List<FlowTemplateJsonEntity> getListAll(List<String> id) {
        QueryWrapper<FlowTemplateJsonEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FlowTemplateJsonEntity::getEnabledMark, 1);
        queryWrapper.lambda().and(w -> w.eq(FlowTemplateJsonEntity::getVisibleType, 0).or(!id.isEmpty()).in(!id.isEmpty(), FlowTemplateJsonEntity::getId, id));
        queryWrapper.lambda().select(FlowTemplateJsonEntity::getId, FlowTemplateJsonEntity::getTemplateId);
        List<FlowTemplateJsonEntity> list = this.list(queryWrapper);
        return list;
    }

    @Override
    @DSTransactional
    public void templateJsonMajor(String ids) throws WorkFlowException {
        String[] idAll = ids.split(",");
        for (String id : idAll) {
            FlowTemplateJsonEntity templateJson = getInfo(id);
            if (StringUtil.isEmpty(templateJson.getFlowTemplateJson())) {
                throw new WorkFlowException("主版本没有内容");
            }
            List<String> idList = new ArrayList() {{
                add(templateJson.getTemplateId());
            }};
            List<FlowTemplateJsonEntity> list = getTemplateList(idList).stream().filter(t -> t.getGroupId().equals(templateJson.getGroupId())).collect(Collectors.toList());
            for (FlowTemplateJsonEntity entity : list) {
                if (entity.getEnabledMark() == 1 || entity.getId().equals(templateJson.getId())) {
                    entity.setEnabledMark(entity.getId().equals(templateJson.getId()) ? 1 : 0);
                    this.update(entity.getId(), entity);
                }
            }
        }
    }

    @Override
    public List<String> sendMsgConfigList(FlowTemplateJsonEntity engine) {
        List<String> sendConfigList = new ArrayList<>();
        ChildNode childNodeAll = JsonUtil.getJsonToBean(engine.getFlowTemplateJson(), ChildNode.class);
        //获取流程节点
        List<ChildNodeList> nodeListAll = new ArrayList<>();
        List<ConditionList> conditionListAll = new ArrayList<>();
        //递归获取条件数据和节点数据
        FlowJsonUtil.getTemplateAll(childNodeAll, nodeListAll, conditionListAll);
        for (ChildNodeList childNode : nodeListAll) {
            Properties properties = childNode.getProperties();
            MsgConfig waitMsgConfig = properties.getWaitMsgConfig();
            MsgConfig endMsgConfig = properties.getEndMsgConfig();
            MsgConfig approveMsgConfig = properties.getApproveMsgConfig();
            MsgConfig rejectMsgConfig = properties.getRejectMsgConfig();
            MsgConfig copyMsgConfig = properties.getCopyMsgConfig();
            MsgConfig launchMsgConfig = properties.getLaunchMsgConfig();
            MsgConfig overtimeMsgConfig = properties.getOvertimeMsgConfig();
            MsgConfig noticeMsgConfig = properties.getNoticeMsgConfig();
            //流程代办
            if (1 == waitMsgConfig.getOn()) {
                if (StringUtil.isNotBlank(waitMsgConfig.getMsgId())) {
                    sendConfigList.add(waitMsgConfig.getMsgId());
                }
            }
            //流程结束
            if (1 == endMsgConfig.getOn()) {
                if (StringUtil.isNotBlank(endMsgConfig.getMsgId())) {
                    sendConfigList.add(endMsgConfig.getMsgId());
                }
            }
            //节点同意
            if (1 == approveMsgConfig.getOn()) {
                if (StringUtil.isNotBlank(approveMsgConfig.getMsgId())) {
                    sendConfigList.add(approveMsgConfig.getMsgId());
                }
            }
            //节点拒绝
            if (1 == rejectMsgConfig.getOn()) {
                if (StringUtil.isNotBlank(rejectMsgConfig.getMsgId())) {
                    sendConfigList.add(rejectMsgConfig.getMsgId());
                }
            }
            //节点抄送
            if (1 == copyMsgConfig.getOn()) {
                if (StringUtil.isNotBlank(copyMsgConfig.getMsgId())) {
                    sendConfigList.add(copyMsgConfig.getMsgId());
                }
            }
            //子流程
            if (1 == launchMsgConfig.getOn()) {
                if (StringUtil.isNotBlank(launchMsgConfig.getMsgId())) {
                    sendConfigList.add(launchMsgConfig.getMsgId());
                }
            }
            //超时
            if (1 == overtimeMsgConfig.getOn()) {
                if (StringUtil.isNotBlank(overtimeMsgConfig.getMsgId())) {
                    sendConfigList.add(overtimeMsgConfig.getMsgId());
                }
            }
            //提醒
            if (1 == noticeMsgConfig.getOn()) {
                if (StringUtil.isNotBlank(waitMsgConfig.getMsgId())) {
                    sendConfigList.add(noticeMsgConfig.getMsgId());
                }
            }
        }
        sendConfigList = sendConfigList.stream().distinct().collect(Collectors.toList());
        return sendConfigList;
    }

    @Override
    public void updateFullName(String groupId, String fullName) {
        UpdateWrapper<FlowTemplateJsonEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(FlowTemplateJsonEntity::getFullName, fullName);
        updateWrapper.lambda().eq(FlowTemplateJsonEntity::getGroupId, groupId);
        this.update(updateWrapper);
    }
}
