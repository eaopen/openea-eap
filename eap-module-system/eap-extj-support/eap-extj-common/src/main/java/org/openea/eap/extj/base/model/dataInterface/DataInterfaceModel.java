package org.openea.eap.extj.base.model.dataInterface;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 数据接口模型
 *
 */
@Data
public class DataInterfaceModel implements Serializable {
    /**
     * 参数名称
     */
    @Schema(description = "参数名称")
    private String field;

    /**
     * 列说明
     */
    @Schema(description = "列说明")
    private String fieldName;

    /**
     * 参数类型
     * 字符串  1
     * 整型    2
     * 日期时间 3
     * 浮点   4
     * 长整型  5
     * 文本   6
     */
    @Schema(description = "参数类型")
    private String dataType;

    /**
     * 是否为空（0允许，1不允许）
     */
    @Schema(description = "是否为空（0允许，1不允许）")
    private Integer required;

    /**
     * 默认值
     */
    @Schema(description = "默认值")
    private String defaultValue;
}
