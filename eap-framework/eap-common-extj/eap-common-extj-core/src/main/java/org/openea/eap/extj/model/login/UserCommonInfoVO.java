package org.openea.eap.extj.model.login;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 *
 *
 */
@Data
public class UserCommonInfoVO {
    @ApiModelProperty(value = "用户id")
    private String userId;
    @ApiModelProperty(value = "用户账号")
    private String userAccount;
    @ApiModelProperty(value = "用户姓名")
    private String userName;
    @ApiModelProperty(value = "用户头像")
    private String headIcon;
    @ApiModelProperty(value = "组织主键")
    private String organizeId;
    @ApiModelProperty("组织主键集合")
    private List<String> organizeIdList;
    @ApiModelProperty(value = "组织名称")
    private String organizeName;
    @ApiModelProperty(value = "岗位")
    private List<UserPositionVO> positionIds;
    @ApiModelProperty(value = "系统集合")
    private List<UserSystemVO> systemIds;

    private String positionId;

    private String positionName;

    @ApiModelProperty(value = "上次登录")
    private Integer prevLogin;
    @ApiModelProperty(value = "上次登录时间",example = "1")
    private Long prevLoginTime;
    @ApiModelProperty(value = "上次登录IP")
    private String prevLoginIPAddress;
    @ApiModelProperty(value = "上次登录地址")
    private String prevLoginIPAddressName;
    @ApiModelProperty(value = "门户id")
    private String portalId;
    /**
     * 当前组织角色+全局角色 Id数组
     */
    private List<String> roleIds;

    /**
     * 当前组织角色+全局角色 名称集合用 , 号隔开
     */
    private String roleName;

    /**
     * 直属主管 (u.RealName + "/" + u.Account)
     */
    private String manager;

    /**
     * 手机
     */
    private String mobilePhone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 生日
     */
    private Long birthday;

    /**
     * 部门id
     */
    private String departmentId;

    /**
     * 部门名称 结构树
     */
    private String departmentName;

    private Boolean isAdministrator;

    private String signImg;

    private Date changePasswordDate;

    private String systemId;

    private String appSystemId;
}
