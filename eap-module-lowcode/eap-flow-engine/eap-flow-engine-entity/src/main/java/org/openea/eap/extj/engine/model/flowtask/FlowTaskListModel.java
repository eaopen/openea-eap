package org.openea.eap.extj.engine.model.flowtask;

import lombok.Data;

import java.util.Date;


@Data
public class FlowTaskListModel {
    private String id;
    private String processId;
    private String enCode;
    private String fullName;
    private Integer flowUrgent;
    private String flowId;
    private String flowCode;
    private String flowName;
    private String flowCategory;
    private String flowType;
    private Date startTime;
    private Date endTime;
    private String thisStep;
    private String thisStepId;
    private Integer status;
    private Integer completion;
    private String creatorUserId;
    private Date creatorTime;
    private String handleId;
    private String nodeName;
    private String lastModifyUserId;
    private Date lastModifyTime;
    private String approversProperties;
    private String description;
    private String flowVersion;
    private Integer taskStatus;
    private String sortCode;
    private String counterSign;
    private String templateId;
    private String delegateUser;
}
