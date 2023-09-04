package org.openea.eap.extj.database.model.superQuery;

import lombok.Data;

@Data
public class ConditionJsonModel {

    private String field;
    private String fieldValue;
    private String symbol;
    private String tableName;
    private String jnpfKey;
    private String defaultValue;
    private String attr;
    private boolean formMultiple;
}
