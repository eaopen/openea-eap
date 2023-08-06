package org.openea.eap.extj.base.model.shortLink;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 在线表单外链显示类
 *
 */
@Data
@Schema(description = "外链配置对象")
public class VisualdevShortLinkConfigVo {
    @Schema(description = "主键")
    private String id;
    @Schema(description = "表单开关0-关闭,1-开启")
    private Integer formUse = 0;
    @Schema(description = "表单密码开关0-关闭,1-开启")
    private Integer formPassUse = 0;
    @Schema(description = "列表开关0-关闭,1-开启")
    private Integer columnUse = 0;
    @Schema(description = "列表密码开关0-关闭,1-开启")
    private Integer columnPassUse = 0;
    @Schema(description = "列表查询字段")
    private String columnCondition;
    @Schema(description = "列表展示字段")
    private String columnText;
    @Schema(description = "用户id")
    private String userId;
    @Schema(description = "租户id")
    private String tenantId;
    @Schema(description = "是否启用0-关闭1-启用")
    private Integer enabledMark;
    @Schema(description = "表单外链")
    private String formLink;
    @Schema(description = "列表外链")
    private String columnLink;
}
