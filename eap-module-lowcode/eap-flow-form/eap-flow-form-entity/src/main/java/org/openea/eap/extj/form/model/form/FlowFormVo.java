package org.openea.eap.extj.form.model.form;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 流程表单对象
 *
 */
@Data
@Schema(description = "流程表单对象")
public class FlowFormVo {
    @Schema(description = "主键")
    private String id;

    @Schema(description = "表单编码")
    private String enCode;

    @Schema(description = "表单名称")
    private String fullName;

    @Schema(description = "流程类型")
    private Integer flowType;

    @Schema(description = "表单类型")
    private Integer formType;

    @Schema(description = "表单分类")
    private String category;

    @Schema(description = "Web地址")
    private String urlAddress;

    @Schema(description = "APP地址")
    private String appUrlAddress;

    @Schema(description = "接口路径")
    private String interfaceUrl;

    @Schema(description = "属性字段")
    private String propertyJson;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "排序码")
    private Long sortCode;

    @Schema(description = "有效标志")
    private Integer enabledMark;

    @Schema(description = "创建时间")
    private Long creatorTime;

    @Schema(description = "创建用户")
    private String creatorUserId;
    @Schema(description = "创建用户名")
    private String creatorUser;

    @Schema(description = "修改时间")
    private Long lastModifyTime;

    @Schema(description = "修改用户")
    private String lastModifyUserId;
    @Schema(description = "修改用户名")
    private String lastModifyUser;

    @Schema(description = "删除标志")
    private Integer deleteMark;

    @Schema(description = "删除时间")
    private Long deleteTime;

    @Schema(description = "删除用户")
    private String deleteUserId;
    @Schema(description = "删除用户名")
    private String deleteUser;

    @Schema(description = "草稿版本json")
    private String draftJson;

    @Schema(description = "关联数据连接id")
    private String dbLinkId;

    @Schema(description = "关联的表")
    private String tableJson;

}
