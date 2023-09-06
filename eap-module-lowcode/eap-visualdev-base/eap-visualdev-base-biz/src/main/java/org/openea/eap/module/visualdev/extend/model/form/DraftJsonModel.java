package org.openea.eap.module.visualdev.extend.model.form;

import lombok.Data;

@Data
public class DraftJsonModel {
    private Boolean required;
    private String filedId;
    private String filedName;
    private String jnpfKey;
    private boolean multiple;
}
