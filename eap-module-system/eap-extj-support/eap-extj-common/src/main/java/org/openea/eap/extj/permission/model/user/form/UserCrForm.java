package org.openea.eap.extj.permission.model.user.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 *
 */
@Data
public class UserCrForm {

    @NotBlank(message = "必填")
    @Schema(description = "账户")
    private String account;

    @NotBlank(message = "必填")
    @Schema(description = "户名")
    private String realName;

    @NotBlank(message = "必填")
    @Schema(description = "部门")
    private String organizeId;

    @Schema(description = "主管")
    private String managerId;

    @Schema(description = "岗位")
    private String positionId;

    @Schema(description = "角色")
    private String roleId;

    private String description;

    @NotNull(message = "性别不能为空")
    @Schema(description = "性别")
    private int gender;

    @Schema(description = "民族")
    private String nation;

    @Schema(description = "籍贯")
    private String nativePlace;

    @Schema(description = "证件类型")
    private String certificatesType;

    @Schema(description = "证件号码")
    private String certificatesNumber;

    @Schema(description = "文化程度")
    private String education;

    @Schema(description = "生日")
    private String birthday;

    @Schema(description = "电话")
    private String telePhone;

    @Schema(description = "Landline")
    private String landline;

    @Schema(description = "手机")
    private String mobilePhone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "UrgentContacts")
    private String urgentContacts;

    @Schema(description = "紧急电话")
    private String urgentTelePhone;

    @Schema(description = "通讯地址")
    private String postalAddress;

    @Schema(description = "头像")
    private String headIcon;

    @Schema(description = "排序")
    private Long sortCode;

    @Schema(description = "入职日期")
    private long entryDate;

    @Schema(description = "状态")
    private Integer enabledMark;

    @Schema(description = "分组id")
    private String groupId;
}
