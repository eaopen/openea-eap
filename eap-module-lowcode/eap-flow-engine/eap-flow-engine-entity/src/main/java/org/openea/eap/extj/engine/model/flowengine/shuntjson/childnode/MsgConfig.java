package org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析引擎
 *
 * 
 */
@Data
public class MsgConfig {
    /**
     * 0.关闭  1.自定义  2.同步发起配置  3.默认
     */
    @Schema(description = "类型")
    private Integer on = 0;
    @Schema(description = "消息主键")
    private String msgId;
    @Schema(description = "接口主键")
    private String interfaceId;
    @Schema(description = "名称")
    private String msgName;
    @Schema(description = "数据")
    private List<SendConfigTemplateJsonModel> templateJson = new ArrayList<>();
}
