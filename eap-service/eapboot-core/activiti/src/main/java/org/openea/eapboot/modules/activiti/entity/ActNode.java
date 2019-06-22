package org.openea.eapboot.modules.activiti.entity;

import org.openea.eapboot.base.EapBaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 */
@Data
@Entity
@Table(name = "t_act_node")
@TableName("t_act_node")
@ApiModel(value = "节点")
public class ActNode extends EapBaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "节点id")
    private String nodeId;

    @ApiModelProperty(value = "节点关联类型 0角色 1用户 2部门")
    private Integer type;

    @ApiModelProperty(value = "关联其他表id")
    private String relateId;

}