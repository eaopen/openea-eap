package org.openea.eap.module.visualdev.portal.model;

import com.alibaba.fastjson.annotation.JSONField;
import org.openea.eap.extj.util.treeutil.SumTree;
import lombok.Data;

/**
 *
 */
@Data
public class PortalSelectModel extends SumTree {
    private String fullName;
    private Long sortCode;
    @JSONField(name="category")
    private String  parentId;
}
