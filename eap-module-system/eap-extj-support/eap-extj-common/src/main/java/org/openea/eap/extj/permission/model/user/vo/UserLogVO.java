package org.openea.eap.extj.permission.model.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 *
 */
@Data
public class UserLogVO {
    @Schema(description = "登录时间")
    private Long creatorTime;
    @Schema(description = "登录用户")
    private String userName;
    @Schema(description = "登录IP")
    private String ipaddress;
    @Schema(description = "摘要")
    private String platForm;
    private String requestURL;
    private String requestMethod;
    private String requestDuration;
}
