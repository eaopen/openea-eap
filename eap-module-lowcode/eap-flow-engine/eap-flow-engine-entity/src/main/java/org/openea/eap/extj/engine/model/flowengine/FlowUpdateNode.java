package org.openea.eap.extj.engine.model.flowengine;

import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.engine.entity.FlowTaskEntity;
import org.openea.eap.extj.engine.entity.FlowTaskNodeEntity;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.ChildNode;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.nodejson.ChildNodeList;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.nodejson.ConditionList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowUpdateNode {
    private UserInfo userInfo;
    private FlowTaskEntity flowTask;
    private ChildNode childNodeAll;
    private List<ChildNodeList> nodeListAll;
    private List<ConditionList> conditionListAll;
    private List<FlowTaskNodeEntity> taskNodeList;
    private boolean isSubmit = false;
}
