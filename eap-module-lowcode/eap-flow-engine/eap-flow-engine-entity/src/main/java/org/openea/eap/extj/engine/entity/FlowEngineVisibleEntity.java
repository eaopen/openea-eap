package org.openea.eap.extj.engine.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import org.openea.eap.extj.base.entity.SuperBaseEntity;
import lombok.Data;

/**
 * 流程可见
 *
 *
 */
@Data
@TableName("flow_enginevisible")
public class FlowEngineVisibleEntity extends SuperBaseEntity.SuperCBaseEntity<String> {

    /**
     * 流程主键
     */
    @TableField("F_FLOWID")
    private String flowId;

    /**
     * 经办类型
     */
    @TableField("F_OPERATORTYPE")
    private String operatorType;

    /**
     * 经办主键
     */
    @TableField("F_OPERATORID")
    private String operatorId;

    /**
     * 排序码
     */
    @TableField("F_SORTCODE")
    private Long sortCode;

    /**
     * 可见类型 1.发起 2.协管
     */
    @TableField("F_TYPE")
    private String type;

}
