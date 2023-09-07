package org.openea.eap.extj.message.model.sendmessageconfig;

import lombok.Data;


@Data
public class SendConfigTestResultModel {


    /**
     * 消息类型
     **/
    private String messageType;

    /**
     * 是否成功
     **/
    private String isSuccess;

    /**
     * 失败原因
     **/
    private String result;


}
