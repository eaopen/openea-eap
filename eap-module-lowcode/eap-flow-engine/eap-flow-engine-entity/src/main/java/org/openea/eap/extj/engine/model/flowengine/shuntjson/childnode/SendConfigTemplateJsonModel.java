package org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 *
 *
 */
@Data
public class SendConfigTemplateJsonModel  {

    @Schema(description = "主键")
    private String id;

    /** 消息发送配置id **/
    @Schema(description = "消息发送配置主键")
    private String sendConfigId;

    /** 消息类型 **/
    @Schema(description = "消息类型")
    private String messageType;

    /** 消息模板id **/
    @Schema(description = "消息模板主键")
    private String templateId;

    /** 账号配置id **/
    @Schema(description = "账号配置主键")
    private String accountConfigId;

    /** 接收人 **/
    @Schema(description = "接收人")
    private List<String> toUser;

    /** 模板参数 **/
    @Schema(description = "模板参数")
    private String paramJson;

    /** 消息模板名称 **/
    @Schema(description = "消息模板名称")
    private String msgTemplateName;


}
