package org.openea.eap.extj.base.model;

import lombok.Data;
import org.openea.eap.extj.util.treeutil.SumTree;

/**
 * 类功能
 *
 * 
 */
@Data
public class PrintTableTreeModel extends SumTree<PrintTableTreeModel> {

    private String fullName;

}
