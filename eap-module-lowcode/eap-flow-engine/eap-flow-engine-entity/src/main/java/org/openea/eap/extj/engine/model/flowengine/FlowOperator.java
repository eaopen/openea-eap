package org.openea.eap.extj.engine.model.flowengine;

import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.engine.entity.FlowTaskEntity;
import org.openea.eap.extj.engine.entity.FlowTaskNodeEntity;
import org.openea.eap.extj.engine.entity.FlowTaskOperatorEntity;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.nodejson.ChildNodeList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowOperator {
    private UserInfo userInfo;
    private FlowModel flowModel;
    private FlowTaskEntity flowTask;
    private List<ChildNodeList> nodeList;
    private List<FlowTaskNodeEntity> taskNodeListAll;
    private List<FlowTaskOperatorEntity> operatorListAll;
    private boolean reject = false;
    private Map<String, List<String>> asyncTaskList= new HashMap<>();
    private Map<String, List<String>> nodeTaskIdList= new HashMap<>();
}
