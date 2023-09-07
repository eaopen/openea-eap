package org.openea.eap.extj.engine.model.flowdelegate;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 流程设计
 *
 * 
 */
@Data
@Accessors(chain = true)
public class FlowDdelegateSelectModel {

    private String userId;

    private String toUserId;
    private String type;

    private Long startTime;

    private Long endTime;

    private String flowId;
}
