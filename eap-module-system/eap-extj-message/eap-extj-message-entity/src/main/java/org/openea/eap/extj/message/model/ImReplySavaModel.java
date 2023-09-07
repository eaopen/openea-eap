package org.openea.eap.extj.message.model;

import lombok.Data;

import java.util.Date;


@Data
public class ImReplySavaModel {

    private String userId;

    private String receiveUserId;

    private Date receiveTime;

    public ImReplySavaModel(String userId, String receiveUserId, Date receiveTime) {
        this.userId = userId;
        this.receiveUserId = receiveUserId;
        this.receiveTime = receiveTime;
    }
}
