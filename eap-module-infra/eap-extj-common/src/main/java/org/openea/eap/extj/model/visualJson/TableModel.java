package org.openea.eap.extj.model.visualJson;

import lombok.Data;

import java.util.List;

@Data
public class TableModel {

    private String typeId;
    private String table;
    private String comment;
    private String tableKey;
    private String tableField;
    private String relationTable;
    private String relationField;
    private List<TableFields> fields;
    private String initName;
    private String tableTag;
}
