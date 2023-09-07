package org.openea.eap.extj.engine.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import org.openea.eap.extj.base.entity.SuperBaseEntity;
import lombok.Data;

/**
 * 流程发起用户信息
 *
 * 
 */
@Data
@TableName("flow_user")
public class FlowUserEntity extends SuperBaseEntity.SuperTBaseEntity<String> {

    /**
     * 组织主键
     */
    @TableField("F_ORGANIZEID")
    private String organizeId;

    /**
     * 岗位主键
     */
    @TableField("F_POSITIONID")
    private String positionId;

    /**
     * 主管主键
     */
    @TableField("F_MANAGERID")
    private String managerId;

    /**
     * 上级用户
     */
    @TableField("F_SUPERIOR")
    private String superior;

    /**
     * 下属用户
     */
    @TableField("F_SUBORDINATE")
    private String subordinate;

    /**
     * 公司下所有部门
     */
    @TableField("F_DEPARTMENT")
    private String department;

    /**
     * 任务主键
     */
    @TableField("F_TASKID")
    private String taskId;

}
