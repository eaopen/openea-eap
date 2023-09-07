package org.openea.eap.extj.base.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.openea.eap.extj.base.Pagination;

@Data
@ApiModel("查询条件模型")
public class PaginationModel extends Pagination {
    @ApiModelProperty("查询条件json")
    private String queryJson;
    @ApiModelProperty("菜单id")
    private String menuId;
    @ApiModelProperty("关联字段")
    private String relationField;
    @ApiModelProperty("字段对象")
    private String columnOptions;
    @ApiModelProperty("数据类型")
    private String dataType;
    @ApiModelProperty("高级查询条件json")
    private String superQueryJson;


}
