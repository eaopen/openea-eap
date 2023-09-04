package org.openea.eap.extj.database.model.dbfield;

import lombok.Data;

@Data
public class DbFieldModelBase {

    protected String length;
    protected String dataType;
    protected String field;
    protected Boolean isPrimaryKey;
    protected String nullSign;
    protected Boolean isAutoIncrement;
    protected String comment;
    protected String defaultValue;
}
