package org.openea.eap.module.visualdev.extend.model.flowengine;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.openea.eap.extj.base.Pagination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Data
public class FlowHandleModel extends Pagination {
    /**
     * 意见
     **/
    @ApiModelProperty(value = "意见")
    private String handleOpinion;
    /**
     * 加签人
     **/
    @ApiModelProperty(value = "加签人")
    private String freeApproverUserId;
    /**
     * 加签类型 1.前 2 后
     */
//    @ApiModelProperty(value = "加签类型")
//    private Integer freeApproverType = FlowNature.Later;
    /**
     * 审批数据
     **/
    @ApiModelProperty(value = "审批数据")
    private Map<String, Object> formData = new HashMap<>();
    /**
     * 自定义抄送人
     **/
    @ApiModelProperty(value = "自定义抄送人")
    private String copyIds;
    /**
     * 签名
     **/
    @ApiModelProperty(value = "签名")
    private String signImg;
    /**
     * 指派节点
     **/
    @ApiModelProperty(value = "指派节点")
    private String nodeCode;
    /**
     * 候选人
     */
    @ApiModelProperty(value = "候选人",hidden = true)
    private Map<String, List<String>> candidateList = new HashMap<>();
    /**
     * 异常处理人
     */
    @ApiModelProperty(value = "异常处理人",hidden = true)
    private Map<String, List<String>> errorRuleUserList = new HashMap<>();
    /**
     * 选择分支
     */
    @ApiModelProperty(value = "选择分支",hidden = true)
    private List<String> branchList = new ArrayList<>();
    /**
     * 批量审批id
     */
    @ApiModelProperty(value = "批量审批主键",hidden = true)
    private List<String> ids = new ArrayList<>();
    /**
     * 经办文件
     **/
    @ApiModelProperty(value = "经办文件",hidden = true)
    private List<Object> fileList = new ArrayList<>();
    /**
     * 批量审批类型 0.通过 1.拒绝 2.转办
     */
    @ApiModelProperty(value = "批量审批类型")
    private Integer batchType = 0;
    /**
     * 批量发起用户
     */
    @ApiModelProperty(value = "批量发起用户",hidden = true)
    private List<String> delegateUserList = new ArrayList<>();
    /**
     * 驳回节点
//     */
////    @ApiModelProperty(value = "驳回节点")
////    private String rejectStep = FlowNature.START;
    /**
     * 驳回类型 1.重新审批 2.从当前节点审批
     */
    @ApiModelProperty(value = "驳回类型")
    private Integer rejectType = 1;
    /**
     * false 变更 true 复活
     */
    @ApiModelProperty(value = "类型")
    private Boolean resurgence = false;
    /**
     * true 同步子流程 false 全部子流程
     */
    @ApiModelProperty(value = "冻结类型")
    private Boolean suspend = false;

}
