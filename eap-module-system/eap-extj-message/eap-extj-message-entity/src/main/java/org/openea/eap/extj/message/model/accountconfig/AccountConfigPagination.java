package org.openea.eap.extj.message.model.accountconfig;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.openea.eap.extj.base.Pagination;


@Data
public class AccountConfigPagination extends Pagination {

    /**
     * selectKey
     */
    @Schema(description = "selectKey")
    private String selectKey;

    @Schema(description = "json")
    private String json;

    /**
     * 类型
     */
    @Schema(description = "类型")
    private String dataType;

    /**
     * 特别查询Json
     */
    @Schema(description = "特别查询Json")
    private String superQueryJson;

    /**
     * WebHook类型
     */
    @Schema(description = "WebHook类型")
    private String webhookType;

    /**
     * 渠道
     */
    @Schema(description = "渠道")
    private String channel;

    /**
     * 配置类型(2：邮箱，3：短信，4：钉钉，5：企业，6：飞书，7：webhook)
     */
    @Schema(description = "配置类型(2：邮箱，3：短信，4：钉钉，5：企业，6：飞书，7：webhook)")
    private String type;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private String enabledMark;

    /**
     * 关键字
     **/
    @Schema(description = "关键字")
    private String keyword;

    /**
     * 菜单id
     */
    @Schema(description = "菜单id")
    private String menuId;
}