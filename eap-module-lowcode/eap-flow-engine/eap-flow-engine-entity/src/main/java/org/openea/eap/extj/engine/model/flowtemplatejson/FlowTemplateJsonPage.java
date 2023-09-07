package org.openea.eap.extj.engine.model.flowtemplatejson;

import org.openea.eap.extj.base.PaginationTime;
import lombok.Data;


@Data
public class FlowTemplateJsonPage extends PaginationTime {
    private String templateId;
    private String groupId;
    private String flowId;
    private Integer enabledMark;
}
