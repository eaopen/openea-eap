package org.openea.eap.extj.permission.model.user.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 
 */
@Data
public class UserExportVO implements Serializable {
    @Excel(name = "账号", isImportField = "true")
    private String account;
    @Excel(name = "姓名", isImportField = "true")
    private String realName;
    /**
     * 组织
     */
    @Excel(name = "所属组织", isImportField = "true")
    private String organizeId;
    /**
     * 主管
     */
    @Excel(name = "直属主管", isImportField = "true")
    private String managerId;
    /**
     * 岗位
     */
    @Excel(name = "岗位", isImportField = "true")
    private String positionId;
    /**
     * 角色
     */
    @Excel(name = "角色", isImportField = "true")
    private String roleId;
    @Excel(name = "说明", isImportField = "true")
    private String description;
    /**
     * 性别
     */
    @Excel(name = "性别", isImportField = "true")
    private String gender;
    @Excel(name = "民族", isImportField = "true")
    private String nation;
    @Excel(name = "籍贯", isImportField = "true")
    private String nativePlace;
    @Excel(name = "证件类型", isImportField = "true")
    private String certificatesType;
    @Excel(name = "证件号码", isImportField = "true")
    private String certificatesNumber;
    @Excel(name = "文化程度", isImportField = "true")
    private String education;
    @Excel(name = "生日", isImportField = "true")
    private String birthday;
    @Excel(name = "电话", isImportField = "true")
    private String telePhone;
    @Excel(name = "固定电话", isImportField = "true")
    private String landline;
    @Excel(name = "手机", isImportField = "true")
    private String mobilePhone;
    @Excel(name = "邮箱", isImportField = "true")
    private String email;
    @Excel(name = "紧急联系人", isImportField = "true")
    private String urgentContacts;
    @Excel(name = "紧急电话", isImportField = "true")
    private String urgentTelePhone;
    @Excel(name = "通讯地址", isImportField = "true")
    private String postalAddress;
    @Excel(name = "排序码", isImportField = "true")
    private Long sortCode;
    @Excel(name = "状态", isImportField = "true")
    private String enabledMark;
    /**
     * 入职时间
     */
    @Excel(name = "入职日期", isImportField = "true")
    private String entryDate;

    private List<UserExportVO> list;
}
