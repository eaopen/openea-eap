package org.openea.eap.extj.message.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 企业微信的模型
 *
 *
 */
@Data
public class QyWebChatModel {
    @Schema(description = "CorpId")
    private String qyhCorpId;
    @Schema(description = "AgentId")
    private String qyhAgentId;
    @Schema(description = "AgentSecret")
    private String qyhAgentSecret;
    @Schema(description = "CorpSecret")
    private String qyhCorpSecret;
}
