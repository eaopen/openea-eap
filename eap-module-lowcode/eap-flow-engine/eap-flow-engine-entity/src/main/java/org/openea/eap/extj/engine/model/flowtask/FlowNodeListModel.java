package org.openea.eap.extj.engine.model.flowtask;

import org.openea.eap.extj.engine.entity.FlowTaskNodeEntity;
import org.openea.eap.extj.engine.model.flowengine.FlowModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlowNodeListModel {
    private List<FlowTaskNodeEntity> dataAll = new ArrayList<>();
    private FlowModel flowModel = new FlowModel();
    private Boolean isAdd = false;
    private FlowTaskNodeEntity taskNode = new FlowTaskNodeEntity();
    private Long num = 1L;
}
