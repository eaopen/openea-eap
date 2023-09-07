package org.openea.eap.extj.permission.model.user.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 错误报告时使用的
 *
 * 
 */
@Data
public class UserExportExceptionVO implements Serializable {

    private String account;
    private String realName;
    /**
     * 组织
     */
    private String organizeId;
    /**
     * 主管
     */
    private String managerId;
    /**
     * 岗位
     */
    private String positionId;
    /**
     * 角色
     */
    private String roleId;
    private String description;
    /**
     * 性别
     */
    private String gender;
    private String nation;
    private String nativePlace;
    private String certificatesType;
    private String certificatesNumber;
    private String education;
    private String birthday;
    private String telePhone;
    private String landline;
    private String mobilePhone;
    private String email;
    private String urgentContacts;
    private String urgentTelePhone;
    private String postalAddress;
    private Long sortCode;
    private String enabledMark;
    /**
     * 入职时间
     */
    private String entryDate;
}
