package org.openea.eap.extj.model.login;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 *
 */
@Data
public class LoginVO {
    @ApiModelProperty(value = "token")
    private String token;
    @ApiModelProperty(value = "主题")
    private String theme;
}
