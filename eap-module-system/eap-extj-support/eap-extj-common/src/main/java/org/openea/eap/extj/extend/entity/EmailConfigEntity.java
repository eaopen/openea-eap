package org.openea.eap.extj.extend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.openea.eap.extj.base.entity.SuperBaseEntity;

@Data
@TableName("ext_emailconfig")
public class EmailConfigEntity extends SuperBaseEntity.SuperCBaseEntity<String> {

    /**
     * POP3服务
     */
    @TableField("F_POP3HOST")
    private String pop3Host;

    /**
     * POP3端口
     */
    @TableField("F_POP3PORT")
    private Integer pop3Port;

    /**
     * SMTP服务
     */
    @TableField("F_SMTPHOST")
    private String smtpHost;

    /**
     * SMTP端口
     */
    @TableField("F_SMTPPORT")
    private Integer smtpPort;

    /**
     * 账户
     */
    @TableField("F_ACCOUNT")
    private String account;

    /**
     * 密码
     */
    @TableField("F_PASSWORD")
    private String password;

    /**
     * SSL登录
     */
    @TableField("F_SSL")
    private Integer emailSsl=0;

    /**
     * 发件人名称
     */
    @TableField("F_SENDERNAME")
    private String senderName;

    /**
     * 我的文件夹
     */
    @TableField("F_FOLDERJSON")
    private String folderJson;

    /**
     * 描述
     */
    @TableField("F_DESCRIPTION")
    private String description;

    /**
     * 排序码
     */
    @TableField("F_SORTCODE")
    private Long sortCode;

    /**
     * 有效标志
     */
    @TableField("F_ENABLEDMARK")
    private Integer enabledMark;

}
