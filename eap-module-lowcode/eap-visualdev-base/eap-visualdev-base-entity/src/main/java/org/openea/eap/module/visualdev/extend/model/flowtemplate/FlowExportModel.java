package org.openea.eap.module.visualdev.extend.model.flowtemplate;

import lombok.Data;
import org.openea.eap.module.visualdev.extend.entity.FlowEngineVisibleEntity;
import org.openea.eap.module.visualdev.extend.entity.FlowTemplateEntity;
import org.openea.eap.module.visualdev.extend.entity.FlowTemplateJsonEntity;

import java.util.ArrayList;
import java.util.List;

@Data
public class FlowExportModel {

    private FlowTemplateEntity templateEntity;
    private List<FlowTemplateJsonEntity> templateJsonEntity = new ArrayList<>();
    private List<FlowEngineVisibleEntity> visibleList = new ArrayList<>();

}