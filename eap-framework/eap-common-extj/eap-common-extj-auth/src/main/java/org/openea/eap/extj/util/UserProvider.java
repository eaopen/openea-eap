package org.openea.eap.extj.util;

import cn.dev33.satoken.id.SaIdUtil;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.TokenSign;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.text.StrPool;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.consts.AuthConsts;
import org.openea.eap.extj.consts.DeviceType;
import org.openea.eap.extj.model.OnlineUserModel;
import org.openea.eap.extj.model.OnlineUserProvider;
import org.openea.eap.extj.util.data.DataSourceContextHolder;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.openea.eap.extj.consts.AuthConsts.TOKEN_PREFIX;
import static org.openea.eap.extj.consts.AuthConsts.TOKEN_PREFIX_SP;


@Slf4j
//@Component
//@ConditionalOnMissingBean
public class UserProvider {

    protected static RedisUtil redisUtil;
    protected static CacheKeyUtil cacheKeyUtil;

    public static final String USER_INFO_KEY = "userInfo";

    protected static final ThreadLocal<UserInfo> USER_CACHE = new ThreadLocal<>();

    public UserProvider(RedisUtil redisUtil, CacheKeyUtil cacheKeyUtil) {
        UserProvider.redisUtil = redisUtil;
        UserProvider.cacheKeyUtil = cacheKeyUtil;
    }



    // =================== 登录相关操作 ===================

    /**
     * 登录系统
     *
     * @param userInfo 登录用户信息
     */
    public static void login(UserInfo userInfo) {
        setLocalLoginUser(userInfo);
        StpUtil.login(splicingLoginId(userInfo.getUserId()));
        userInfo.setToken(StpUtil.getTokenValueNotCut());
        setLoginUser(userInfo);
    }

    /**
     * 登录系统
     *
     * @param userInfo   用户信息
     * @param loginModel 登录参数
     */
    public static void login(UserInfo userInfo, SaLoginModel loginModel) {
        setLocalLoginUser(userInfo);
        StpUtil.login(splicingLoginId(userInfo.getUserId()), loginModel);
        userInfo.setToken(StpUtil.getTokenValueNotCut());
        setLoginUser(userInfo);
    }


    // =================== 登录用户ID相关操作 ===================

    /**
     * 获取指定TOKEN用户ID
     *
     * @param token
     * @return
     */
    public static String getLoginUserId(String token) {
        String loginId = (String) StpUtil.getLoginIdByToken(token);
        return parseLoginId(loginId);
    }

    /**
     * 获取当前用户ID, 包含临时切换用户ID
     *
     * @return
     */
    public static String getLoginUserId() {
        String loginId = getUser().getUserId();
        return parseLoginId(loginId);
    }



    // =================== 用户ID拼接相关操作 ===================


    /**
     * 拼接租户下的用户ID
     *
     * @param userId
     * @return
     */
    public static String splicingLoginId(String userId) {
        return splicingLoginId(userId, null);
    }

    /**
     * 拼接租户下的用户ID
     * @param userId
     * @param tenantId
     * @return
     */
    protected static String splicingLoginId(String userId, String tenantId) {
        if(StringUtil.isEmpty(tenantId)){
            tenantId = DataSourceContextHolder.getDatasourceId();
        }
        if (!StringUtil.isEmpty(tenantId)) {
            return tenantId + StrPool.COLON + userId;
        }
        return userId;
    }

    /**
     * 解析租户下的登录ID
     * @param loginId
     * @return
     */
    private static String parseLoginId(String loginId) {
        if (loginId != null && loginId.contains(StrPool.COLON)) {
            loginId = loginId.substring(loginId.indexOf(StrPool.COLON) + 1);
        }
        return loginId;
    }

    /**
     * Token是否有效
     *
     * @param token
     * @return
     */
    public static Boolean isValidToken(String token) {
        UserInfo userInfo = getUser(token);
        return userInfo.getUserId() != null;
    }


    // =================== UserInfo缓存相关操作 ===================


    /**
     * 设置Redis用户数据
     */
    public static void setLoginUser(UserInfo userInfo) {
        StpUtil.getTokenSession().set(USER_INFO_KEY, userInfo);
    }

    /**
     * 设置本地用户数据
     */
    public static void setLocalLoginUser(UserInfo userInfo) {
        USER_CACHE.set(userInfo);
    }

    /**
     * 获取本地用户数据
     */
    public static UserInfo getLocalLoginUser() {
        return USER_CACHE.get();
    }

    /**
     * 清空本地用户数据
     */
    public static void clearLocalUser() {
        USER_CACHE.remove();
    }




    /**
     * 获取用户缓存
     * 保留旧方法
     *
     * @param token
     * @return
     */
    public UserInfo get(String token) {
        return UserProvider.getUser(token);
    }

    /**
     * 获取用户缓存
     *
     * @return
     */
    public UserInfo get() {
        return UserProvider.getUser();
    }


    /**
     * 根据用户ID, 租户ID获取随机获取一个UserInfo
     * @param userId
     * @param tenantId
     * @return
     */
    public static UserInfo getUser(String userId, String tenantId){
        return getUser(userId, tenantId, null, null);
    }

    /**
     * 根据用户ID, 租户ID, 设备类型获取随机获取一个UserInfo
     * @param userId
     * @param tenantId
     * @param includeDevice 指定的设备类型中查找
     * @param excludeDevice 排除指定设备类型
     * @return
     */
    public static UserInfo getUser(String userId, String tenantId, List<String> includeDevice, List<String> excludeDevice){
        SaSession session = StpUtil.getSessionByLoginId(splicingLoginId(userId, tenantId), false);
        if (session != null) {
            List<TokenSign> tokenSignList = session.tokenSignListCopy();
            if (!tokenSignList.isEmpty()) {
                tokenSignList = tokenSignList.stream().filter(tokenSign -> {
                    if(!ObjectUtils.isEmpty(excludeDevice)){
                        if(excludeDevice.contains(tokenSign.getDevice())){
                            return false;
                        }
                    }
                    if(!ObjectUtils.isEmpty(includeDevice)){
                        if(!includeDevice.contains(tokenSign.getDevice())){
                            return false;
                        }
                    }
                    return true;
                }).collect(Collectors.toList());
                if(!tokenSignList.isEmpty()){
                    return getUser(tokenSignList.get(0).getValue());
                }
            }
        }
        return new UserInfo();
    }

    /**
     * 获取用户缓存
     *
     * @param token
     * @return
     */
    public static UserInfo getUser(String token) {
        UserInfo userInfo = null;
        String tokens = null;
        if (token != null) {
            tokens = cutToken(token);
        } else {
            try {
                //处理非Web环境报错
                tokens = StpUtil.getTokenValue();
            } catch (Exception e) {
            }
        }
        if (tokens != null) {
            if (StpUtil.getLoginIdByToken(tokens) != null) {
                userInfo = (UserInfo) StpUtil.getTokenSessionByToken(tokens).get(USER_INFO_KEY);
            }
        }
        if (userInfo == null) {
            userInfo = new UserInfo();
        }
        return userInfo;
    }

    /**
     * 获取用户缓存
     *
     * @return
     */
    public static UserInfo getUser() {
//        if(StpUtil.getTokenValue() == null){
//            return  new UserInfo();
//        }
        UserInfo userInfo = USER_CACHE.get();
        if (userInfo != null) {
            return userInfo;
        }
        userInfo = UserProvider.getUser(null);
        if (userInfo.getUserId() != null) {
            USER_CACHE.set(userInfo);
        }
        return userInfo;
    }

    // =================== Token相关操作 ===================

    /**
     * 去除Token前缀
     *
     * @param token
     * @return
     */
    public static String cutToken(String token) {
        if (token != null && token.startsWith(TOKEN_PREFIX_SP)) {
            token = token.substring(TOKEN_PREFIX_SP.length());
        }
        return token;
    }

    /**
     * 获取token
     */
    public static String getToken() {
        String toke = getAuthorize();
        return toke;
    }


    /**
     * 获取Authorize
     */
    public static String getAuthorize() {
        String authorize = ServletUtil.getHeader(Constants.AUTHORIZATION);
        return authorize;
    }


    /**
     * TOKEN续期
     */
    public static void renewTimeout() {
        if (StpUtil.getTokenValue() != null) {
            UserInfo userInfo = UserProvider.getUser();
            if(userInfo.getUserId() == null) {
                //避免请求过网关之后TOKEN失效(携带TOKEN调用登录接口之后账号被顶替)
                return;
            }
            StpUtil.renewTimeout(userInfo.getTokenTimeout() * 60L);
            SaSession saSession = StpUtil.getSessionByLoginId(splicingLoginId(userInfo.getUserId()), false);
            if (saSession != null) {
                saSession.updateTimeout(userInfo.getTokenTimeout() * 60L);
            }
        }
    }

    /**
     * 获取所有Token记录
     * 包含无效状态的用户、临时用户
     *
     * @return
     */
    public static List<String> getLoginUserListToken() {
        return StpUtil.searchTokenValue("", -1, 0, true).stream().map(token -> token.replace(StpUtil.stpLogic.splicingKeyTokenValue(""), "")).collect(Collectors.toList());
    }


    // =================== 临时Token相关操作 ===================


    /**
     * 获取内部服务传递验证TOKEN
     *
     * @return
     */
    public static String getInnerAuthToken() {
        return SaIdUtil.getToken();
    }

    /**
     * 验证内部传递Token是否有效 抛出异常
     * @param token
     */
    public static void checkInnerToken(String token){
        SaIdUtil.checkToken(token);
    }

    /**
     * 验证内部传递Token是否有效
     * @param token
     */
    public static boolean isValidInnerToken(String token){
        return SaIdUtil.isValid(token);
    }


    // =================== 退出相关操作 ===================


    /**
     * 根据用户ID踢出全部用户
     * @param userId
     */
    public static void kickoutByUserId(String userId, String tenantId) {
        StpUtil.kickout(splicingLoginId(userId, tenantId));
    }

    /**
     * 根据Token踢出指定会话
     * @param tokens
     */
    public static void kickoutByToken(String... tokens) {
        for (String token : tokens) {
            StpUtil.kickoutByTokenValue(token);
        }
    }

    /**
     * 退出当前Token, 不清除用户其他系统缓存
     */
    public static void logout() {
        StpUtil.logout();

    }

    /**
     * 退出指定Token, 不清除用户其他系统缓存
     *
     * @param token
     */
    public static void logoutByToken(String token) {
        if (token == null) {
            logout();
        } else {
            StpUtil.logoutByTokenValue(cutToken(token));
        }
    }

    /**
     * 退出指定设备类型的用户的全部登录信息, 不清除用户其他系统缓存
     *
     * @param userId
     * @param deviceType
     */
    public static void logoutByUserId(String userId, DeviceType deviceType) {
        StpUtil.logout(splicingLoginId(userId), deviceType.getDevice());
    }

    /**
     * 退出指定用户的全部登录信息, 清除相关缓存
     *
     * @param userId
     */
    public static void logoutByUserId(String userId) {
        StpUtil.logout(splicingLoginId(userId));
        removeOtherCache(userId);

    }

    // =================== 用户权限 ===================

    /**
     * 获取当前用户拥有的权限列表(菜单编码列表、功能ID列表)
     * @return
     */
    public static List<String> getPermissionList(){
        return StpUtil.getPermissionList();
    }

    /**
     * 获取当前用户拥有的角色列表
     * @return
     */
    public static List<String> getRoleList(){
        return StpUtil.getRoleList();
    }



    // =================== Websocket相关操作 ===================

    /**
     * 根据Token精准推送Websocket 登出消息
     * @param token
     */
    public static void removeWebSocketByToken(String... token) {
        List<String> tokens = Arrays.stream(token).map(t -> t.contains(AuthConsts.TOKEN_PREFIX) ? t : TOKEN_PREFIX + " " + t).collect(Collectors.toList());
        //清除websocket登录状态
        List<OnlineUserModel> users = OnlineUserProvider.getOnlineUserList().stream().filter(t -> tokens.contains(t.getToken())).collect(Collectors.toList());
        if (!ObjectUtils.isEmpty(users)) {
            for (OnlineUserModel user : users) {
                OnlineUserProvider.logoutWS(user, null);
                //先移除对象， 并推送下线信息， 避免网络原因导致就用户未断开 新用户连不上WebSocket
                OnlineUserProvider.removeModel(user);
                //通知所有在线，有用户离线
                for (OnlineUserModel item : OnlineUserProvider.getOnlineUserList().stream().filter(t -> !Objects.equals(user.getUserId(), t.getUserId()) && !Objects.equals(user.getTenantId(),t.getTenantId())).collect(Collectors.toList())) {
                    if (!item.getUserId().equals(user.getUserId())) {
                        JSONObject obj = new JSONObject();
                        obj.put("method", "Offline");
                        //推送给前端
                        OnlineUserProvider.sendMessage(item, obj);

                    }
                }
            }
        }
    }

    /**
     * 根据用户ID 推送全部Websocket 登出消息
     * @param userId
     */
    public static void removeWebSocketByUser(String userId) {
        List<String> tokens = StpUtil.getTokenValueListByLoginId(splicingLoginId(userId));
        removeWebSocketByToken(tokens.toArray(new String[tokens.size()]));
    }


    // =================== 其他缓存相关操作 ===================

    /**
     * 移除
     */
    public static void removeOtherCache(String userId) {
        redisUtil.remove(cacheKeyUtil.getUserAuthorize() + userId);
        redisUtil.remove(cacheKeyUtil.getSystemInfo());
    }

    /**
     * 是否在线
     */
    public boolean isOnLine(String userId) {
        return StpUtil.getTokenValueByLoginId(splicingLoginId(userId), getDeviceForAgent().getDevice()) != null;
    }


    /**
     * 是否登陆
     */
    public static boolean isLogined() {
        return StpUtil.isLogin();
    }

    /**
     * 指定Token是否有效
     * @param token
     * @return
     */
    public static boolean isValid(String token) {
        return StpUtil.getLoginIdByToken(token) != null;
    }


    public static DeviceType getDeviceForAgent() {
        if (ServletUtil.getIsMobileDevice()) {
            return DeviceType.APP;
        } else {
            return DeviceType.PC;
        }
    }

    /**
     * 判断用户是否是临时用户
     * @param userInfo
     * @return
     */
    public static boolean isTempUser(UserInfo userInfo){
        if(userInfo == null){
            userInfo = getUser();
        }
        return DeviceType.TEMPUSER.equals(userInfo.getLoginDevice())
                || DeviceType.TEMPUSERLIMITED.equals(userInfo.getLoginDevice());
    }



}
