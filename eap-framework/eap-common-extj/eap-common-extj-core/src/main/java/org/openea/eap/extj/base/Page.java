package org.openea.eap.extj.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Page {
    @ApiModelProperty(value = "关键字")
    private String keyword="";
}
