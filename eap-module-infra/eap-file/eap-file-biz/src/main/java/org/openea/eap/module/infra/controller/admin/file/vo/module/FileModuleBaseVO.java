package org.openea.eap.module.infra.controller.admin.file.vo.module;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
* 文件模块 Base VO，提供给添加、修改、详细的子 VO 使用
*/
@Data
public class FileModuleBaseVO {

    @Schema(description = "编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "system")
    @NotNull(message = "编码不能为空")
    private String code;

    @Schema(description = "配置名", requiredMode = Schema.RequiredMode.REQUIRED, example = "系统")
    @NotNull(message = "配置名不能为空")
    private String name;

    @Schema(description = "描述", example = "我是备注")
    private String description;

}
