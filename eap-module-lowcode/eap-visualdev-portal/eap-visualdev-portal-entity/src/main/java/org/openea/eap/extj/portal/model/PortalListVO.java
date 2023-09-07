package org.openea.eap.extj.portal.model;
import lombok.Data;

import java.util.List;

/**
 *
 *
 */
@Data
public class PortalListVO{
    private String id;
    private Long num;
    private String fullName;
    private String enCode;
    private Integer enabledMark;
    private Long creatorTime;
    private String creatorUser;
    private Long lastModifyTime;
    private String lastModifyUser;
    private Long sortCode;
    private List<PortalListVO> children;
    private Integer enabledLock;
}
