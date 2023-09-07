package org.openea.eap.extj.engine.model.flowtask;

import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.engine.entity.FlowTaskEntity;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.nodejson.ChildNodeList;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.nodejson.ConditionList;
import org.openea.eap.extj.permission.entity.UserEntity;
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
public class FlowConditionModel {
    private String data;
    private String nodeId;
    private UserInfo userInfo;
    private UserEntity userEntity;
    private FlowTaskEntity flowTaskEntity;
    private List<ChildNodeList> childNodeListAll;
    private List<ConditionList> conditionListAll;
}
