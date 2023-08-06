package org.openea.eap.extj.message.model.message;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * 钉钉的部门模型
 */
@Data
public class DingTalkDeptModel {
    /**
     * 部门ID
     */
    @TableField("dept_id")
    private Long deptId;

    /**
     * 上级部门(必填项)
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 部门名称(必填项)
     */
    @TableField("name")
    private String name;

    /**
     * 是否隐藏本部门 false（默认值）
     */
    @TableField("hide_dept")
    private Boolean hideDept;

    /**
     * 指定可以查看本部门的其他部门列表，总数不能超过200
     * 当hide_dept为true时，则此值生效
     */
    @TableField("dept_permits")
    private String deptPermits;

    /**
     * 指定可以查看本部门的人员userid列表，总数不能超过200
     * 当hide_dept为true时，则此值生效
     */
    @TableField("user_permits")
    private String userPermits;

    /**
     * 是否限制本部门成员查看通讯录 false（默认值）
     */
    @TableField("outer_dept")
    private Boolean outerDept;

    /**
     * 本部门成员是否只能看到所在部门及下级部门通讯录：
     * true：只能看到所在部门及下级部门通讯录
     * false：不能查看所有通讯录，在通讯录中仅能看到自己
     * 当outer_dept为true时，此参数生效
     */
    @TableField("outer_dept_only_self")
    private Boolean outerDeptOnlySelf;

    /**
     * 指定本部门成员可查看的通讯录用户userid列表，总数不能超过200。
     * 当outer_dept为true时，此参数生效
     */
    @TableField("outer_permit_users")
    private String outerPermitUsers;

    /**
     * 指定本部门成员可查看的通讯录部门ID列表，总数不能超过200
     * 当outer_dept为true时，此参数生效
     */
    @TableField("outer_permit_depts")
    private String outerPermitDepts;

    /**
     * 是否创建一个关联此部门的企业群，默认为false即不创建
     */
    @TableField("create_dept_group")
    private Boolean createDeptGroup;

    /**
     * 在父部门中的排序值，order值小的排序靠前
     */
    @TableField("order")
    private Long order;

    /**
     * 部门标识字段，开发者可用该字段来唯一标识一个部门，并与钉钉外部通讯录里的部门做映射
     */
    @TableField("source_identifier")
    private String sourceIdentifier;

    /**
     * 扩展属性，Json格式
     */
    @TableField("extension")
    private String extension;

    /**
     * 当部门群已经创建后，有新人加入部门时是否会自动加入该群
     */
    @TableField("auto_add_user")
    private Boolean autoAddUser;

    /**
     * 部门的主管userid列表
     */
    @TableField("dept_manager_userid_list")
    private String deptManagerUseridList;

    /**
     * 部门群是否包含子部门(没什么用)
     */
    @TableField("group_contain_sub_dept")
    private Boolean groupContainSubDept;

    /**
     * 部门群是否包含外包部门(没什么用)
     */
    @TableField("group_contain_outer_dept")
    private Boolean groupContainOuterDept;

    /**
     * 部门群是否包含隐藏部门(没什么用)
     */
    @TableField("group_contain_hidden_dept")
    private Boolean groupContainHiddenDept;

    /**
     * 企业群群主的userid(没什么用)
     */
    @TableField("org_dept_owner")
    private String orgDeptOwner;

}
