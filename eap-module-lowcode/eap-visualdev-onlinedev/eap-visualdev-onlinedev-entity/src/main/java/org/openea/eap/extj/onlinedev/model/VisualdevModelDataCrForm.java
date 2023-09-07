package org.openea.eap.extj.onlinedev.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.openea.eap.extj.engine.model.flowengine.FlowModel;

import java.util.List;
import java.util.Map;
@Data
@ApiModel("功能数据创建表单")
public class VisualdevModelDataCrForm extends FlowModel {
    @ApiModelProperty("数据内容")
    private String data;
    @ApiModelProperty("状态")
    private String status;
    @ApiModelProperty("流程候选人列表")
    private Map<String, List<String>> candidateList;
    @ApiModelProperty("流程紧急度")
    private Integer flowUrgent = 1;
}
