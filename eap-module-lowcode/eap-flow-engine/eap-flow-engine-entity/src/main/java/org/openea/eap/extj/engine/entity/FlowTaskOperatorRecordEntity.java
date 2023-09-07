package org.openea.eap.extj.engine.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import org.openea.eap.extj.base.entity.SuperBaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 流程经办记录
 *
 * 
 */
@Data
@TableName("flow_taskoperatorrecord")
public class FlowTaskOperatorRecordEntity extends SuperBaseEntity.SuperTBaseEntity<String> {

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
     * 经办状态 0-拒绝、1-同意、2-提交、3-撤回、4-终止、5-指派、6-加签、7-转办
     */
    @TableField("F_HANDLESTATUS")
    private Integer handleStatus;

    /**
     * 经办人员
     */
    @TableField("F_HANDLEID")
    private String handleId;

    /**
     * 经办时间
     */
    @TableField("F_HANDLETIME")
    private Date handleTime;

    /**
     * 经办理由
     */
    @TableField("F_HANDLEOPINION")
    private String handleOpinion;

    /**
     * 流转操作人
     */
    @TableField("F_OPERATORID")
    private String operatorId;

    /**
     * 经办主键
     */
    @TableField(value="F_TASKOPERATORID",fill = FieldFill.UPDATE)
    private String taskOperatorId;

    /**
     * 节点主键
     */
    @TableField(value="F_TASKNODEID",fill = FieldFill.UPDATE)
    private String taskNodeId;

    /**
     * 任务主键
     */
    @TableField("F_TASKID")
    private String taskId;

    /**
     * 签名图片
     */
    @TableField("F_SIGNIMG")
    private String signImg;

    /**
     * 0.进行数据 1.加签数据 3.已办不显示数据
     */
    @TableField("F_STATUS")
    private Integer status;

    /**
     * 经办文件
     */
    @TableField("F_FILELIST")
    private String fileList;

    /**
     * 审批数据
     */
    @TableField(value = "F_DRAFTDATA")
    private String draftData;

    /**
     * 加签类型
     */
    @TableField(value = "F_ApproverType")
    private Integer approverType;

}
