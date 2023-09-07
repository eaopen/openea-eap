package org.openea.eap.extj.base.model.column;

import lombok.Data;

/**
 *
 *
 */
@Data
public class ColumnModel {
    private String id;
    private String parentId;
    private String fullName;
    private String enCode;
    private String bindTable;
    private String bindTableName;
    private String moduleId;
}
