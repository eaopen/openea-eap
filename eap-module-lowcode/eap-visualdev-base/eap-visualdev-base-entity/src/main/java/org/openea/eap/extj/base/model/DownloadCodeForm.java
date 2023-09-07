package org.openea.eap.extj.base.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "下载代码表单")
public class DownloadCodeForm {
    /**
     * 所属模块
     */
    @Schema(description = "所属模块")
    private String module;

    /**
     * 模块包名
     */
    @Schema(description = "模块包名")
    private String modulePackageName;

    /**
     * 主功能名称
     */
    @Schema(description = "主功能名称")
    private String className;
    /**
     * 子表名称集合
     */
    @Schema(description = "子表名称集合")
    private String subClassName;
    /**
     * 主功能备注
     */
    @Schema(description = "主功能备注")
    private String description;

    /**
     * 数据源id
     */
    @Schema(description = "数据源id")
    private String dataSourceId;
}
