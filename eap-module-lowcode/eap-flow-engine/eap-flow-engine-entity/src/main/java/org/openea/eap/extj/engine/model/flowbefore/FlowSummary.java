package org.openea.eap.extj.engine.model.flowbefore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 *
 *
 *
 */
@Data
public class FlowSummary {
    @Schema(description = "主键")
    private String id;
    @Schema(description = "名称")
    private String fullName;
    @Schema(description = "意见")
    private String handleOpinion;
    @Schema(description = "用户")
    private String userName;
    @Schema(description = "时间")
    private Long handleTime;
    @Schema(description = "状态")
    private Integer handleStatus;
    @Schema(description = "流转操作人")
    private String operatorId;
    @Schema(description = "子流程")
    private List<FlowSummary> list;
    private String fileList;
    @Schema(description = "签名")
    private String signImg;
    @Schema(description = "头像")
    private String headIcon;
}
