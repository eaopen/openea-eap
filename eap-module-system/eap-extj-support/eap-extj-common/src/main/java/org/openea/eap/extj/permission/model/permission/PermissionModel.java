package org.openea.eap.extj.permission.model.permission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.openea.eap.extj.util.treeutil.SumTree;


/**
 * 个人信息设置 我的组织/我的岗位/（我的角色：暂无）
 *
 * 
 */
@Data
public class PermissionModel extends SumTree {

    @Schema(description = "名称")
    private String fullName;
    @Schema(description = "id")
    private String id;
    @Schema(description = "是否为默认")
    private Boolean isDefault;

}
