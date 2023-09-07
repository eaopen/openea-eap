package org.openea.eap.extj.extend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.openea.eap.extj.base.entity.SuperExtendEntity;

@Data
@TableName("ext_emailsend")
public class EmailSendEntity extends SuperExtendEntity.SuperExtendSortEntity<String> {

    /**
     * 类型 1-外部、0-内部
     */
    @TableField("F_TYPE")
    private Integer type;

    /**
     * 发件人
     */
    @TableField("F_SENDER")
    private String sender;

    /**
     * 收件人
     */
    @TableField("F_TO")
    private String recipient;

    /**
     * 抄送人
     */
    @TableField("F_CC")
    private String cc;

    /**
     * 密送人
     */
    @TableField("F_BCC")
    private String bcc;

    /**
     * 颜色
     */
    @TableField("F_COLOUR")
    private String colour;

    /**
     * 主题
     */
    @TableField("F_SUBJECT")
    private String subject;

    /**
     * 正文
     */
    @TableField("F_BODYTEXT")
    private String bodyText;

    /**
     * 附件
     */
    @TableField("F_ATTACHMENT")
    private String attachment;

    /**
     * 状态 -1-草稿、0-正在投递、1-投递成功
     */
    @TableField("F_STATE")
    private Integer state;

}