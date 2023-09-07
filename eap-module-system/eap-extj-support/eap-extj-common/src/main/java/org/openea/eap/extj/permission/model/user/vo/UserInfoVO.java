package org.openea.eap.extj.permission.model.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 *
 *
 */
@Data
public class UserInfoVO {

    @Schema(description = "id")
    private String id;
    @Schema(description = "账户")
    private String account;
    @Schema(description = "户名")
    private String realName;
    @Schema(description = "部门id")
    private String organizeId;
    @Schema(description = "组织id树")
    private List<LinkedList<String>> organizeIdTree;
    @Schema(description = "主管id")
    private String managerId;
    @Schema(description = "岗位id")
    private String positionId;
    @Schema(description = "角色id")
    private String roleId;
    @Schema(description = "备注")
    private String description;
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
    private String headIcon;
    @Schema(description = "状态")
    private Integer enabledMark;
    @Schema(description = "排序")
    private Long sortCode;
    @Schema(description = "分组id")
    private String groupId;

}
