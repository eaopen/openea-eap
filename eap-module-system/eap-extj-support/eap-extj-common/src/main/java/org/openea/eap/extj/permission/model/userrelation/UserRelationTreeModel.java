package org.openea.eap.extj.permission.model.userrelation;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.openea.eap.extj.util.treeutil.SumTree;

/**
 *
 *
 */
@Data
public class UserRelationTreeModel extends SumTree {

    @Schema(description = "主键")
    private String id;
    @Schema(description = "名称")
    private String fullName;
    @Schema(description = "是否有子节点")
    private Boolean hasChildren;
    @JSONField(name="category")
    private String type;
}
