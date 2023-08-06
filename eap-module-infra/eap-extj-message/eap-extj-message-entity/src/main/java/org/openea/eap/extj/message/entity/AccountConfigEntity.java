package org.openea.eap.extj.message.entity;


import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.openea.eap.extj.base.entity.SuperExtendEntity;

/**
 * 账号配置表
 *
 */
@Data
@TableName("base_message_account_config")
public class AccountConfigEntity extends SuperExtendEntity<String> {

    @TableField("F_TYPE")
    private String type;

    @TableField("F_FULLNAME")
    private String fullName;

    @TableField("F_ENCODE")
    private String enCode;

    @TableField("F_ADDRESSORNAME")
    private String addressorName;

//    @TableField("F_SEND_EMAIL")
//
//    private String sendEmail;

    @TableField("F_SMTPSERVER")
    private String smtpServer;

    @TableField("F_SMTPPORT")
    private Integer smtpPort;

    @TableField("F_SSLLINK")
    private String sslLink;

    @TableField("F_SMTPUSER")
    private String smtpUser;

    @TableField("F_SMTPPASSWORD")
    private String smtpPassword;

    @TableField("F_CHANNEL")
    private String channel;

    @TableField("F_SMSSIGNATURE")
    private String smsSignature;

    @TableField(value = "F_APPID",fill = FieldFill.INSERT)
    private String appId;

    @TableField(value = "F_APPSECRET",fill = FieldFill.INSERT)
    private String appSecret;

    @TableField("F_ENDPOINT")
    private String endPoint;

    @TableField("F_SDKAPPID")
    private String sdkAppId;

    @TableField(value = "F_APPKEY",fill = FieldFill.INSERT)
    private String appKey;

    @TableField("F_ZONENAME")
    private String zoneName;

    @TableField("F_ZONEPARAM")
    private String zoneParam;

    @TableField("F_ENTERPRISEID")
    private String enterpriseId;

    @TableField("F_AGENTID")
    private String agentId;

    @TableField("F_WEBHOOKTYPE")
    private String webhookType;

    @TableField("F_WEBHOOKADDRESS")
    private String webhookAddress;

    @TableField("F_APPROVETYPE")
    private String approveType;

    @TableField("F_BEARER")
    private String bearer;

    @TableField("F_USERNAME")
    private String userName;

    @TableField("F_PASSWORD")
    private String password;

    @TableField("F_SORTCODE")
    private Integer sortCode;

}
