package org.openea.eap.extj.message.model.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class NoticeInfoVO {
    @Schema(description = "主键")
    private String id;
    @Schema(description = "标题")
    private String title;
    @Schema(description = "内容")
    private String bodyText;
    @Schema(description = "创建用户")
    private String creatorUser;
    @Schema(description = "修改时间")
    private Long lastModifyTime;
    @Schema(description = "接收人id集合")
    private String toUserIds;

    @Schema(description = "文件")
    private String files;

    /**
     * 发布人员
     */
    @Schema(description = "发布人员")
    private String releaseUser;

    /**
     * 发布时间
     */
    @Schema(description = "发布时间")
    private Long releaseTime;

    @Schema(description = "封面图片")
    private String coverImage;
    @Schema(description = "过期时间")
    private Long expirationTime;
    @Schema(description = "分类")
    private String category;
    @Schema(description = "提醒方式")
    private Integer remindCategory;
    @Schema(description = "发送配置")
    private String sendConfigId;

    @Schema(description = "摘要")
    private String excerpt;
}
