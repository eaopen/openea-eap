package org.openea.eap.extj.engine.model.flowengine;

import lombok.Data;

/**
 *
 *
 *
 */
@Data
public class FlowTaskRejectModel {
    private String id;
    private String thisStep;
    private String thisStepId;
    private Integer status;
    private String nodeNext;
    private Integer completion;
}
