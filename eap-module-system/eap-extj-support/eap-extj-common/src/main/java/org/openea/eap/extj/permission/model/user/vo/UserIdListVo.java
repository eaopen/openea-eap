package org.openea.eap.extj.permission.model.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 *
 */
@Data
public class UserIdListVo extends UserBaseVO{

    /**
     * 前端协议字段，以后将改回realName
     */
    @Schema(description = "名称")
    private String fullName;

    @Schema(description = "头像")
    private String headIcon;

    @Schema(description = "性别")
    private Integer gender;

    @Schema(description = "手机号")
    private String mobilePhone;

    @Schema(description = "组织")
    private String organize;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "类型")
    private String type;


    @Schema(description = "组织id树")
    private List<String> organizeIds;

}
