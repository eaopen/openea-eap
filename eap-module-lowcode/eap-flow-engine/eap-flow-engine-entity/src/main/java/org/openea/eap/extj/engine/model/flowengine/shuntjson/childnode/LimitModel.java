package org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 
 */
@Data
public class LimitModel {
    /**
     * 限时设置 0.关闭  1.自定义  2.同步发起配置
     */
    @Schema(description = "限时设置")
    private Integer on = 0;
    /**
     * 开始时间 0-接收时间，1-发起时间，2-表单变量
     */
    @Schema(description = "开始类型")
    private Integer nodeLimit = 0;
    /**
     * 表单字段key
     */
    @Schema(description = "表单字段key")
    private String formField = "";
    /**
     * 处理限定时长默认24 （小时）
     */
    @Schema(description = "处理限定时长默认24")
    private Integer duringDeal = 24;
}

