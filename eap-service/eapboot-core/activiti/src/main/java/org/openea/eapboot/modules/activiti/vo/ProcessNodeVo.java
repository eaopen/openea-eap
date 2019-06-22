package org.openea.eapboot.modules.activiti.vo;

import org.openea.eapboot.modules.base.entity.Department;
import org.openea.eapboot.modules.base.entity.Role;
import org.openea.eapboot.modules.base.entity.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 */
@Data
public class ProcessNodeVo {

    @ApiModelProperty(value = "节点id")
    private String id;

    @ApiModelProperty(value = "节点名")
    private String title;

    @ApiModelProperty(value = "节点类型 0开始 1用户任务 2结束")
    private Integer type;

    @ApiModelProperty(value = "关联角色")
    private List<Role> roles;

    @ApiModelProperty(value = "关联用户")
    private List<User> users;

    @ApiModelProperty(value = "关联部门")
    private List<Department> departments;

    @ApiModelProperty(value = "节点展开 前端所需")
    private Boolean expand = true;
}
