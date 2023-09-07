package org.openea.eap.extj.portal.model;


import lombok.Data;
import org.openea.eap.extj.util.treeutil.SumTree;


@Data
public class PortalTreeModel extends SumTree {
    private String fullName;
    private Long num;
    private String enCode;
    private Long creatorTime;
    private Integer enabledMark;
    private String creatorUser;
    private Long lastModifyTime;
    private String lastModifyUser;
    private Long sortCode;
}
