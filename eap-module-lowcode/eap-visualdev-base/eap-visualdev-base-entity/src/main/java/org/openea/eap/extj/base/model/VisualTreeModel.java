package org.openea.eap.extj.base.model;

import lombok.Data;
import org.openea.eap.extj.util.treeutil.SumTree;


@Data
public class VisualTreeModel extends SumTree {
    private String fullName;
    private Long num;
    private String enCode;
    private Integer state;
    private String type;
    private String tables;
    private Long creatorTime;
    private String creatorUser;
    private Long lastModifyTime;
    private String lastModifyUser;
    private Long sortCode;
}
