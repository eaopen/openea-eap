package org.openea.eap.extj.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class BaseSystemInfo implements Serializable {

    private Integer singleLogin;
    private Integer passwordErrorsNumber;
    private Integer lockType;
    private Integer lockTime;
    private Integer enableVerificationCode;
    private Integer verificationCodeNumber;
    private String tokenTimeout;
    private Integer lastLoginTimeSwitch = 0;
    private String companyTelePhone;
    private String wxGzhAppId;
    private String companyAddress;
    private String wxGzhAppSecret;
    private String qyhCorpSecret;
    private String isLog;
    private String emailSmtpPort;
    private String emailPop3Host;
    private String emailSenderName;
    private String companyEmail;
    private String sysName;
    private String copyright;
    private String qyhAgentId;
    private String lastLoginTime;
    private String emailAccount;
    private String qyhJoinUrl;
    private String whitelistSwitch;
    private String pageSize;
    private String sysDescription;
    private String emailPassword;
    private String companyContacts;
    private String sysTheme;
    private String qyhAgentSecret;
    private String whitelistIp;
    private String companyCode;
    private String emailSsl;
    private String emailSmtpHost;
    private String registerKey;
    private String wxGzhToken;
    private String qyhJoinTitle;
    private String qyhCorpId;
    private String sysVersion;
    private String emailPop3Port;
    private String companyName;
    private String wxGzhUrl;
    private Integer qyhIsSynOrg;
    private Integer qyhIsSynUser;
    private String dingSynAppKey;
    private String dingSynAppSecret;
    private Integer dingSynIsSynOrg;
    private Integer dingSynIsSynUser;
    private String loginIcon;
    private String logoIcon;
    private String appIcon;
    private String navigationIcon;
    private String dingDepartment;
    private String linkTime;
    private Integer isClick;
    private Integer unClickNum;
    private Integer passwordIsUpdatedRegularly;
    private Integer updateCycle;
    private Integer updateInAdvance;
    @Schema(
            description = "窗口标题"
    )
    private String title;
}
