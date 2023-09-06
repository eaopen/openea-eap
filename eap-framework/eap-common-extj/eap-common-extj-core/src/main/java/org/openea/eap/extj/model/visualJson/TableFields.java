package org.openea.eap.extj.model.visualJson;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 *
 */
@Data
@ApiModel("表字段模型")
public class TableFields {
    @ApiModelProperty("字段名")
    private String field;
    @ApiModelProperty("字段备注")
    private String comment;
    @ApiModelProperty("类型")
    private String dataType;
    @ApiModelProperty("是否主键")
    private Integer isPrimaryKey;
}
