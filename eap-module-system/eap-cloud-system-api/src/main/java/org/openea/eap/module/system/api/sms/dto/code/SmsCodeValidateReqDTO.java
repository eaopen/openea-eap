package org.openea.eap.module.system.api.sms.dto.code;

import org.openea.eap.framework.common.validation.InEnum;
import org.openea.eap.framework.common.validation.Mobile;
import org.openea.eap.module.system.enums.sms.SmsSceneEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Schema(description = "RPC 服务 - 短信验证码的校验 Request DTO")
@Data
public class SmsCodeValidateReqDTO {

    @Schema(description = "手机号", required = true, example = "15601691300")
    @Mobile
    @NotEmpty(message = "手机号不能为空")
    private String mobile;
    @Schema(description = "发送场景", required = true, example = "1")
    @NotNull(message = "发送场景不能为空")
    @InEnum(SmsSceneEnum.class)
    private Integer scene;
    @Schema(description = "验证码", required = true, example = "1024")
    @NotEmpty(message = "验证码")
    private String code;

}