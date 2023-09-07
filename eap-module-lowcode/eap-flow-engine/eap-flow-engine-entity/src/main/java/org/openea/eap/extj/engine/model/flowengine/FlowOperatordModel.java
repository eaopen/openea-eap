package org.openea.eap.extj.engine.model.flowengine;

import org.openea.eap.extj.engine.entity.FlowTaskOperatorEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowOperatordModel {
    /** 审批状态 */
    private Integer status;
    /** 审批原因 */
    private FlowModel flowModel;
    /** 审核人 */
    private String userId;
    /** 节点对象 */
    private FlowTaskOperatorEntity operator;
    /** 流转操作人 */
    private String operatorId;
    /** 自动审批 */
    private Boolean voluntarily = false;
}
