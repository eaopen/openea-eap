package org.openea.eap.extj.engine.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import org.openea.eap.extj.base.entity.SuperBaseEntity;
import lombok.Data;

/**
 * 流程候选人
 *
 * 
 */
@Data
@TableName("flow_candidates")
public class FlowCandidatesEntity extends SuperBaseEntity.SuperTBaseEntity<String> {

    /**
     * 节点主键
     */
    @TableField("F_TASKNODEID")
    private String taskNodeId;

    /**
     * 任务主键
     */
    @TableField("F_TASKID")
    private String taskId;

    /**
     * 代办主键
     */
    @TableField("F_TASKOPERATORID")
    private String operatorId;

    /**
     * 审批人主键
     */
    @TableField("F_HANDLEID")
    private String handleId;

    /**
     * 审批人账号
     */
    @TableField("F_ACCOUNT")
    private String account;

    /**
     * 候选人
     */
    @TableField("F_CANDIDATES")
    private String candidates;

    /**
     * 类型 1.候选人 2.异常人
     */
    @TableField("F_TYPE")
    private String type;

}
