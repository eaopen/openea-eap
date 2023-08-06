package org.openea.eap.extj.message.entity;

import org.openea.eap.extj.base.entity.SuperBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 消息接收
 *
 *
 */
@Data
@TableName("base_messagereceive")
public class MessageReceiveEntity extends SuperBaseEntity.SuperTBaseEntity<String> {

    /**
     * 消息主键
     */
    @TableField("F_MESSAGEID")
    private String messageId;

    /**
     * 用户主键
     */
    @TableField("F_USERID")
    private String userId;

    /**
     * 是否阅读
     */
    @TableField("F_ISREAD")
    private Integer isRead;

    /**
     * 阅读时间
     */
    @TableField("F_READTIME")
    private Date readTime;

    /**
     * 阅读次数
     */
    @TableField("F_READCOUNT")
    private Integer readCount;

    /**
     * 站内信息
     */
    @TableField("F_BODYTEXT")
    private String bodyText;

}
