package org.openea.eap.module.visualdev.base.model.dataInterface;


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
