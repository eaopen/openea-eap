package org.openea.eap.extj.engine.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import org.openea.eap.extj.base.entity.SuperBaseEntity;
import lombok.Data;

/**
 * 冻结审批
 *
 * 
 */
@Data
@TableName("flow_rejectData")
public class FlowRejectDataEntity extends SuperBaseEntity.SuperTBaseEntity<String> {

    /**
     * 节点数据
     */
    @TableField("F_TASKNODEJSON")
    private String taskNodeJson;

    /**
     * 流程任务
     */
    @TableField("F_TASKJSON")
    private String taskJson;

    /**
     * 经办数据
     */
    @TableField("F_TASKOPERATORJSON")
    public String taskOperatorJson;


}
