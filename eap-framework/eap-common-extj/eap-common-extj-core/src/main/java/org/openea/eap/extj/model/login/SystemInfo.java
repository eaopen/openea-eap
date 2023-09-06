package org.openea.eap.extj.model.login;

import lombok.Data;

/**
 * 登陆时返回系统配置信息
 *
 *
 */
@Data
public class SystemInfo {
    /**
     * 系统名称
     */
    public String sysName;

    /**
     * 系统版本
     */
    public String sysVersion;

    /**
     * 登录图标
     */
    public String loginIcon;

    /**
     * 版权信息
     */
    public String copyright;

    /**
     * 公司名称
     */
    public String companyName;

    /**
     * 导航图标
     */
    public String navigationIcon;

    /**
     * logo图标
     */
    public String logoIcon;

    /**
     * App图标
     */
    public String appIcon;
}
