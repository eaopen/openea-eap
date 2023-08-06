package org.openea.eap.extj.util;

import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.consts.DeviceType;
import org.springframework.util.ObjectUtils;

//import cn.dev33.satoken.id.SaIdUtil;
//import cn.dev33.satoken.session.SaSession;
//import cn.dev33.satoken.session.TokenSign;
//import cn.dev33.satoken.stp.SaLoginModel;
//import cn.dev33.satoken.stp.StpUtil;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * todo auth
 */

@Component
public class UserProvider {
    private static final Logger log = LoggerFactory.getLogger(UserProvider.class);
//    private static RedisUtil redisUtil;
//    private static CacheKeyUtil cacheKeyUtil;
    public static final String USER_INFO_KEY = "userInfo";
    private static final ThreadLocal<UserInfo> USER_CACHE = new ThreadLocal();

//    public UserProvider(RedisUtil redisUtil, CacheKeyUtil cacheKeyUtil) {
//        UserProvider.redisUtil = redisUtil;
//        UserProvider.cacheKeyUtil = cacheKeyUtil;
//    }

    public static void login(UserInfo userInfo) {
        setLocalLoginUser(userInfo);
//        StpUtil.login(splicingLoginId(userInfo.getUserId()));
//        userInfo.setToken(StpUtil.getTokenValueNotCut());
//        setLoginUser(userInfo);
    }

//    public static void login(UserInfo userInfo, SaLoginModel loginModel) {
//        setLocalLoginUser(userInfo);
//        StpUtil.login(splicingLoginId(userInfo.getUserId()), loginModel);
//        userInfo.setToken(StpUtil.getTokenValueNotCut());
//        setLoginUser(userInfo);
//    }

    public static String getLoginUserId(String token) {
//        String loginId = (String)StpUtil.getLoginIdByToken(token);
//        return parseLoginId(loginId);
        return  null;
    }

    public static String getLoginUserId() {
        String loginId = getUser().getUserId();
        return parseLoginId(loginId);
    }

    public static String splicingLoginId(String userId) {
        return splicingLoginId(userId, (String)null);
    }

    private static String splicingLoginId(String userId, String tenantId) {
//        if (StringUtil.isEmpty(tenantId)) {
//            tenantId = DataSourceContextHolder.getDatasourceId();
//        }

        return !StringUtil.isEmpty(tenantId) ? tenantId + ":" + userId : userId;
    }

    private static String parseLoginId(String loginId) {
        if (loginId != null && loginId.contains(":")) {
            loginId = loginId.substring(loginId.indexOf(":") + 1);
        }

        return loginId;
    }

    public static Boolean isValidToken(String token) {
        UserInfo userInfo = getUser(token);
        return userInfo.getUserId() != null;
    }

    public static void setLoginUser(UserInfo userInfo) {
//        StpUtil.getTokenSession().set("userInfo", userInfo);
    }

    public static void setLocalLoginUser(UserInfo userInfo) {
        USER_CACHE.set(userInfo);
    }

    public static UserInfo getLocalLoginUser() {
        return (UserInfo)USER_CACHE.get();
    }

    public static void clearLocalUser() {
        USER_CACHE.remove();
    }

    public UserInfo get(String token) {
        return getUser(token);
    }

    public UserInfo get() {
        return getUser();
    }

    public static UserInfo getUser(String userId, String tenantId) {
        return getUser(userId, tenantId, (List)null, (List)null);
    }

    public static UserInfo getUser(String userId, String tenantId, List<String> includeDevice, List<String> excludeDevice) {
//        SaSession session = StpUtil.getSessionByLoginId(splicingLoginId(userId, tenantId), false);
//        if (session != null) {
//            List<TokenSign> tokenSignList = session.tokenSignListCopy();
//            if (!tokenSignList.isEmpty()) {
//                tokenSignList = (List)tokenSignList.stream().filter((tokenSign) -> {
//                    if (!ObjectUtils.isEmpty(excludeDevice) && excludeDevice.contains(tokenSign.getDevice())) {
//                        return false;
//                    } else {
//                        return ObjectUtils.isEmpty(includeDevice) || includeDevice.contains(tokenSign.getDevice());
//                    }
//                }).collect(Collectors.toList());
//                if (!tokenSignList.isEmpty()) {
//                    return getUser(((TokenSign)tokenSignList.get(0)).getValue());
//                }
//            }
//        }

        return new UserInfo();
    }

    public static UserInfo getUser(String token) {
        UserInfo userInfo = null;
        String tokens = null;
//        if (token != null) {
//            tokens = cutToken(token);
//        } else {
//            try {
//                tokens = StpUtil.getTokenValue();
//            } catch (Exception var4) {
//            }
//        }

//        if (tokens != null && StpUtil.getLoginIdByToken(tokens) != null) {
//            userInfo = (UserInfo)StpUtil.getTokenSessionByToken(tokens).get("userInfo");
//        }

        if (userInfo == null) {
            userInfo = new UserInfo();
        }

        return userInfo;
    }

    public static UserInfo getUser() {
        UserInfo userInfo = (UserInfo)USER_CACHE.get();
        if (userInfo != null) {
            return userInfo;
        } else {
            userInfo = getUser((String)null);
            if (userInfo.getUserId() != null) {
                USER_CACHE.set(userInfo);
            }

            return userInfo;
        }
    }

    public static String cutToken(String token) {
        if (token != null && token.startsWith("bearer ")) {
            token = token.substring("bearer ".length());
        }

        return token;
    }

    public static String getToken() {
        String toke = getAuthorize();
        return toke;
    }

    public static String getAuthorize() {
        String authorize = ServletUtil.getHeader("Authorization");
        return authorize;
    }

    public static void renewTimeout() {
//        if (StpUtil.getTokenValue() != null) {
//            UserInfo userInfo = getUser();
//            if (userInfo.getUserId() == null || userInfo.getTokenTimeout() == null) {
//                return;
//            }

//            StpUtil.renewTimeout((long)userInfo.getTokenTimeout() * 60L);
//            SaSession saSession = StpUtil.getSessionByLoginId(splicingLoginId(userInfo.getUserId()), false);
//            if (saSession != null) {
//                saSession.updateTimeout((long)userInfo.getTokenTimeout() * 60L);
//            }
//        }

    }

//    public static List<String> getLoginUserListToken() {
//        return (List)StpUtil.searchTokenValue("", -1, 0, true).stream().map((token) -> {
//            return token.replace(StpUtil.stpLogic.splicingKeyTokenValue(""), "");
//        }).collect(Collectors.toList());
//    }

    public static String getInnerAuthToken() {
//        return SaIdUtil.getToken();
        return null;
    }

    public static void checkInnerToken(String token) {
//        SaIdUtil.checkToken(token);
    }

    public static boolean isValidInnerToken(String token) {
//        return SaIdUtil.isValid(token);
        return true;
    }

//    public static void kickoutByUserId(String userId, String tenantId) {
//        StpUtil.kickout(splicingLoginId(userId, tenantId));
//    }

    public static void kickoutByToken(String... tokens) {
//        String[] var1 = tokens;
//        int var2 = tokens.length;

//        for(int var3 = 0; var3 < var2; ++var3) {
//            String token = var1[var3];
//            StpUtil.kickoutByTokenValue(token);
//        }

    }

    public static void logout() {
//        StpUtil.logout();
    }

    public static void logoutByToken(String token) {
        if (token == null) {
            logout();
        } else {
//            StpUtil.logoutByTokenValue(cutToken(token));
        }

    }

    public static void logoutByUserId(String userId, DeviceType deviceType) {
//        StpUtil.logout(splicingLoginId(userId), deviceType.getDevice());
    }

    public static void logoutByUserId(String userId) {
//        StpUtil.logout(splicingLoginId(userId));
        removeOtherCache(userId);
    }

    public static List<String> getPermissionList() {
//        return StpUtil.getPermissionList();
        return null;
    }

    public static List<String> getRoleList() {
//        return StpUtil.getRoleList();
        return null;
    }

    public static void removeOtherCache(String userId) {
//        redisUtil.remove(cacheKeyUtil.getUserAuthorize() + userId);
//        redisUtil.remove(cacheKeyUtil.getSystemInfo());
    }

    public boolean isOnLine(String userId) {
//        return StpUtil.getTokenValueByLoginId(splicingLoginId(userId), getDeviceForAgent().getDevice()) != null;
        return false;
    }

    public static boolean isLogined() {
//        return StpUtil.isLogin();
        return  false;
    }

    public static boolean isValid(String token) {
//        return StpUtil.getLoginIdByToken(token) != null;
        return true;
    }

    public static DeviceType getDeviceForAgent() {
        return ServletUtil.getIsMobileDevice() ? DeviceType.APP : DeviceType.PC;
    }

    public static boolean isTempUser(UserInfo userInfo) {
        if (userInfo == null) {
            userInfo = getUser();
        }

        return DeviceType.TEMPUSER.getDevice().equals(userInfo.getLoginDevice()) || DeviceType.TEMPUSERLIMITED.getDevice().equals(userInfo.getLoginDevice());
    }
}