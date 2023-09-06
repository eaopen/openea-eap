package org.openea.eap.extj.model.login;

import org.openea.eap.extj.util.treeutil.SumTree;
import lombok.Data;

/**
 *
 *
 */
@Data
public class UserMenuModel extends SumTree{
    private String id;
    private String fullName;
    private Integer isButtonAuthorize;
    private Integer isColumnAuthorize;
    private Integer isDataAuthorize;
    private Integer isFormAuthorize;
    private String enCode;
    private String parentId;
    private String icon;
    private String urlAddress;
    private String linkTarget;
    private Integer type;
    private Boolean isData;
    private Integer enabledMark;
    private Long sortCode;
    private String category;
    private String description;
    private String propertyJson;

    private String systemId;
}
