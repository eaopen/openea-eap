package org.openea.eap.module.system.controller.admin.user.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ImUserListVo{

    @Schema(description = "用户编号")
    private Long id;

    @Schema(description = "账号")
    private String account;

    @Schema(description = "名称")
    private String realName;

    @Schema(description = "用户头像")
    private String headIcon;

    @Schema(description = "部门")
    private String department;

}
