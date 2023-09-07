package org.openea.eap.extj.base.model.dataInterface;


import lombok.Data;
import org.openea.eap.extj.util.treeutil.SumTree;

/**
 *

 */
@Data
public class DataInterfaceTreeModel extends SumTree {
    private String fullName;
    private String categoryId;
    private String requestParameters;
}
