package org.openea.eap.extj.model.visualJson.analysis;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FormColumnTableModel {

    private String tableModel;
    private String tableName;
    private String label;
    private String tipLabel;
    private Integer span;
    private boolean showTitle;
    private String actionText;
    private List<FormColumnModel> childList;
    private String fieLdsModel;
    private Boolean showSummary;
    private String summaryField;
    private String summaryFieldName;
    private Integer addType = 0;
    private String addTableConf;
    private boolean app = true;
    private boolean pc = true;
    private String visibility;
    private boolean required = false;
    private String aliasClassName;
    private boolean thousands = false;
    private List<String> thousandsField = new ArrayList();

}
