package org.openea.eap.extj.model.login;

import io.swagger.annotations.ApiModelProperty;
import org.openea.eap.extj.util.treeutil.SumTree;
import lombok.Data;

@Data
public class AllUserMenuModel extends SumTree {
    @ApiModelProperty(value = "名称")
    private String fullName;
    @ApiModelProperty(value = "菜单编码")
    private String enCode;
    @ApiModelProperty(value = "图标")
    private String icon;
    @ApiModelProperty(value = "菜单地址")
    private String urlAddress;
    @ApiModelProperty(value = "链接目标")
    private String linkTarget;
    @ApiModelProperty(value = "菜单分类【1-类别、2-页面】")
    private Integer type;
    private String propertyJson;
    private Long sortCode;

    private String systemId;
}
