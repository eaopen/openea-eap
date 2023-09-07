package org.openea.eap.extj.engine.model.flowengine.shuntjson.nodejson;

import org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.ProperCond;
import lombok.Data;

import java.util.List;

/**
 * 解析引擎
 *
 * 
 */
@Data
public class ConditionList {
    /**条件**/
    private List<ProperCond> conditions;
    /**条件节点id**/
    private String nodeId;
    /**上一节点id**/
    private String prevId;
    /**1.先判断分流节点 2.在判断孩子节点 3.最后获取子节点**/
    /**判断是否有分流节点**/
    private Boolean flow = false;
    /**判断是否有选择节点**/
    private Boolean branchFlow = false;
    /**分流的节点id**/
    private String flowId;
    /**判断是否有子节点**/
    private Boolean child;
    /**条件成功id**/
    private String childNodeId;
    /**子节点id**/
    private String firstId;
    /**判断是否其他条件**/
    private Boolean isDefault;
    /**名称**/
    private String title;
}
