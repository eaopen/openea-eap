package org.openea.eap.extj.engine.model.flowmessage;

import org.openea.eap.extj.engine.entity.FlowTaskCirculateEntity;
import org.openea.eap.extj.engine.entity.FlowTaskEntity;
import org.openea.eap.extj.engine.entity.FlowTaskNodeEntity;
import org.openea.eap.extj.engine.entity.FlowTaskOperatorEntity;
import org.openea.eap.extj.engine.model.flowbefore.FlowTemplateAllModel;
import org.openea.eap.extj.engine.model.flowengine.FlowModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
public class FlowMsgModel {
    private String title;
    private FlowTemplateAllModel flowTemplateAllModel = new FlowTemplateAllModel();
    private Map<String, Object> data = new HashMap<>();
    private FlowModel flowModel = new FlowModel();
    private FlowTaskEntity taskEntity = new FlowTaskEntity();
    private FlowTaskNodeEntity taskNodeEntity = new FlowTaskNodeEntity();
    private List<FlowTaskNodeEntity> nodeList = new ArrayList<>();
    private List<FlowTaskOperatorEntity> operatorList = new ArrayList<>();
    private List<FlowTaskCirculateEntity> circulateList = new ArrayList<>();
    /**代办 (通知代办)*/
    private Boolean wait = true;
    /**同意*/
    private Boolean approve = false;
    /**拒绝*/
    private Boolean reject = false;
    /**抄送人*/
    private Boolean copy = false;
    /**结束 (通知发起人)*/
    private Boolean end = false;
    /**子流程通知*/
    private Boolean launch = false;
    /**拒绝发起节点*/
    private Boolean start = false;
    /**超时*/
    private Boolean overtime = false;
    /**提醒*/
    private Boolean notice = false;
}
