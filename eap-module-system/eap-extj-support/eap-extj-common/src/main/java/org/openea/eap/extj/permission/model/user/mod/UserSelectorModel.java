package org.openea.eap.extj.permission.model.user.mod;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.openea.eap.extj.util.treeutil.SumTree;

/**
 *
 *
 */
@Data
public class UserSelectorModel extends SumTree {
    @JSONField(name="category")
    private String type;
    private String fullName;
    @Schema(description = "状态")
    private Integer enabledMark;
    @Schema(description = "图标")
    private String icon;
}
