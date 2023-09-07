package org.openea.eap.extj.engine.model;

import org.openea.eap.extj.base.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import org.openea.eap.extj.engine.util.FlowNature;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
public class FlowHandleModel extends Pagination {
    /**
     * 意见
     **/
    @Schema(description = "意见")
    private String handleOpinion;
    /**
     * 加签人
     **/
    @Schema(description = "加签人")
    private String freeApproverUserId;
    /**
     * 加签类型 1.前 2 后
     */
    @Schema(description = "加签类型")
    private Integer freeApproverType = FlowNature.Later;
    /**
     * 审批数据
     **/
    @Schema(description = "审批数据")
    private Map<String, Object> formData = new HashMap<>();
    /**
     * 自定义抄送人
     **/
    @Schema(description = "自定义抄送人")
    private String copyIds;
    /**
     * 签名
     **/
    @Schema(description = "签名")
    private String signImg;
    /**
     * 指派节点
     **/
    @Schema(description = "指派节点")
    private String nodeCode;
    /**
     * 候选人
     */
    @Schema(description = "候选人",hidden = true)
    private Map<String, List<String>> candidateList = new HashMap<>();
    /**
     * 异常处理人
     */
    @Schema(description = "异常处理人",hidden = true)
    private Map<String, List<String>> errorRuleUserList = new HashMap<>();
    /**
     * 选择分支
     */
    @Schema(description = "选择分支",hidden = true)
    private List<String> branchList = new ArrayList<>();
    /**
     * 批量审批id
     */
    @Schema(description = "批量审批主键",hidden = true)
    private List<String> ids = new ArrayList<>();
    /**
     * 经办文件
     **/
    @Schema(description = "经办文件",hidden = true)
    private List<Object> fileList = new ArrayList<>();
    /**
     * 批量审批类型 0.通过 1.拒绝 2.转办
     */
    @Schema(description = "批量审批类型")
    private Integer batchType = 0;
    /**
     * 批量发起用户
     */
    @Schema(description = "批量发起用户",hidden = true)
    private List<String> delegateUserList = new ArrayList<>();
    /**
     * 驳回节点
     */
    @Schema(description = "驳回节点")
    private String rejectStep = FlowNature.START;
    /**
     * 驳回类型 1.重新审批 2.从当前节点审批
     */
    @Schema(description = "驳回类型")
    private Integer rejectType = 1;
    /**
     * false 变更 true 复活
     */
    @Schema(description = "类型")
    private Boolean resurgence = false;
    /**
     * true 同步子流程 false 全部子流程
     */
    @Schema(description = "冻结类型")
    private Boolean suspend = false;

}
