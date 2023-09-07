package org.openea.eap.extj.extend.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.openea.eap.extj.base.entity.SuperExtendEntity;

import java.util.Date;

@Data
@TableName("ext_emailreceive")
public class EmailReceiveEntity extends SuperExtendEntity.SuperExtendSortEntity<String> {

    /**
     * 类型
     */
    @TableField("F_TYPE")
    private Integer type;

    /**
     * 邮箱账户
     */
    @TableField(value = "F_MACCOUNT",fill= FieldFill.INSERT)
    private String mAccount;

    /**
     * F_MID
     */
    @TableField("F_MID")
    private String mId;

    /**
     * 发件人
     */
    @TableField("F_SENDER")
    private String sender;

    /**
     * 发件人名称
     */
    @TableField("F_SENDERNAME")
    private String senderName;

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
     * 阅读
     */
    @TableField("F_READ")
    private Integer isRead;

    /**
     * F_Date
     */
    @TableField("F_DATE")
    private Date fdate;

    /**
     * 星标
     */
    @TableField("F_STARRED")
    private Integer starred;

}