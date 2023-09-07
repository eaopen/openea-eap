package org.openea.eap.extj.permission.model.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 *
 */
@Data
public class UserListVO {

    @Schema(description = "主键")
    private String id;
    @Schema(description = "账号")
    private String account;
    @Schema(description = "姓名")
    private String realName;
    @Schema(description = "性别")
    private String gender;
    @Schema(description = "手机")
    private String mobilePhone;
    @Schema(description = "部门")
    private String organize;
    @Schema(description = "说明")
    private String description;
    @Schema(description = "状态")
    private Integer enabledMark;
    @Schema(description = "添加时间",example = "1")
    private Long creatorTime;
    @Schema(description = "排序")
    private Long sortCode;
    // 是否显示修改选项（暂时停用）
    // @Schema(description = "是否管理员")
    // private Integer isAdministrator;
//    @Schema(description = "锁定标志")
//    private Integer lockMark;
    @Schema(description = "头像")
    private String headIcon;
}
