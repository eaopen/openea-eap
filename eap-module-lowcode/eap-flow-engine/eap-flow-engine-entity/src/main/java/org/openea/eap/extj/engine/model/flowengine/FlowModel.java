package org.openea.eap.extj.engine.model.flowengine;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.constant.MsgCode;
import io.swagger.v3.oas.annotations.media.Schema;
import org.openea.eap.extj.engine.enums.FlowStatusEnum;
import org.openea.eap.extj.engine.model.FlowHandleModel;
import org.openea.eap.extj.engine.util.FlowNature;
import lombok.Data;


@Data
public class FlowModel extends FlowHandleModel {
    /**
     * 判断新增
     **/
    @Schema(description = "判断新增")
    private String id;
    /**
     * 引擎id
     **/
    @Schema(description = "引擎主键")
    private String flowId;
    /**
     * 流程主键
     **/
    @Schema(description = "流程主键")
    private String processId;
    /**
     * 流程标题
     **/
    @Schema(description = "流程标题")
    private String flowTitle;
    /**
     * 紧急程度
     **/
    @Schema(description = "紧急程度")
    private Integer flowUrgent = 1;
    /**
     * 流水号
     **/
    @Schema(description = "流水号")
    private String billNo = MsgCode.WF109.get();
    /**
     * 0.提交 1.保存
     **/
    @Schema(description = "类型")
    private String status = FlowStatusEnum.save.getMessage();
    /**
     * 子流程
     **/
    @Schema(description = "子流程")
    private String parentId = FlowNature.ParentId;
    /**
     * 创建人
     **/
    @Schema(description = "创建人")
    private String userId;
    /**
     * 被委托人
     */
    @Schema(description = "被委托人")
    private String delegateUser;
    /**
     * 当前经办id
     **/
    @Schema(description = "当前经办id")
    private String taskOperatorId;
    /**
     * 回流id
     */
    @Schema(description = "回流主键")
    private String rollbackId;
    /**
     * 是否冻结审批
     */
    @Schema(description = "是否冻结审批",hidden = true)
    private Boolean rejectUser = false;
    /**
     * 是否子流程
     **/
    @Schema(description = "是否子流程",hidden = true)
    private Boolean isAsync = false;
    /**
     * 用户信息
     */
    @Schema(description = "用户信息")
    @JsonIgnore
    private UserInfo userInfo;
    /**
     * 定时器
     */
    @Schema(description = "是否定时器",hidden = true)
    private Boolean isTimer = false;
    /**
     * 变更节点
     */
    @Schema(description = "变更节点",hidden = true)
    private String taskNodeId;
    /**
     * 任务主键
     */
    @Schema(description = "任务主键")
    private String taskId;
    /**
     * 自动审批
     */
    @Schema(description = "自动审批",hidden = true)
    private Boolean voluntarily = false;

}
