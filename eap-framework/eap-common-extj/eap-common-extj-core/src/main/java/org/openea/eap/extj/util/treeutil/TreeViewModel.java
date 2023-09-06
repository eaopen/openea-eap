package org.openea.eap.extj.util.treeutil;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 *
 *
 */
@Data
public class TreeViewModel {
    @ApiModelProperty(value = "主键")
    private String id;
    private String code;
    @ApiModelProperty(value = "名称")
    private String text;
    private String title;
    @ApiModelProperty(value = "父主键")
    private String parentId;
    @ApiModelProperty(value = "选中")
    private Integer checkstate;
    private Boolean showcheck = true;
    private Boolean isexpand = true;
    private Boolean complete = true;
    @ApiModelProperty(value = "图标")
    private String img;
    @ApiModelProperty(value = "样式")
    private String cssClass;
    @ApiModelProperty(value = "是否有下级菜单")
    private Boolean hasChildren;
    @ApiModelProperty(value = "其他")
    private Map<String, Object> ht;
    @ApiModelProperty(value = "是否点击")
    private Boolean click;
    @ApiModelProperty(value = "下级菜单列表")
    private List<TreeViewModel> childNodes;
}
