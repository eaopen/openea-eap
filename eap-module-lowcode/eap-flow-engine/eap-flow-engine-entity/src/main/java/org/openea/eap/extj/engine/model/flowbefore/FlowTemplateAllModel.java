package org.openea.eap.extj.engine.model.flowbefore;

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
public class FlowTemplateAllModel {
    private FlowTemplateJsonEntity templateJson = new FlowTemplateJsonEntity();
    private FlowTemplateEntity template = new FlowTemplateEntity();
    private List<FlowTemplateJsonEntity> templateJsonList = new ArrayList<>();
}
