package org.openea.eap.extj.permission.model.user.form;

import lombok.Data;

/**
 *
 *
 */
@Data
public class UserInfoForm {
    private String signature;
    private int gender;
    private String nation;
    private String nativePlace;
    private String entryDate;
    private String certificatesType;
    private String certificatesNumber;
    private String education;
    private Long birthday;
    private String telePhone;
    private String landline;
    private String mobilePhone;
    private String email;
    private String urgentContacts;
    private String urgentTelePhone;
    private String postalAddress;
    private String realName;
}
