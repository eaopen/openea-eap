package org.openea.eap.extj.engine.model.flowengine;

import org.openea.eap.extj.engine.entity.FlowTaskNodeEntity;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.nodejson.ChildNodeList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *
 *
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlowDataModel {
    private ChildNodeList childNodeList;
    private List<FlowTaskNodeEntity> taskNodeList;
    private FlowModel flowModel;
    private Boolean isAssig = true;
    private Boolean isData = true;

}
