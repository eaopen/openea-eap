package org.openea.eap.extj.permission.model.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * IM获取用户接口
 *
 *
 */
@Data
public class ImUserListVo extends UserBaseVO{

    @Schema(description = "用户头像")
    private String headIcon;
    @Schema(description = "部门")
    private String department;

}
