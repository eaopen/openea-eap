package org.openea.eap.extj.message.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.openea.eap.extj.base.entity.SuperBaseEntity;

import java.util.Date;

/**
 * 聊天内容
 *
 *
 */
@Data
@TableName("base_imcontent")
public class ImContentEntity extends SuperBaseEntity.SuperTBaseEntity<String> {

    /**
     * 发送者
     */
    @TableField("F_SENDUSERID")
    private String sendUserId;

    /**
     * 删除者标记
     */
    @TableField("F_SENDDELETEMARK")
    private String sendDeleteMark;

    /**
     * 发送时间
     */
    @TableField("F_SENDTIME")
    private Date sendTime;

    /**
     * 接收者
     */
    @TableField("F_RECEIVEUSERID")
    private String receiveUserId;

    /**
     * 删除标记
     */
    @TableField("F_DELETEMARK")
    private Integer deleteMark;

    /**
     * 接收时间
     */
    @TableField("F_RECEIVETIME")
    private Date receiveTime;

    /**
     * 内容
     */
    @TableField("F_CONTENT")
    private String content;

    /**
     * 内容
     */
    @TableField("F_CONTENTTYPE")
    private String contentType;

    /**
     * 状态
     */
    @TableField("F_STATE")
    private Integer state;

}
