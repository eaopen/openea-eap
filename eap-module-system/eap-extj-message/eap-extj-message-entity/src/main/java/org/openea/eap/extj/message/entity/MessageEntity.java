package org.openea.eap.extj.message.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.openea.eap.extj.base.entity.SuperBaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 消息实例
 *
 *
 */
@Data
@TableName("base_message")
public class MessageEntity extends SuperBaseEntity.SuperTBaseEntity<String> {

    /**
     * 类别
     */
    @TableField("F_TYPE")
    private Integer type;

    /**
     * 流程类别 1.流程 2.委托
     */
    @TableField("F_FLOWTYPE")
    private Integer flowType;

    /**
     * 新标题
     */
    @TableField("F_DEFAULTTITLE")
    private String defaultTitle;

    /**
     * 标题
     */
    @TableField("F_TITLE")
    private String title;

    /**
     * 正文
     */
    @TableField("F_BODYTEXT")
    private String bodyText;

    /**
     * 优先
     */
    @TableField("F_PRIORITYLEVEL")
    private Integer priorityLevel;

    /**
     * 收件用户
     */
    @TableField("F_TOUSERIDS")
    private String toUserIds;

    /**
     * 附件
     */
    @TableField("F_Files")
    private String files;

    /**
     * 是否阅读
     */
    @TableField("F_ISREAD")
    private Integer isRead;

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

    /**
     * 创建时间
     */
    @TableField(value = "F_CREATORTIME",fill = FieldFill.INSERT)
    private Date creatorTime;

    /**
     * 创建用户
     */
    @TableField(value = "F_CREATORUSERID",fill = FieldFill.INSERT)
    private String creatorUser;

    /**
     * 修改时间
     */
    @TableField(value = "F_LASTMODIFYTIME", updateStrategy = FieldStrategy.IGNORED)
    private Date lastModifyTime;

    /**
     * 修改用户
     */
    @TableField(value = "F_LASTMODIFYUSERID", updateStrategy = FieldStrategy.IGNORED)
    private String lastModifyUserId;

    /**
     * 删除标志
     */
    @TableField("F_DELETEMARK")
    private Integer deleteMark;

    /**
     * 删除时间
     */
    @TableField("F_DELETETIME")
    private Date deleteTime;

    /**
     * 删除用户
     */
    @TableField("F_DELETEUSERID")
    private String deleteUserId;


    /**
     * 封面图片
     */
    @TableField("F_COVERIMAGE")
    private String coverImage;

    /**
     * 过期时间
     */
    @TableField("F_EXPIRATIONTIME")
    private Date expirationTime;

    /**
     * 分类 1-公告 2-通知
     */
    @TableField("F_CATEGORY")
    private String category;

    /**
     * 提醒方式 1-站内信 2-自定义 3-不通知
     */
    @TableField("F_REMINDCATEGORY")
    private Integer remindCategory;

    /**
     * 发送配置
     */
    @TableField("F_SENDCONFIGID")
    private String sendConfigId;

    /**
     * 摘要
     */
    @TableField("F_EXCERPT")
    private String excerpt;
}
