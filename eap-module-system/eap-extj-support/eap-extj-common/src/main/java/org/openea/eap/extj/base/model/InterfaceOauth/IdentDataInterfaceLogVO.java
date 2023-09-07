package org.openea.eap.extj.base.model.InterfaceOauth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class IdentDataInterfaceLogVO implements Serializable {

    @Schema(description = "主键")
    private String id;

    @Schema(description = "调用id")
    private String invokId;

    @Schema(description = "调用时间")
    private Date invokTime;

    @Schema(description = "用户id")
    private String userId;

    @Schema(description = "调用ip")
    private String invokIp;

    @Schema(description = "调用设备")
    private String invokDevice;

    @Schema(description = "调用类型")
    private String invokType;

    @Schema(description = "调用响应时间")
    private Integer invokWasteTime;

    @Schema(description = "租户id")
    private String tenantId;

    @Schema(description = "授权码")
    private String oauthAppId;

    @Schema(description = "名称")
    private String fullName;

    @Schema(description = "编码")
    private String enCode;

}
