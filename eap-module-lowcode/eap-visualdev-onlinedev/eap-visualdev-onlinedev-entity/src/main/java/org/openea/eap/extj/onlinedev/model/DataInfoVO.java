package org.openea.eap.extj.onlinedev.model;

import lombok.Data;


@Data
public class DataInfoVO {
    private String formData;
    private String columnData;
    private String appColumnData;
    private String webType;
    private String flowTemplateJson;
    private String flowEnCode;
    private String flowId;
    private String fullName;
    private Integer enableFlow;
}
