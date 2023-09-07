package org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析引擎
 *
 * 
 */
@Data
public class FlowAssignModel {
    /**
     * 节点编码
     */
    @Schema(description = "节点编码")
    private String nodeId;

    /**
     * 传递规则
     */
    @Schema(description = "传递规则")
    private List<RuleListModel> ruleList = new ArrayList<>();

}
