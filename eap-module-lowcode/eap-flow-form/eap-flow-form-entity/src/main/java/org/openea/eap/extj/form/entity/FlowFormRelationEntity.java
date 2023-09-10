package org.openea.eap.extj.form.entity;

import org.openea.eap.extj.base.entity.SuperBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 流程表单关联表
 *
 *
 */
@Data
@TableName("flow_engineform_relation")
public class FlowFormRelationEntity extends SuperBaseEntity.SuperTBaseEntity<String> {

    /**
     * 流程版本id
     */
    @TableField("F_FLOWID")
    private String flowId;
    /**
     * 表单id
     */
    @TableField("F_FORMID")
    private String formId;

}
