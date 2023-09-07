package org.openea.eap.extj.model.form;

import lombok.Data;

@Data
public class DraftJsonModel {
    private Boolean required;
    private String filedId;
    private String filedName;
    private String jnpfKey;
    private boolean multiple;
}
