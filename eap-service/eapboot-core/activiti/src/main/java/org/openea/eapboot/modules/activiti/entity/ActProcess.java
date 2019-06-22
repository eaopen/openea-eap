package org.openea.eapboot.modules.activiti.entity;

import org.openea.eapboot.base.EapBaseEntity;
import org.openea.eapboot.common.constant.ActivitiConstant;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 */
@Data
@Entity
@Table(name = "t_act_process")
@TableName("t_act_process")
@ApiModel(value = "流程定义")
public class ActProcess extends EapBaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "流程名称")
    private String name;

    @ApiModelProperty(value = "流程标识名称")
    private String processKey;

    @ApiModelProperty(value = "版本")
    private Integer version;

    @ApiModelProperty(value = "部署id")
    private String deploymentId;

    @ApiModelProperty(value = "所属分类")
    private String categoryId;

    @ApiModelProperty(value = "xml文件名")
    private String xmlName;

    @ApiModelProperty(value = "流程图片名")
    private String diagramName;

    @ApiModelProperty(value = "描述/备注")
    private String description;

    @ApiModelProperty(value = "最新版本")
    private Boolean latest;

    @ApiModelProperty(value = "流程状态 部署后默认1激活")
    private Integer status = ActivitiConstant.PROCESS_STATUS_ACTIVE;

    @ApiModelProperty(value = "关联前端表单路由名")
    private String routeName;

    @ApiModelProperty(value = "关联业务表名")
    private String businessTable;

    @Transient
    @TableField(exist=false)
    @ApiModelProperty(value = "所属分类名称")
    private String categoryTitle;
}