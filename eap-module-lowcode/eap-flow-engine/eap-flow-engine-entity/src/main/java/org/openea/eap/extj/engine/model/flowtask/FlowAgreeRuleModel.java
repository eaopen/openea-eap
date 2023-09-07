package org.openea.eap.extj.engine.model.flowtask;

import org.openea.eap.extj.engine.entity.FlowTaskEntity;
import org.openea.eap.extj.engine.entity.FlowTaskNodeEntity;
import org.openea.eap.extj.engine.entity.FlowTaskOperatorEntity;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.nodejson.ChildNodeList;
import org.openea.eap.extj.engine.model.flowtask.method.TaskOperatoUser;
import org.openea.eap.extj.permission.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowAgreeRuleModel {
    private List<FlowTaskOperatorEntity> operatorListAll = new ArrayList<>();
    private TaskOperatoUser taskOperatoUser;
    private FlowTaskEntity flowTask;
    private Boolean reject = false;
    private List<UserEntity> userName = new ArrayList<>();
    private ChildNodeList childNode;
    private List<FlowTaskNodeEntity> taskNodeList = new ArrayList<>();
}
