package org.openea.eap.extj.base.model.dataInterface;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class DataInterfaceVo {
    @Schema(description = "主键id")
    private String id;
    @Schema(description = "接口名称")
    private String fullName;
    @Schema(description = "数据源id")
    private String dbLinkId;
    @Schema(description = "分类id")
    private String categoryId;
    @Schema(description = "数据类型")
    private Integer dataType;
    @Schema(description = "请求方式")
    private String requestMethod;
    @Schema(description = "返回类型")
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
    @Schema(description = "接口路径")
    private String path;
    @Schema(description = "请求参数JSON")
    private String requestParameters;
    @Schema(description = "请求头参数")
    private String requestHeaders;
    @Schema(description = "验证类型")
    private Integer checkType;
    @Schema(description = "数据处理")
    private String dataProcessing;
    @Schema(description = "白名单")
    private String propertyJson;
}
