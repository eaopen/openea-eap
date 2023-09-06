package org.openea.eap.extj.exception;

/**
 * 流程异常封装
 * 
 */
public class WorkFlowException extends Exception {

    private Integer code = 400;

    public WorkFlowException(Integer code,String message) {
        super(message);
        this.code = code;
    }

    public WorkFlowException(String message) {
        super(message);
    }

    public Integer getCode() {
        return code;
    }
}
