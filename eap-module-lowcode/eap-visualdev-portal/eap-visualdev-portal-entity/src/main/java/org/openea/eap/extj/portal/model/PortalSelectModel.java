package org.openea.eap.extj.portal.model;

import lombok.Data;
import org.openea.eap.extj.util.treeutil.SumTree;


@Data
public class PortalSelectModel extends SumTree {

    private String fullName;
    private Long sortCode;

//    @JSONField(name="category")
//    private String  parentId;
}
