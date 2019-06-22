package org.openea.eapboot.common.exception;

import lombok.Data;

/**
 */
@Data
public class EapbootException extends RuntimeException {

    private String msg;

    public EapbootException(String msg){
        super(msg);
        this.msg = msg;
    }
}
