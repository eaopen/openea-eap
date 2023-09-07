package org.openea.eap.extj.engine.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import org.openea.eap.extj.base.entity.SuperBaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 流程传阅
 *
 *
 */
@Data
@TableName("flow_taskcirculate")
public class FlowTaskCirculateEntity extends SuperBaseEntity.SuperTBaseEntity<String> {

    /**
     * 对象类型
     */
    @TableField("F_OBJECTTYPE")
    private String objectType;

    /**
     * 对象主键
     */
    @TableField("F_OBJECTID")
    private String objectId;

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
     * 创建时间
     */
    @TableField(value = "F_CREATORTIME", fill = FieldFill.INSERT)
    private Date creatorTime;

}
