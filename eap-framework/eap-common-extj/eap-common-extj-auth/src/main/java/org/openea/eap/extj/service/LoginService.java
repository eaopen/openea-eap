package org.openea.eap.extj.service;

import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.exception.LoginException;
import org.openea.eap.extj.model.BaseSystemInfo;
import org.openea.eap.extj.model.login.PcUserVO;

/**
 * 登陆业务层
 *
 * 
 */
public interface LoginService {

    /**
     * 租戶登录验证
     *
     * @param userInfo
     * @return userAccount, tenantId, tenandDb
     * @throws LoginException
     */
    UserInfo getTenantAccount(UserInfo userInfo) throws LoginException;

    /**
     * 生成用户登录信息
     * @param userInfo   账户信息
     * @param sysConfigInfo 系统配置
     * @return
     * @throws LoginException
     */
    UserInfo userInfo(UserInfo userInfo, BaseSystemInfo sysConfigInfo) throws LoginException;

    /**
     * 获取用户登陆信息
     *
     * @return
     */
    PcUserVO getCurrentUser(String type);

    void updatePasswordMessage();

    /**
     *
     * @param tenantId
     * @param tenantDb
     * @param isAssignDataSource 是否租户指定数据源
     * @return
     */
    BaseSystemInfo getBaseSystemConfig(String tenantId, String tenantDb, boolean isAssignDataSource);



}
