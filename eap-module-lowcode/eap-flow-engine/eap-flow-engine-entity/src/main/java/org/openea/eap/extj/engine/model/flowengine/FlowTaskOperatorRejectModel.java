package org.openea.eap.extj.engine.model.flowengine;

import lombok.Data;

import java.util.Date;

/**
 * 流程设计
 *
 *
 */
@Data
public class FlowTaskOperatorRejectModel {
    private String id;
    private Integer state;
    private Integer handleStatus;
    private Date handleTime;
//    private Integer completion;
    private String draftData;
}
