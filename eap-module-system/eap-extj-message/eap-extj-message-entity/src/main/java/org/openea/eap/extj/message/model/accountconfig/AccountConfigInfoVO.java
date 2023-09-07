package org.openea.eap.extj.message.model.accountconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class AccountConfigInfoVO {
    /**
     * 主键
     **/
    @Schema(description = "主键")
    @JsonProperty("id")
    private String id;

    /**
     * 名称
     **/
    @Schema(description = "名称")
    @JsonProperty("fullName")
    private String fullName;

    /**
     * 配置类型
     **/
    @Schema(description = "配置类型")
    @JsonProperty("type")
    private String type;

    /**
     * 编码
     **/
    @Schema(description = "编码")
    @JsonProperty("enCode")
    private String enCode;

    /**
     * 发件人昵称
     **/
    @Schema(description = "发件人昵称")
    @JsonProperty("addressorName")
    private String addressorName;

//        /** 发件人邮箱 **/
//        @JsonProperty("sendEmail")
//        private String sendEmail;

    /**
     * SMTP服务器
     **/
    @Schema(description = "SMTP服务器")
    @JsonProperty("smtpServer")
    private String smtpServer;

    /**
     * SMTP端口
     **/
    @Schema(description = "SMTP端口")
    @JsonProperty("smtpPort")
    private Integer smtpPort;

    /**
     * SSL安全链接
     **/
    @Schema(description = "SSL安全链接")
    @JsonProperty("sslLink")
    private Integer sslLink;

    /**
     * SMTP用户名
     **/
    @Schema(description = "SMTP用户名")
    @JsonProperty("smtpUser")
    private String smtpUser;

    /**
     * SMTP密码
     **/
    @Schema(description = "SMTP密码")
    @JsonProperty("smtpPassword")
    private String smtpPassword;

    /**
     * 渠道
     **/
    @Schema(description = "渠道")
    @JsonProperty("channel")
    private Integer channel;

    /**
     * 短信签名
     **/
    @Schema(description = "短信签名")
    @JsonProperty("smsSignature")
    private String smsSignature;

    /**
     * 应用ID
     **/
    @Schema(description = "应用ID")
    @JsonProperty("appId")
    private String appId;

    /**
     * 应用Secret
     **/
    @Schema(description = "应用Secret")
    @JsonProperty("appSecret")
    private String appSecret;

    /**
     * EndPoint（阿里云）
     **/
    @Schema(description = "EndPoint（阿里云）")
    @JsonProperty("endPoint")
    private String endpoint;

    /**
     * SDK AppID（腾讯云）
     **/
    @Schema(description = "SDK AppID（腾讯云）")
    @JsonProperty("sdkAppId")
    private String sdkAppId;

    /**
     * AppKey（腾讯云）
     **/
    @Schema(description = "AppKey（腾讯云）")
    @JsonProperty("appKey")
    private String appKey;

    /**
     * 地域域名（腾讯云）
     **/
    @Schema(description = "地域域名（腾讯云）")
    @JsonProperty("zoneName")
    private String zoneName;

    /**
     * 地域参数（腾讯云）
     **/
    @Schema(description = "地域参数（腾讯云）")
    @JsonProperty("zoneParam")
    private String zoneParam;

    /**
     * 企业id
     **/
    @Schema(description = "企业id")
    @JsonProperty("enterpriseId")
    private String enterpriseId;

    /**
     * AgentID
     **/
    @Schema(description = "AgentID")
    @JsonProperty("agentId")
    private String agentId;

    /**
     * WebHook类型
     **/
    @Schema(description = "WebHook类型")
    @JsonProperty("webhookType")
    private String webhookType;

    /**
     * WebHook地址
     **/
    @Schema(description = "WebHook地址")
    @JsonProperty("webhookAddress")
    private String webhookAddress;

    /**
     * 认证类型
     **/
    @Schema(description = "认证类型")
    @JsonProperty("approveType")
    private String approveType;

    /**
     * bearer令牌
     **/
    @Schema(description = "bearer令牌")
    @JsonProperty("bearer")
    private String bearer;

    /**
     * 用户名（基本认证）
     **/
    @Schema(description = "用户名（基本认证）")
    @JsonProperty("userName")
    private String userName;

    /**
     * 密码（基本认证）
     **/
    @Schema(description = "密码（基本认证）")
    @JsonProperty("password")
    private String password;

    /**
     * 排序
     **/
    @Schema(description = "排序")
    @JsonProperty("sortCode")
    private Integer sortCode;

    /**
     * 状态
     **/
    @Schema(description = "状态")
    @JsonProperty("enabledMark")
    private Integer enabledMark;

    /**
     * 说明
     **/
    @Schema(description = "说明")
    @JsonProperty("description")
    private String description;

//        /** 创建时间 **/
////        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
//        @JsonProperty("creatorTime")
//        private Long  creatorTime;
//
//        /** 创建用户 **/
//        @JsonProperty("creatorUserId")
//        private String creatorUserId;
//
//        /** 修改时间 **/
////        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
//        @JsonProperty("lastModifyTime")
//        private Long  lastModifyTime;
//
//        /** 修改用户 **/
//        @JsonProperty("lastModifyUserId")
//        private String lastModifyUserId;

}