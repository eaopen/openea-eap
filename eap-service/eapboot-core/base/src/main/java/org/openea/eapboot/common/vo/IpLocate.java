package org.openea.eapboot.common.vo;

import lombok.Data;

import java.io.Serializable;

/**
 */
@Data
public class IpLocate implements Serializable {

    private String retCode;

    private City result;
}

