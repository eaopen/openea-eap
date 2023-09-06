package org.openea.eap.extj.model.login;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 *
 *
 */
@Data
public class MenuSelectVO {
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "父主键")
    private String parentId;
    @ApiModelProperty(value = "名称")
    private String fullName;
    @ApiModelProperty(value = "是否按钮权限")
    private Integer isButtonAuthorize;
    @ApiModelProperty(value = "是否列表权限")
    private Integer isColumnAuthorize;
    @ApiModelProperty(value = "是否数据权限")
    private Integer isDataAuthorize;
    @ApiModelProperty(value = "排序码")
    private Long sortCode;
    @ApiModelProperty(value = "图标")
    private String icon;
    @ApiModelProperty(value = "是否有下级菜单")
    private Boolean hasChildren;
    @ApiModelProperty(value = "下级菜单列表")
    private List<MenuSelectVO> children;

    @ApiModelProperty(value = "外链")
    private String linkTarget;
    @ApiModelProperty(value = "编码")
    private String enCode;


    @ApiModelProperty(value = "系统id")
    private String systemId;

    @ApiModelProperty(value = "分类")
    private Integer type;
}
