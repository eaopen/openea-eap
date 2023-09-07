package org.openea.eap.extj.permission.model.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 *
 */
@Data
public class UserAllVO extends UserBaseVO{

    @Schema(description = "用户头像")
    private String headIcon;
    @Schema(description = "性别(1,男。2女)")
    private String gender;
    @Schema(description = "快速搜索")
    private String quickQuery;

}
