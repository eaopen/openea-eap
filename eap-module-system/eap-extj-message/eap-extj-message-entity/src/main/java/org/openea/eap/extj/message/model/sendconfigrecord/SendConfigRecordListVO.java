package org.openea.eap.extj.message.model.sendconfigrecord;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;


@Data
public class SendConfigRecordListVO {
    private String id;


    /**
     * 发送配置id
     **/
    @JSONField(name = "sendConfigId")
    private String sendConfigId;

    /**
     * 消息来源
     **/
    @JSONField(name = "messageSource")
    private String messageSource;

    /**
     * 被引用id
     **/
    @JSONField(name = "usedId")
    private String usedId;

    /**
     * 创建时间
     **/
    @JSONField(name = "creatorTime")
    private Long creatorTime;

    /**
     * 创建人员
     **/
    @JSONField(name = "creatorUserId")
    private String creatorUserId;

    /**
     * 修改时间
     **/
    @JSONField(name = "lastModifyTime")
    private Long lastModifyTime;

    /**
     * 修改人员
     **/
    @JSONField(name = "lastModifyUserId")
    private String lastModifyUserId;


}