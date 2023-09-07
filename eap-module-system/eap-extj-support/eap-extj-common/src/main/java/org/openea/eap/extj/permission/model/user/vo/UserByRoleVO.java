package org.openea.eap.extj.permission.model.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 
 */
@Data
public class UserByRoleVO implements Serializable {
    @Schema(description = "主键")
    private String id;
    @Schema(description = "有效标志")
    private Integer enabledMark;
    @Schema(description = "名称")
    private String fullName;
    @Schema(description = "类型")
    private String type;
    @Schema(description = "头像")
    private String headIcon;

    @Schema(description = "是否含有子类对象集合")
    private Boolean hasChildren;
    @Schema(description = "是否含有子类对象集合反值")
    private Boolean isLeaf;
    @Schema(description = "图标")
    private String icon;
    @Schema(description = "父节点ID")
    private String parentId;
    @Schema(description = "数量")
    private Long num;
    @Schema(description = "组织")
    private String organize;
    @Schema(description = "性别")
    private Integer gender;

    @Schema(description = "子类对象集合")
    private List<UserByRoleVO> children;
}
