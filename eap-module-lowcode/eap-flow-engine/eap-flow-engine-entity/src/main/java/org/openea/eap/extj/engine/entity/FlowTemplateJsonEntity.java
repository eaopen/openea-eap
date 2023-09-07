package org.openea.eap.extj.engine.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import org.openea.eap.extj.base.entity.SuperEntity;
import lombok.Data;

/**
 * 流程引擎
 *
 *
 */
@Data
@TableName("flow_templatejson")
public class FlowTemplateJsonEntity extends SuperEntity<String> {

    /**
     * 流程模板id
     */
    @TableField("F_TEMPLATEID")
    private String templateId;

    /**
     * 流程名称
     */
    @TableField("F_FULLNAME")
    private String fullName;

    /**
     * 可见类型 0-全部可见、1-指定经办
     */
    @TableField("F_VISIBLETYPE")
    private Integer visibleType;

    /**
     * 流程模板
     */
    @TableField("F_FLOWTEMPLATEJSON")
    private String flowTemplateJson;

    /**
     * 流程版本
     */
    @TableField("F_VERSION")
    private String version;

    /**
     * 分组id
     */
    @TableField("F_GROUPID")
    private String groupId;

    /**
     * 主版本 1.主版本 0.不是主版本
     */
    @TableField("F_ENABLEDMARK")
    private Integer enabledMark;

    /**
     * 排序码
     */
    @TableField("F_SORTCODE")
    private Long sortCode;

}
