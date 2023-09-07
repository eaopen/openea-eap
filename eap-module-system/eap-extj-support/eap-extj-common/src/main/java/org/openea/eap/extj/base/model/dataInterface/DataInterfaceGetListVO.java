package org.openea.eap.extj.base.model.dataInterface;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 工作流调用弹框时使用
 *
 */
@Data
public class DataInterfaceGetListVO implements Serializable {
    @Schema(description = "主键Id")
    private String id;
    @Schema(description = "接口名称")
    private String fullName;
    @Schema(description = "接口类型")
    private String dataType;
    @Schema(description = "类别")
    private String requestMethod;
    @Schema(description = "编码")
    private String enCode;
    @Schema(description = "请求参数")
    private String requestParameters;
}
