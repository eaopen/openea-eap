package org.openea.eap.extj.permission.model.user.mod;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 导入模型
 *
 * 
 */
@Data
public class UserImportModel implements Serializable {
    private String account;

    private String realName;

    private String organizeId;

    private String managerId;

    private String positionId;

    private String roleId;

    private String description;

    private Integer gender;

    private String nation;

    private String nativePlace;

    private String certificatesType;

    private String certificatesNumber;

    private String education;

    private Date birthday;

    private String telePhone;

    private String landline;

    private String mobilePhone;

    private String email;

    private String urgentContacts;

    private String urgentTelePhone;

    private String postalAddress;

    private Long sortCode;

    private Date entryDate;

    private Integer enabledMark;
}
