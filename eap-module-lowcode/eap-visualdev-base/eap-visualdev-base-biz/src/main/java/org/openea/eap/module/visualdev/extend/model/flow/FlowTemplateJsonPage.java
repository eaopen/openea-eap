package org.openea.eap.module.visualdev.extend.model.flow;

import lombok.Data;
import org.openea.eap.extj.base.PaginationTime;

@Data
public class FlowTemplateJsonPage extends PaginationTime {
    private String templateId;
    private String groupId;
    private String flowId;
    private Integer enabledMark;
}