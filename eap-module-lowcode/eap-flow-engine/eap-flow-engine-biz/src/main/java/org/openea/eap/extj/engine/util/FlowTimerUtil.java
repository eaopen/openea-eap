package org.openea.eap.extj.engine.util;

import org.openea.eap.extj.engine.entity.FlowTaskEntity;
import org.openea.eap.extj.engine.entity.FlowTaskNodeEntity;
import org.openea.eap.extj.engine.entity.FlowTaskOperatorEntity;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.LimitModel;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.nodejson.ChildNodeList;
import org.openea.eap.extj.engine.model.flowtime.FlowTimeModel;
import org.openea.eap.extj.engine.service.FlowTaskNodeService;
import org.openea.eap.extj.engine.service.FlowTaskOperatorService;
import org.openea.eap.extj.engine.service.FlowTaskService;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.util.DateUtil;
import org.openea.eap.extj.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 
 */
@Component
public class FlowTimerUtil {

    @Autowired
    private FlowTaskNodeService flowTaskNodeService;
    @Autowired
    private FlowTaskService flowTaskService;
    @Autowired
    private FlowTaskOperatorService flowTaskOperatorService;


    /**
     * 限时开始时间
     */
    public FlowTimeModel time(FlowTaskNodeEntity taskNodeEntity, List<FlowTaskNodeEntity> nodeList
            , FlowTaskEntity flowTaskEntity, FlowTaskOperatorEntity operatorInfo) throws WorkFlowException {
        FlowTaskNodeEntity startNode = nodeList.stream().filter(t -> FlowNature.NodeStart.equals(t.getNodeType())).findFirst().orElse(null);
        String nodeJson = startNode != null ? startNode.getNodePropertyJson() : "{}";
        ChildNodeList childNode = JsonUtil.getJsonToBean(taskNodeEntity.getNodePropertyJson(), ChildNodeList.class);
        FlowTimeModel date = new FlowTimeModel();
        date.setChildNode(JsonUtil.getJsonToBean(nodeJson, ChildNodeList.class));//开始节点
        date.setChildNodeEvnet(childNode);//当前节点
        Date(operatorInfo, flowTaskEntity, date);
        return date;
    }


    private void Date(FlowTaskOperatorEntity operatorInfo, FlowTaskEntity flowTaskEntity, FlowTimeModel flowTimeModel) throws WorkFlowException {
        Properties taskProperties = flowTimeModel.getChildNodeEvnet().getProperties();
        LimitModel limitModel = taskProperties.getTimeLimitConfig();
        boolean isOn = limitModel.getOn() != 0;
        if (limitModel.getOn() == 2) {
            taskProperties = flowTimeModel.getChildNode().getProperties();
            limitModel = taskProperties.getTimeLimitConfig();
        }
        Map<String, Object> data = JsonUtil.stringToMap(flowTaskEntity.getFlowFormContentJson());
        flowTimeModel.setOn(isOn);
        if (isOn) {
            Date date = null;
            if (limitModel.getNodeLimit() == 0) {
                date = operatorInfo.getCreatorTime();
            } else if (limitModel.getNodeLimit() == 1) {
                date = flowTaskEntity.getCreatorTime();
            } else {
                Object formData = data.get(limitModel.getFormField());
                try {
                    date = new Date((Long) formData);
                } catch (Exception e) {
                }
                if (date == null) {
                    try {
                        date = DateUtil.stringToDate(String.valueOf(formData));
                    } catch (Exception e) {
                    }
                }
            }
            if (date == null) {
                date = flowTaskEntity.getCreatorTime();
            }
            flowTimeModel.setDate(date);
        }
    }

}
