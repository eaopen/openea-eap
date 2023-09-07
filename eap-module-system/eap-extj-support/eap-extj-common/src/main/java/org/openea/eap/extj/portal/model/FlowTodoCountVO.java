package org.openea.eap.extj.portal.model;

import lombok.Data;

@Data
public class FlowTodoCountVO {
    private Integer toBeReviewed;
    private Integer entrust;
    private Integer flowDone;
    private Integer flowCirculate;
}
