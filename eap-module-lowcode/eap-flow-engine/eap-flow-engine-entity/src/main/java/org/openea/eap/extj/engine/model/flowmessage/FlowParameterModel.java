package org.openea.eap.extj.engine.model.flowmessage;

import lombok.Data;

import java.util.Map;

/**
 * 事件对象
 *
 * 
 */
@Data
public class FlowParameterModel {
    private String interId;
    private Map<String, String> parameterMap;
}
