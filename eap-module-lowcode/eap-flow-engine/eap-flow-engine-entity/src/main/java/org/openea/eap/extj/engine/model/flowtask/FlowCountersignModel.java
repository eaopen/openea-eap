package org.openea.eap.extj.engine.model.flowtask;

import org.openea.eap.extj.engine.entity.FlowTaskOperatorEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
@Data
public class FlowCountersignModel {
    private String taskNodeId;
    private List<FlowTaskOperatorEntity> operatorList = new ArrayList<>();
    private Boolean fixed = false;
    private double pass = 100;
    private List<FlowTaskOperatorEntity> passNumList = new ArrayList<>();
}
