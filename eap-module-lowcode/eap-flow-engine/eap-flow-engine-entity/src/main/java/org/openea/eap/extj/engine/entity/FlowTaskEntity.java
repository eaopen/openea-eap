package org.openea.eap.extj.engine.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import org.openea.eap.extj.base.entity.SuperExtendEntity;
import lombok.Data;

import java.util.Date;

/**
 * 流程任务
 *
 * 
 */
@Data
@TableName("flow_task")
public class FlowTaskEntity extends SuperExtendEntity.SuperExtendSortEntity<String> {

    /**
     * 实例进程
     */
    @TableField("F_PROCESSID")
    private String processId;

    /**
     * 任务编码
     */
    @TableField("F_ENCODE")
    private String enCode;

    /**
     * 任务标题
     */
    @TableField("F_FULLNAME")
    private String fullName;

    /**
     * 紧急程度
     */
    @TableField("F_FLOWURGENT")
    private Integer flowUrgent;

    /**
     * 流程主键
     */
    @TableField("F_FLOWID")
    private String flowId;

    /**
     * 流程模板id
     */
    @TableField("F_TEMPLATEID")
    private String templateId;

    /**
     * 流程编码
     */
    @TableField("F_FLOWCODE")
    private String flowCode;

    /**
     * 流程名称
     */
    @TableField("F_FLOWNAME")
    private String flowName;

    /**
     * 流程类型
     */
    @TableField("F_FLOWTYPE")
    private Integer flowType;

    /**
     * 流程分类
     */
    @TableField("F_FLOWCATEGORY")
    private String flowCategory;

    /**
     * 流程表单
     */
    @TableField("F_FLOWFORM")
    private String flowForm;

    /**
     * 表单内容
     */
    @TableField("F_FLOWFORMCONTENTJSON")
    private String flowFormContentJson;

    /**
     * 流程模板
     */
    @TableField("F_FLOWTEMPLATEJSON")
    private String flowTemplateJson;

    /**
     * 流程版本
     */
    @TableField("F_FLOWVERSION")
    private String flowVersion;

    /**
     * 开始时间
     */
    @TableField(value = "F_STARTTIME")
    private Date startTime;

    /**
     * 结束时间
     */
    @TableField(value = "F_ENDTIME")
    private Date endTime;

    /**
     * 当前步骤
     */
    @TableField("F_THISSTEP")
    private String thisStep;

    /**
     * 当前步骤Id
     */
    @TableField("F_THISSTEPID")
    private String thisStepId;

    /**
     * 重要等级
     */
    @TableField("F_GRADE")
    private String grade;

    /**
     * 任务状态 0-草稿、1-处理、2-通过、3-驳回、4-撤销、5-终止、6-挂起
     */
    @TableField("F_STATUS")
    private Integer status;

    /**
     * 挂起之前状态
     */
    @TableField(value = "F_SUSPEND", fill = FieldFill.UPDATE)
    private Integer suspend;

    /**
     * 完成情况
     */
    @TableField("F_COMPLETION")
    private Integer completion;

    /**
     * 父节点id
     */
    @TableField("F_PARENTID")
    private String parentId;

    /**
     * 被委托用户
     */
    @TableField("F_DELEGATEUSER")
    private String delegateUser;

    /**
     * 节点主键
     */
    @TableField(value = "F_TASKNODEID", fill = FieldFill.UPDATE)
    private String taskNodeId;

    /**
     * 是否批量（0：否，1：是）
     */
    @TableField("F_ISBATCH")
    private Integer isBatch;

    /**
     * 同步异步（0：同步，1：异步）
     */
    @TableField(value = "F_ISASYNC")
    private Integer isAsync;

    /**
     * 冻结审批
     */
    @TableField(value = "F_REJECTDATAID", fill = FieldFill.UPDATE)
    private String rejectId;

}
