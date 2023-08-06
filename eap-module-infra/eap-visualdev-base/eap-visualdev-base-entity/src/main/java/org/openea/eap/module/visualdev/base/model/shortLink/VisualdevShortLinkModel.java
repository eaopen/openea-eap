package org.openea.eap.extj.base.model.shortLink;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 外链请求参数
 *
 */
@Data
@Schema(description = "外链入口参数")
public class VisualdevShortLinkModel {
    @Schema(description = "类型：form-表单,list-列表")
    private String type;
    @Schema(description = "租户id")
    private String tenantId;
}
