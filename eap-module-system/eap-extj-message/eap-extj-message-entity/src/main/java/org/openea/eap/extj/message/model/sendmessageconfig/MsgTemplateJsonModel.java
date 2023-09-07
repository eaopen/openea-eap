package org.openea.eap.extj.message.model.sendmessageconfig;

import lombok.Data;

/**
 * 解析引擎
 */
@Data
public class MsgTemplateJsonModel {

    public String field;
    public String fieldName;
    public String relationField;
    private String id;
    private Boolean isSubTable = false;
    private String msgTemplateId;

}
