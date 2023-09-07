package org.openea.eap.extj.engine.model.flowtask.method;

import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.engine.entity.FlowTaskNodeEntity;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.nodejson.ChildNodeList;
import org.openea.eap.extj.engine.model.flowengine.FlowModel;
import lombok.Data;

import java.util.List;

/**
 * 
 */
@Data
public class TaskHandleIdStatus {
    /**
     * 审批类型（0：拒绝，1：同意）
     **/
    private Integer status;
    /**
     * 当前节点属性
     **/
    private ChildNodeList nodeModel;
    /**
     * 用户
     **/
    private UserInfo userInfo;
    /**
     * 审批对象
     **/
    private FlowModel flowModel;
    /**
     * 节点list
     **/
    private List<FlowTaskNodeEntity> taskNodeList;

}
