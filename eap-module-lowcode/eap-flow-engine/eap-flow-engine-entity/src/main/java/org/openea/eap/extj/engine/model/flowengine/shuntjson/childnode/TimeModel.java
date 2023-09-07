package org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 
 */
@Data
public class TimeModel {
    /**
     * 超时设置 0.关闭  1.自定义  2.同步发起配置
     */
    @Schema(description = "超时设置")
    private Integer on = 0;
    /**
     * （小时）第一次超时时间默认值0=第一次触发超时事件时间=节点限定时长起始值+节点处理限定时长+设定的第一次超时时间
     */
    @Schema(description = "超时时间")
    private Integer firstOver = 0;
    /**
     * 超时间隔（小时）
     */
    @Schema(description = "超时间隔")
    private Integer overTimeDuring = 2;
    /**
     * 超时通知
     */
    @Schema(description = "超时通知")
    private Boolean overNotice = false;
    /**
     * 超时自动审批
     */
    @Schema(description = "超时自动审批")
    private Boolean overAutoApprove = false;
    /**
     * 超时自动审批前提条件，超时次数
     */
    @Schema(description = "超时次数")
    private Integer overAutoApproveTime = 5;
    /**
     * 超时事件
     */
    @Schema(description = "超时事件")
    private Boolean overEvent = false;
    /**
     * 超时事件前提条件，超时次数
     */
    @Schema(description = "超时次数")
    private Integer overEventTime = 5;
}
