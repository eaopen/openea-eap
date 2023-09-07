package org.openea.eap.extj.message.entity;

import org.openea.eap.extj.base.entity.SuperBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 聊天会话表
 *
 *
 */
@Data
@TableName("base_imreply")
public class ImReplyEntity extends SuperBaseEntity.SuperTBaseEntity<String> {

    /**
     * 发送者
     */
    @TableField("F_USERID")
    private String userId;

    /**
     * 接收者
     */
    @TableField("F_RECEIVEUSERID")
    private String receiveUserId;

    /**
     * 发送时间
     */
    @TableField("F_RECEIVETIME")
    private Date receiveTime;

    /**
     * 删除者标记
     */
    @TableField("F_IMREPLYSENDDELETEMARK")
    private String imreplySendDeleteMark;

    /**
     * 删除标记
     */
    @TableField("F_IMREPLYDELETEMARK")
    private int imreplyDeleteMark;

}
