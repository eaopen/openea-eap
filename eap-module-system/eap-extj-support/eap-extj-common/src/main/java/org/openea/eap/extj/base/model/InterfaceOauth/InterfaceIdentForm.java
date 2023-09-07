package org.openea.eap.extj.base.model.InterfaceOauth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class InterfaceIdentForm {

    @Schema(description = "应用id")
    @NotBlank(message = "appId不能为空")
    private String appId;

    @Schema(description = "应用名称")
    @NotBlank(message = "应用名称不能为空")
    private String appName;

    @Schema(description = "应用秘钥")
    @NotBlank(message = "appSecret不能为空")
    private String appSecret;

    @Schema(description = "验证签名")
    private Integer verifySignature;

    @Schema(description = "使用期限")
    private Date usefulLife;

    @Schema(description = "白名单")
    private String whiteList;

    @Schema(description = "黑名单")
    private String blackList;

    @Schema(description = "排序")
    private Long sortCode;

    @Schema(description = "状态")
    private Integer enabledMark;

    @Schema(description = "说明")
    private String description;

}
