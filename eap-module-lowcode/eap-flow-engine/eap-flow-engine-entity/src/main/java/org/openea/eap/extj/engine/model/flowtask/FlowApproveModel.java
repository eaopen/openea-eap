package org.openea.eap.extj.engine.model.flowtask;

import org.openea.eap.extj.engine.entity.FlowTaskEntity;
import org.openea.eap.extj.engine.entity.FlowTaskNodeEntity;
import org.openea.eap.extj.engine.entity.FlowTaskOperatorEntity;
import org.openea.eap.extj.engine.model.flowengine.FlowModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowApproveModel {
    private List<FlowTaskOperatorEntity> operatorList = new ArrayList<>();
    private List<FlowTaskNodeEntity> taskNodeList = new ArrayList<>();
    private FlowTaskEntity flowTask = new FlowTaskEntity();
    private FlowModel flowModel = new FlowModel();
    private boolean isSubmit = false;
}
