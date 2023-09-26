package org.openea.eap.module.infra.controller.admin.file.vo.module;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 文件配置创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FileModuleCreateReqVO extends FileModuleBaseVO {



}
