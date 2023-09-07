package org.openea.eap.extj.base.model.dataInterface;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
public class DataInterfaceCrForm {
    @Schema(description = "接口名称")
    @NotBlank(message = "接口名称不能为空")
    private String fullName;
    @Schema(description = "数据源id")
    @NotBlank(message = "数据源id不能为空")
    private String dbLinkId;
    @Schema(description = "接口路径")
    private String path;
    @Schema(description = "数据类型")
    @NotNull(message = "数据类型不能为空")
    private Integer dataType;
    @Schema(description = "分类id")
    @NotBlank(message = "分类id不能为空")
    private String categoryId;
    @Schema(description = "请求方式")
    private String requestMethod;
    @Schema(description = "返回类型")
    @NotBlank(message = "返回类型不能为空")
    private String responseType;
    @Schema(description = "排序")
    private Long sortCode;
    @Schema(description = "状态(0-默认，禁用，1-启用)")
    private Integer enabledMark;
    @Schema(description = "说明备注")
    private String description;
    @Schema(description = "查询语句")
    private String query;
    @Schema(description = "编码")
    private String enCode;
    @Schema(description = "请求参数JSON")
    private String requestParameters;
    @Schema(description = "请求头参数")
    private String requestHeaders;
    @Schema(description = "验证类型")
    private Integer checkType;
    @Schema(description = "数据处理")
    private String dataProcessing;
    @Schema(description = "扩展参数Json")
    private String propertyJson;
}
