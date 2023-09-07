package org.openea.eap.extj.model.visualJson.analysis;

import lombok.Data;
import org.openea.eap.extj.model.visualJson.analysis.FormColumnModel;

@Data
public class FormMastTableModel {
    /**
     * 表名
     */
    private String table;
    /**
     * 字段
     */
    private String field;
    /**
     * 原始字段
     */
    private String vModel;

    private FormColumnModel mastTable;
}
