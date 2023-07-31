package org.openea.eap.module.visualdev.onlinedev.model;


import io.swagger.v3.oas.annotations.media.Schema;
import org.openea.eap.module.visualdev.engine.model.flowengine.FlowModel;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 *
 */
@Data
@Schema(description = "功能数据创建表单")
public class VisualdevModelDataCrForm extends FlowModel{
    @Schema(description = "数据内容")
    private String data;
    @Schema(description = "状态")
    private String status;
    @Schema(description = "流程候选人列表")
    private Map<String, List<String>> candidateList;
    @Schema(description = "流程紧急度")
    private Integer flowUrgent = 1;
}
