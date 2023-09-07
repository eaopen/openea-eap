package org.openea.eap.extj.engine.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import org.openea.eap.extj.base.entity.SuperExtendEntity;
import lombok.Data;

import java.util.Date;

/**
 * 流程委托
 *
 * 
 */
@Data
@TableName("flow_delegate")
public class FlowDelegateEntity extends SuperExtendEntity<String> {

    /**
     * 委托人
     */
    @TableField("F_USERID")
    private String userId;

    /**
     * 委托人
     */
    @TableField("F_USERNAME")
    private String userName;

    /**
     * 被委托人
     */
    @TableField("F_TOUSERID")
    private String toUserId;

    /**
     * 被委托人
     */
    @TableField("F_TOUSERNAME")
    private String toUserName;
    /**
     * 委托类型（0-发起委托，1-审批委托）
     */
    @TableField("F_TYPE")
    private String type;

    /**
     * 委托流程
     */
    @TableField("F_FLOWID")
    private String flowId;

    /**
     * 委托流程
     */
    @TableField("F_FLOWNAME")
    private String flowName;

    /**
     * 流程分类
     */
    @TableField("F_FLOWCATEGORY")
    private String flowCategory;

    /**
     * 开始时间
     */
    @TableField("F_STARTTIME")
    private Date startTime;

    /**
     * 结束时间
     */
    @TableField("F_ENDTIME")
    private Date endTime;

    /**
     * 排序码
     */
    @TableField("F_SORTCODE")
    private Long fSortCode;

}
