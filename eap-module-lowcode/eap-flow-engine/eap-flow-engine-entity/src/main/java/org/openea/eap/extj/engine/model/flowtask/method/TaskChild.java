package org.openea.eap.extj.engine.model.flowtask.method;

import org.openea.eap.extj.engine.entity.FlowTaskEntity;
import org.openea.eap.extj.engine.entity.FlowTaskNodeEntity;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.nodejson.ChildNodeList;
import org.openea.eap.extj.engine.model.flowbefore.FlowTemplateAllModel;
import org.openea.eap.extj.engine.model.flowengine.FlowModel;
import org.openea.eap.extj.engine.model.flowtask.FlowErrorModel;
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
public class TaskChild {
    private ChildNodeList childNode;
    private FlowTaskEntity flowTask;
    private List<FlowTaskNodeEntity> flowTaskNodeList = new ArrayList<>();
    private FlowTemplateAllModel templateAllModel;
    private FlowModel flowModel;
    private Boolean verify = true;
    private List<FlowErrorModel> errorList = new ArrayList<>();
}

