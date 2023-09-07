package org.openea.eap.extj.engine.model.flowengine;

import lombok.Data;

/**
 *
 *
 *
 */
@Data
public class FlowTaskNodeRejectModel {
    private String id;
    private String nodeCode;
    private String nodeName;
    private String completion;
    private Integer state;
    private String nodeNext;
}
