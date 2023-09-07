package org.openea.eap.extj.engine.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import org.openea.eap.extj.base.entity.SuperBaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 流程依次审批
 *
 * 
 */
@Data
@TableName("flow_taskoperatoruser")
public class FlowOperatorUserEntity extends SuperBaseEntity.SuperTBaseEntity<String> {

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
     * 状态
     */
    @TableField("F_STATE")
    private Integer state;

    /**
     * 描述
     */
    @TableField("F_DESCRIPTION")
    private String description;

    /**
     * 经办主键
     */
    @TableField("F_HANDLEID")
    private String handleId;

    /**
     * 排序码
     */
    @TableField("F_SORTCODE")
    private Long sortCode;

    /**
     * 创建时间
     */
    @TableField(value = "F_CREATORTIME", fill = FieldFill.INSERT)
    private Date creatorTime;

    /**
     * 经办对象
     */
    @TableField("F_HANDLETYPE")
    private String handleType;

    /**
     * 节点类型
     */
    @TableField("F_Type")
    private String type;

    /**
     * 节点编码
     */
    @TableField("F_NODECODE")
    private String nodeCode;

    /**
     * 节点名称
     */
    @TableField("F_NODENAME")
    private String nodeName;

    /**
     * 是否完成
     */
    @TableField("F_COMPLETION")
    private Integer completion;

    /**
     * 父节点id
     */
    @TableField("F_PARENTID")
    private String parentId;

    /**
     * 自动审批
     */
    @TableField("F_AUTOMATION")
    private String automation;

    /**
     * 冻结
     */
    @TableField("F_Reject")
    private String reject;

}
