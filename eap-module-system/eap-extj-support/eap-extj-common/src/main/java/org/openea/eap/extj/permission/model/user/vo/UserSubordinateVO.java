package org.openea.eap.extj.permission.model.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 *
 *
 */
@Data
@Builder
public class UserSubordinateVO {
    private String id;
    @Schema(description = "头像")
    private String avatar;
    @Schema(description = "用户名")
    private String userName;
    @Schema(description = "部门")
    private String department;
    @Schema(description = "岗位")
    private String position;

    @Schema(description = "是否显示下级按钮")
    private Boolean isLeaf;
}
