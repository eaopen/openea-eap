package org.openea.eap.extj.engine.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import org.openea.eap.extj.base.entity.SuperEntity;
import lombok.Data;

/**
 * 流程评论
 *
 * 
 */
@Data
@TableName("flow_comment")
public class FlowCommentEntity extends SuperEntity<String> {

    /**
     * 任务主键
     */
    @TableField("F_TASKID")
    private String taskId;

    /**
     * 任务主键
     */
    @TableField("F_TEXT")
    private String text;

    /**
     * 任务主键
     */
    @TableField("F_IMAGE")
    private String image;

    /**
     * 任务主键
     */

    @TableField("F_FILE")
    @JSONField(name = "file")
    private String fileName;

    /**
     * 有效标志
     */
    @TableField("F_ENABLEDMARK")
    private Integer enabledMark;

}
