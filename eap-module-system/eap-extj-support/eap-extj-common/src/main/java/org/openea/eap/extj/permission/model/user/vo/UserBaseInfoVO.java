package org.openea.eap.extj.permission.model.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 *
 */
@Data
public class UserBaseInfoVO {
    @Schema(description = "主键")
    private String id;
    @Schema(description = "账户")
    private String account;
    @Schema(description = "户名")
    private String realName;
    @Schema(description = "部门")
    private String organize;
    @Schema(description = "公司名称")
    private String company;
    @Schema(description = "岗位")
    private String position;
    @Schema(description = "主管")
    private String manager;
    @Schema(description = "角色")
    private String roleId;
    @Schema(description = "注册时间")
    private Long creatorTime;
    @Schema(description = "上次登录时间")
    private Long prevLogTime;
    @Schema(description = "自我介绍")
    private String signature;
    @Schema(description = "性别")
    private String gender;
    @Schema(description = "民族")
    private String nation;
    @Schema(description = "籍贯")
    private String nativePlace;
    @Schema(description = "入职时间")
    private Long entryDate;
    @Schema(description = "证件类型")
    private String certificatesType;
    @Schema(description = "证件号码")
    private String certificatesNumber;
    @Schema(description = "学历")
    private String education;
    @Schema(description = "出生年月")
    private Long birthday;
    @Schema(description = "办公电话")
    private String telePhone;
    @Schema(description = "办公座机")
    private String landline;
    @Schema(description = "手机号码")
    private String mobilePhone;
    @Schema(description = "电子邮箱")
    private String email;
    @Schema(description = "紧急联系人")
    private String urgentContacts;
    @Schema(description = "紧急联系人电话")
    private String urgentTelePhone;
    @Schema(description = "通信地址")
    private String postalAddress;
    @Schema(description = "用户头像")
    private String avatar;
    @Schema(description = "主题")
    private String theme;
    @Schema(description = "语言")
    private String language;
}
