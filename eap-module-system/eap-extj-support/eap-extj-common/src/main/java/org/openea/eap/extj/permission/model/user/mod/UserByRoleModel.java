package org.openea.eap.extj.permission.model.user.mod;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 */
@Data
public class UserByRoleModel implements Serializable {
    /**
     * 关键字
     */
    @Schema(description = "关键字")
    private String keyword;

    /**
     * 组织id
     */
    @Schema(description = "组织id")
    private String organizeId;

    /**
     * 角色id
     */
    @Schema(description = "角色id")
    private String roleId;

}
