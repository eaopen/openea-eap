package org.openea.eap.extj.engine.model.flowtemplate;

import org.openea.eap.extj.engine.entity.FlowEngineVisibleEntity;
import org.openea.eap.extj.engine.entity.FlowTemplateEntity;
import org.openea.eap.extj.engine.entity.FlowTemplateJsonEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 *
 */
@Data
public class FlowExportModel {

    private FlowTemplateEntity templateEntity;
    private List<FlowTemplateJsonEntity> templateJsonEntity = new ArrayList<>();
    private List<FlowEngineVisibleEntity> visibleList = new ArrayList<>();

}
