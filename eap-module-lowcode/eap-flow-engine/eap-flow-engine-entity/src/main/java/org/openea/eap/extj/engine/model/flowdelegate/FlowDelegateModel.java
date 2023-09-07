package org.openea.eap.extj.engine.model.flowdelegate;

import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.engine.entity.FlowTaskEntity;
import org.openea.eap.extj.engine.model.flowbefore.FlowTemplateAllModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class FlowDelegateModel {
    //true 委托 false 审批
    private Boolean delegate = true;
    //0.发起 1.审批 2.结束
    private String type = "0";
    private List<String> toUserIds = new ArrayList<>();
    private UserInfo userInfo = new UserInfo();
    private FlowTaskEntity flowTask = new FlowTaskEntity();
    private FlowTemplateAllModel templateAllModel = new FlowTemplateAllModel();
    //审批是否要发送消息
    private Boolean approve = true;
}
