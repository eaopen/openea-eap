package org.openea.eap.extj.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 *
 *
 */
@Data
public class ImageVO {
    @ApiModelProperty(value = "路径")
    private String domain;
    @ApiModelProperty(value = "链接")
    private String link;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "名称")
    private String originalName;
}
