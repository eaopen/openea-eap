package org.openea.eap.extj.model.visualJson;

import lombok.Data;

/**
 *
 *
 */
@Data
public class TemplateJsonModel {
    private String fieldName;
    private String field;
    private String defaultValue;
    private String jnpfKey;
    private String dataType;
    private String id;
    private String required;
    private String relationField;
}
