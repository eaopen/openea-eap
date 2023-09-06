package org.openea.eap.extj.service;

import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.exception.LoginException;
import org.springframework.core.Ordered;

public interface UserDetailService extends Ordered {

    static final String USER_DETAIL_PREFIX = "USERDETAIL_";

    /**
     * 获取用户信息
     * @param userInfo
     * @return UserEntity
     * @param <T>
     */
    <T> T loadUserEntity(UserInfo userInfo) throws LoginException;

}
