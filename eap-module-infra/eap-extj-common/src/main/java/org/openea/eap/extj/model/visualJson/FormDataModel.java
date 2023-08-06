package org.openea.eap.extj.model.visualJson;

import lombok.Data;

import java.util.Arrays;

@Data
public class FormDataModel {
    private String areasName;
    private String className;
    private String serviceDirectory;
    private String module;
    private String subClassName;
    private String formRef;
    private String formModel;
    private String size;
    private String labelPosition;
    private Integer labelWidth;
    private String formRules;
    private String drawerWidth;
    private Integer gutter;
    private Boolean disabled;
    private String span;
    private Boolean formBtns;
    private Integer idGlobal;
    private String fields;
    private String popupType;
    private String fullScreenWidth;
    private String formStyle;
    private String generalWidth;
    private Boolean hasCancelBtn;
    private String cancelButtonText;
    private Boolean hasConfirmBtn;
    private String confirmButtonText;
    private Boolean hasPrintBtn;
    private String printButtonText;
    private String[] printId;
    private FieLdsModel children;
    private Integer primaryKeyPolicy = 1;
    private Boolean concurrencyLock = false;
    private String ruleList;
    private String ruleListApp;
    private Boolean logicalDelete = false;

    public void setPrimaryKeyPolicy(Integer primaryKeyPolicy) {
        if (primaryKeyPolicy == null) {
            this.primaryKeyPolicy = 1;
        } else {
            this.primaryKeyPolicy = primaryKeyPolicy;
        }
    }

}