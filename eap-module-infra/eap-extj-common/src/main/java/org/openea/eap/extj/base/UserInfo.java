package org.openea.eap.extj.base;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * todo 需要同eap user转换
 */
@Data
public class UserInfo implements Serializable {

    private String id;
    private String userId;
    private String userAccount;
    private String userName;
    private String userIcon;
    private String userGender;
    private String theme;
    private String organizeId;
    private String departmentId;
    private String managerId;
    private String[] subOrganizeIds;
    private List<String> subordinateIds;
    private String[] positionIds;
    private List<String> roleIds;
    private String loginTime;
    private String loginIpAddress;
    private String loginIpAddressName;
    private String macAddress;
    private String loginPlatForm;
    private String loginDevice;
    private Date prevLoginTime;
    private String prevLoginIpAddress;
    private String prevLoginIpAddressName;
    private Boolean isAdministrator = true;
    private Date overdueTime;
    private Integer tokenTimeout;
    private String tenantId;
    private String tenantDbConnectionString;
    private boolean assignDataSource;
    private String portalId;
    private String systemId;
    private String appSystemId;
    private String grantType;
    private String onlineTicket;
    private String token;
    private String userDetailKey;
}
