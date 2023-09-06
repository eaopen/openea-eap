package org.openea.eap.extj.model.login;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 *
 */
@Data
public class UserSystemVO {
    @ApiModelProperty(value = "系统id")
    private String id;
    @ApiModelProperty(value = "系统名称")
    private String name;
    @ApiModelProperty(value = "系统图标")
    private String icon;
    @ApiModelProperty(value = "是否当前系统")
    private Boolean currentSystem = false;
}
