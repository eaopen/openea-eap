package org.openea.eap.extj.model.visualJson.analysis;

import lombok.Data;

@Data
public class FormAllModel {

    private String jnpfKey;
    private String isEnd = "0";
    private FormColumnModel formColumnModel;
    private FormColumnTableModel childList;
    private FormModel formModel;
    private FormMastTableModel formMastTableModel;

}
