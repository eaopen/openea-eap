package org.openea.eap.extj.model;

import lombok.Data;
@Data
public class DbTableConModel {
    private String id;
    private String table;
    private String newTable;
    private String tableName;
    private String size;
    private Integer sum;
    private String description;
    private String primaryKey;
    private String dataSourceId;

}