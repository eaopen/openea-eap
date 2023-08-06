package org.openea.eap.extj.model.visualJson.analysis;

import lombok.Data;

@Data
public class FormMastTableModel {

    private String table;
    private String field;
    private String vModel;
    private FormColumnModel mastTable;
}
