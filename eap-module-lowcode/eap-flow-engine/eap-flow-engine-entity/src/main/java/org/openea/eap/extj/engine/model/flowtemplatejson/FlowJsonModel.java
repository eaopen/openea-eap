package org.openea.eap.extj.engine.model.flowtemplatejson;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class FlowJsonModel {
    private String id;
    private String flowId;
    private String fullName;
    //true 不能删除 false 能删除
    private Boolean isDelete = false;
    private Map<String,Object> flowTemplateJson = new HashMap<>();
}
