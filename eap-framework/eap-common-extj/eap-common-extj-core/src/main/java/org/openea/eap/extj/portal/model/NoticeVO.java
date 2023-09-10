package org.openea.eap.extj.portal.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class NoticeVO {
   private String id;
   @JSONField(name="title")
   private String fullName;
   private Long creatorTime;
   @Schema(description = "创建用户")
   private String creatorUser;

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

   /**
    * 消息类型
    */
   @Schema(description = "分类")
   private String category;

   @Schema(description = "封面图片")
   private String coverImage;

   @Schema(description = "摘要")
   private String excerpt;

   @JsonIgnore
   @Schema(description = "修改时间")
   private Long lastModifyTime;

   @JsonIgnore
   @Schema(description = "修改用户")
   private String lastModifyUserId;
}
