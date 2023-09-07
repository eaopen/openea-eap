package org.openea.eap.extj.engine.model.flowengine.shuntjson.nodejson;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析引擎
 *
 *
 */
@Data
public class Custom {
    private String type;
    /**当前节点id**/
    private String nodeId;
    /**上一节点id,可能是条件的节点也有可能是父类节点**/
    private String prevId;
    /**0，外层节点，1.里层节点**/
    private String num;
    /**判断是否有分流节点**/
    private Boolean flow = false;
    /**判断是否有选择节点**/
    private Boolean branchFlow = false;
    /**分流的节点id**/
    private String flowId;
    /** true，选择childNode,false 选择firstId**/
    private Boolean child;
    /** 该节点的子节点**/
    private String childNode;
    /**最外层的child节点**/
    private String firstId;
    /**同步子流程任务id**/
    private List<String> taskId = new ArrayList<>();
    /**异步子流程任务id**/
    private List<String> asyncTaskList = new ArrayList<>();
}
