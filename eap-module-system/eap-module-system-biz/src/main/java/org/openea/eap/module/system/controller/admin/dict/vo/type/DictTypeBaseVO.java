package org.openea.eap.module.system.controller.admin.dict.vo.type;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 字典类型 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
public class DictTypeBaseVO {

    @Schema(description = "字典名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "性别")
    @NotBlank(message = "字典名称不能为空")
    @Size(max = 100, message = "字典类型名称长度不能超过100个字符")
    private String name;

    @Schema(description = "状态,参见 CommonStatusEnum 枚举类", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "备注", example = "快乐的备注")
    private String remark;

    // 扩展字段
    @Schema(description = "树形")
    private Integer isTree;

    @Schema(description = "上级")
    private Long parentId;

    @Schema(description = "数据类型 data/json/sql, 默认为data")
    private String dataType;

    @Schema(description = "json数据")
    private String dataJson;

    @Schema(description = "查询sql")
    private String dataSql;

    @Schema(description = " 查询sql数据源")
    private String dataDs;

}
