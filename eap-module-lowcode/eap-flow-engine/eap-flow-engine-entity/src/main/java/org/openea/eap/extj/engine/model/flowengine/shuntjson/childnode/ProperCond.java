package org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 解析引擎
 *
 *
 */
@Data
public class ProperCond {
    @Schema(description = "名称")
    private String fieldName;
    @Schema(description = "名称")
    private String symbolName;
    @Schema(description = "名称")
    private String logicName;
    //1.字段 2.公式
    @Schema(description = "类型")
    private int fieldType = 1;
    @Schema(description = "类型")
    //1.数据里面获取 //2.解析表达式
    private String field;
    @Schema(description = "类型")
    //1.字段 2.自定义
    private int fieldValueType = 2;
    @Schema(description = "类型")
    //1.数据里面获取 2.直接获取
    private Object fieldValue;
    @Schema(description = "属性")
    private String symbol;
    @Schema(description = "表达式")
    private String logic;
    @Schema(description = "类型")
    private String jnpfKey;
    @Schema(description = "类型")
    private String fieldValueJnpfKey;
}
