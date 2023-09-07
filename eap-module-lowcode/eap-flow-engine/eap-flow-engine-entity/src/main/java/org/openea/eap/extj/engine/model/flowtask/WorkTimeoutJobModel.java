package org.openea.eap.extj.engine.model.flowtask;

import org.openea.eap.extj.engine.entity.FlowTaskOperatorEntity;
import org.openea.eap.extj.engine.model.flowengine.FlowModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流程设计
 *
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkTimeoutJobModel {

    private String tenantId;
    private String tenantDbConnectionString;
    private boolean isAssignDataSource;


    private FlowModel flowModel;
    private String taskId;
    private String taskNodeId;
    private String taskNodeOperatorId;
    private FlowTaskOperatorEntity operatorEntity;
    private Integer counter;
    private Integer overtimeNum;
    private boolean isSuspend= false;


}
