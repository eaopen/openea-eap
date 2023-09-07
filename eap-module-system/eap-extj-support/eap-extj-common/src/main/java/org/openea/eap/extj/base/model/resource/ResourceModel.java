package org.openea.eap.extj.base.model.resource;

import lombok.Data;

/**
 *
 * 
 */
@Data
public class ResourceModel {
    private String id;
    private String fullName;
    private String enCode;
    private String conditionJson;
    private String conditionText;
    private Integer allData;
    private String moduleId;
}
