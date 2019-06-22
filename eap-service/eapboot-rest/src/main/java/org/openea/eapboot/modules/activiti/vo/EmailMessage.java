package org.openea.eapboot.modules.activiti.vo;

import lombok.Data;

import java.io.Serializable;

/**
 */
@Data
public class EmailMessage implements Serializable {

    private String username;

    private String content;

    private String fullUrl;
}
