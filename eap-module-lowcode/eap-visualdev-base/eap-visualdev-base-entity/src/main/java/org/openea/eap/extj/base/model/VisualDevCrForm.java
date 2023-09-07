package org.openea.eap.extj.base.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;



@Data
@Schema(description = "功能设计创建表单" )
public class VisualDevCrForm {
    @Schema(description = "名称" )
    private String fullName;
    @Schema(description = "编码" )
    private String enCode;
    @Schema(description = "类型(1-应用开发,2-移动开发,3-流程表单,4-Web表单,5-App表单)" )
    private String type;
    @Schema(description = "描述" )
    private String description;
    @Schema(description = "表单配置JSON" )
    private String formData;
    @Schema(description = "列表配置JSON" )
    private String columnData;
    @Schema(description = "app列表配置JSON" )
    private String appColumnData;
    @Schema(description = "关联的表" )
    private String tables;
    @Schema(description = "分类(数据字典维护)" )
    private String category;
    @Schema(description = "状态" )
    private Integer state = 0;
    @Schema(description = "关联数据连接id" )
    private String dbLinkId;
    @Schema(description = "页面类型（1、纯表单，2、表单加列表，3、表单列表工作流、4、数据视图）" )
    private String webType;
    @Schema(description = "排序" )
    private Long sortCode;
    @Schema(description = "启用流程" )
    private Integer enableFlow;
    @Schema(description = "流程引擎json" )
    private String flowTemplateJson;
    @Schema(description = "接口id" )
    private String interfaceId;
    @Schema(description = "接口名称" )
    private String interfaceName;
    @Schema(description = "接口参数" )
    private String interfaceParam;
}
