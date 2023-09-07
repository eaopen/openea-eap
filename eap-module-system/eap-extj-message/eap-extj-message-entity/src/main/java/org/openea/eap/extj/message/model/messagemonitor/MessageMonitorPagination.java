package org.openea.eap.extj.message.model.messagemonitor;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.openea.eap.extj.base.Pagination;


@Data
public class MessageMonitorPagination extends Pagination {

    @Schema(description = "selectKey")
    private String selectKey;

    @Schema(description = "json")
    private String json;

    @Schema(description = "数据类型")
    private String dataType;

    @Schema(description = "特殊查询json")
    private String superQueryJson;


    /**
     * 消息来源
     */
    @Schema(description = "消息来源")
    private String messageSource;

    /**
     * 消息类型
     */
    @Schema(description = "消息类型")
    private String messageType;

    /**
     * 关键词
     */
    @Schema(description = "关键词")
    private String keyword;

    /**
     * 发送时间（开始时间）
     */
    @Schema(description = "发送时间")
    private String startTime;

    /**
     * 接收时间
     */
    @Schema(description = "接收时间")
    private String endTime;
    /**
     * 菜单id
     */
    @Schema(description = "菜单id")
    private String menuId;
}