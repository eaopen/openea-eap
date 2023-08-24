package org.openea.eap.module.visualdev.base.model.flowengine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.constant.MsgCode;

@Data
public class FlowModel extends FlowHandleModel {
    /**
     * 判断新增
     **/
    @ApiModelProperty(value = "判断新增")
    private String id;
    /**
     * 引擎id
     **/
    @ApiModelProperty(value = "引擎主键")
    private String flowId;
    /**
     * 流程主键
     **/
    @ApiModelProperty(value = "流程主键")
    private String processId;
    /**
     * 流程标题
     **/
    @ApiModelProperty(value = "流程标题")
    private String flowTitle;
    /**
     * 紧急程度
     **/
    @ApiModelProperty(value = "紧急程度")
    private Integer flowUrgent = 1;
    /**
     * 流水号
     **/
    @ApiModelProperty(value = "流水号")
    private String billNo = MsgCode.WF109.get();
    /**
     * 0.提交 1.保存
     **/
//    @ApiModelProperty(value = "类型")
//    private String status = FlowStatusEnum.save.getMessage();
    /**
     * 子流程
     **/
//    @ApiModelProperty(value = "子流程")
//    private String parentId = FlowNature.ParentId;
    /**
     * 创建人
     **/
    @ApiModelProperty(value = "创建人")
    private String userId;
    /**
     * 被委托人
     */
    @ApiModelProperty(value = "被委托人")
    private String delegateUser;
    /**
     * 当前经办id
     **/
    @ApiModelProperty(value = "当前经办id")
    private String taskOperatorId;
    /**
     * 回流id
     */
    @ApiModelProperty(value = "回流主键")
    private String rollbackId;
    /**
     * 是否冻结审批
     */
    @ApiModelProperty(value = "是否冻结审批",hidden = true)
    private Boolean rejectUser = false;
    /**
     * 是否子流程
     **/
    @ApiModelProperty(value = "是否子流程",hidden = true)
    private Boolean isAsync = false;
    /**
     * 用户信息
     */
    @ApiModelProperty(value = "用户信息")
    @JsonIgnore
    private UserInfo userInfo;
    /**
     * 定时器
     */
    @ApiModelProperty(value = "是否定时器",hidden = true)
    private Boolean isTimer = false;
    /**
     * 变更节点
     */
    @ApiModelProperty(value = "变更节点",hidden = true)
    private String taskNodeId;
    /**
     * 任务主键
     */
    @ApiModelProperty(value = "任务主键")
    private String taskId;
    /**
     * 自动审批
     */
    @ApiModelProperty(value = "自动审批",hidden = true)
    private Boolean voluntarily = false;

}
