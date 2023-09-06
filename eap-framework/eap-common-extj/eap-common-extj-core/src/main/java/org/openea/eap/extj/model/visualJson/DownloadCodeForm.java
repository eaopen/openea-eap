package org.openea.eap.extj.model.visualJson;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 *
 */
@Data
@ApiModel("下载代码表单")
public class DownloadCodeForm {
    /**
     * 所属模块
     */
    @ApiModelProperty("所属模块")
    private String module;
    /**
     * 主功能名称
     */
    @ApiModelProperty("主功能名称")
    private String className;
    /**
     * 子表名称集合
     */
    @ApiModelProperty("子表名称集合")
    private String subClassName;
    /**
     * 主功能备注
     */
    @ApiModelProperty("主功能备注")
    private String description;

    /**
     * 数据源id
     */
    @ApiModelProperty("数据源id")
    private String dataSourceId;

}
