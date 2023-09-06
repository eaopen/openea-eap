package org.openea.eap.extj.model.visualJson;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 *
 */
@Data
@ApiModel("表单字段")
public class FormDataField {
    @ApiModelProperty("key")
    private String vModel;
    @ApiModelProperty("名称")
    private String label;
}
