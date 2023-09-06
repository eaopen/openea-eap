package org.openea.eap.extj.granter;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.config.ConfigValueUtil;
import org.openea.eap.extj.consts.DeviceType;
import org.openea.eap.extj.exception.LoginException;
import org.openea.eap.extj.model.BaseSystemInfo;
import org.openea.eap.extj.model.LoginTicketModel;
import org.openea.eap.extj.service.LoginService;
import org.openea.eap.extj.util.RedisUtil;
import org.openea.eap.extj.util.TenantProvider;
import org.openea.eap.extj.util.TicketUtil;
import org.openea.eap.extj.util.UserProvider;
import org.openea.eap.extj.util.data.DataSourceContextHolder;
import org.openea.eap.extj.consts.AuthConsts;
import org.openea.eap.extj.consts.LoginTicketStatus;
import org.openea.eap.extj.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.HashMap;
import java.util.Map;

import static org.openea.eap.extj.consts.AuthConsts.*;


public abstract class AbstractTokenGranter implements TokenGranter, Ordered {


    @Autowired
    protected LoginService loginService;
    @Autowired
    protected UserProvider userProvider;
    @Autowired
    protected ConfigValueUtil configValueUtil;
    @Autowired
    protected RedisUtil redisUtil;

    protected static PathMatcher pathMatcher = new AntPathMatcher();

    private String authenticationUrl;


    public AbstractTokenGranter(String authenticationUrl){
        this.authenticationUrl = authenticationUrl;
    }


    /**
     * 最终登录用户
     * @param userInfo 包含账户名, 登录方式
     * @return
     */
    protected String loginAccount(UserInfo userInfo, BaseSystemInfo baseSystemInfo) throws LoginException {
        try{
            //获取用户实现类接口名称
            userInfo.setUserDetailKey(getUserDetailKey());
            //获取登录信息
            userInfo = getUserInfo(userInfo, baseSystemInfo);
            //预登陆
            preLogin(userInfo, baseSystemInfo);
            //登录
            UserProvider.login(userInfo, getLoginModel(userInfo, baseSystemInfo));
        }catch(Exception e){
            try {
                loginFailure(userInfo, baseSystemInfo, e);
            }catch (Exception e1){
                throw e1;
            }
            throw e;
        }
        loginSuccess(userInfo, baseSystemInfo);
        //返回token信息
        return StpUtil.getTokenValueNotCut();
    }

    /**
     * 切换多租户
     * @param userInfo
     * @return userAccount, tenantId, tenandDb
     * @throws LoginException
     */
    protected UserInfo switchTenant(UserInfo userInfo) throws LoginException {
        String tenantId = DEF_TENANT_ID;
        String tenantDbConnectionString = DEF_TENANT_DB;
        Boolean isAssign = false;
        if (configValueUtil.isMultiTenancy()) {
            userInfo = loginService.getTenantAccount(userInfo);
            //设置租户
            tenantId = userInfo.getTenantId();
            tenantDbConnectionString = userInfo.getTenantDbConnectionString();
            isAssign = userInfo.isAssignDataSource();
            DataSourceContextHolder.setDatasource(tenantId, tenantDbConnectionString, isAssign);

        }
        userInfo.setTenantId(tenantId);
        userInfo.setTenantDbConnectionString(tenantDbConnectionString);
        userInfo.setAssignDataSource(isAssign);
        return userInfo;
    }

    /**
     * 获取系统配置
     * @param userInfo
     * @return
     */
    protected BaseSystemInfo getSysconfig(UserInfo userInfo) throws LoginException {
        BaseSystemInfo baseSystemInfo = loginService.getBaseSystemConfig(userInfo.getTenantId(), userInfo.getTenantDbConnectionString(), userInfo.isAssignDataSource());
        if(baseSystemInfo != null && baseSystemInfo.getSingleLogin() != null){
            TenantProvider.setBaseSystemInfo(baseSystemInfo);
        }else{
            if (configValueUtil.isMultiTenancy() && configValueUtil.getMultiTenancyUrl().contains("https")) {
                throw new LoginException("租户登录失败，请用手机验证码登录");
            } else {
                throw new LoginException("数据库异常，请联系管理员处理");
            }
        }
        return baseSystemInfo;
    }

    /**
     * 获取登录设备
     * @return
     */
    protected DeviceType getDeviceType(){
        return UserProvider.getDeviceForAgent();
    }


    /**
     * 生成登录用户信息
     * @param userInfo
     * @return
     */
    protected UserInfo getUserInfo(UserInfo userInfo, BaseSystemInfo sysConfigInfo) throws LoginException {
        userInfo.setGrantType(getGrantType());
        userInfo = loginService.userInfo(userInfo, sysConfigInfo);
        return userInfo;
    }


    /**
     * 登录前执行
     * @param userInfo
     * @param baseSystemInfo
     */
    protected void preLogin(UserInfo userInfo, BaseSystemInfo baseSystemInfo) throws LoginException{

    }

    /**
     * 登录成功触发
     * @param userInfo
     * @param baseSystemInfo
     */
    protected void loginSuccess(UserInfo userInfo, BaseSystemInfo baseSystemInfo){

    }

    /**
     * 登录失败触发
     * @param baseSystemInfo
     */
    protected void loginFailure(UserInfo userInfo, BaseSystemInfo baseSystemInfo, Exception e){

    }

    protected abstract String getUserDetailKey();

    protected String createToken(UserInfo userInfo, BaseSystemInfo baseSystemInfo){
        //登录
        UserProvider.login(userInfo, getLoginModel(userInfo, baseSystemInfo));
        return StpUtil.getTokenValueNotCut();
    }

    /**
     * 更新轮询结果为成功
     */
    protected void updateTicketSuccess(UserInfo userInfo){
        String ticket = getJnpfTicket();
        if(!ticket.isEmpty()) {
            LoginTicketModel loginTicketModel = new LoginTicketModel()
                    .setStatus(LoginTicketStatus.Success.getStatus())
                    .setValue(StpUtil.getTokenValueNotCut())
                    .setTheme(userInfo.getTheme());
            TicketUtil.updateTicket(ticket, loginTicketModel, null);
        }
    }

    /**
     * 更新轮询结果为失败
     */
    protected void updateTicketError(String msg){
        String ticket = getJnpfTicket();
        if(!ticket.isEmpty()) {
            LoginTicketModel loginTicketModel = new LoginTicketModel()
                    .setStatus(LoginTicketStatus.ErrLogin.getStatus())
                    .setValue(msg);
            TicketUtil.updateTicket(ticket, loginTicketModel, null);
        }
    }


    /**
     * 获取轮询ticket
     * @return
     */
    protected String getJnpfTicket(){
        return SaHolder.getRequest().getParam(AuthConsts.PARAMS_JNPF_TICKET, "");
    }

    protected boolean isValidJnpfTicket(){
        String jnpfTicket = getJnpfTicket();
        if(!jnpfTicket.isEmpty()){
            LoginTicketModel loginTicketModel = TicketUtil.parseTicket(jnpfTicket);
            if(loginTicketModel == null){
                return false;
            }
        }
        return true;
    }

    /**
     * 获取登录参数
     * @param userInfo
     * @param baseSystemInfo
     * @return
     */
    protected SaLoginModel getLoginModel(UserInfo userInfo, BaseSystemInfo baseSystemInfo){
        SaLoginModel loginModel = new SaLoginModel();
        loginModel.setTimeout(userInfo.getTokenTimeout() * 60L);
        loginModel.setExtraData(getTokenExtraData(userInfo, baseSystemInfo));
        if(userInfo.getLoginDevice() == null) {
            loginModel.setDevice(getDeviceType().getDevice());
            userInfo.setLoginDevice(loginModel.device);
        }else{
            loginModel.setDevice(userInfo.getLoginDevice());
        }
        return loginModel;
    }

    /**
     * 获取额外的JWT内容
     * @param userInfo
     * @param baseSystemInfo
     * @return
     */
    protected Map<String, Object> getTokenExtraData(UserInfo userInfo, BaseSystemInfo baseSystemInfo){
        Map<String, Object> tokenInfo = new HashMap<>();
//        tokenInfo.put("token", StpUtil.getTokenValue());
        tokenInfo.put("singleLogin", baseSystemInfo.getSingleLogin());
        tokenInfo.put("user_name", userInfo.getUserAccount());
        tokenInfo.put("user_id", userInfo.getUserId());
        tokenInfo.put("exp", userInfo.getOverdueTime().getTime());
        tokenInfo.put("token", userInfo.getId());
        return tokenInfo;
    }

    @Override
    public ActionResult logout() {
        UserProvider.logout();
        return ActionResult.success();
    }

    protected abstract String getGrantType();


    @Override
    public boolean requiresAuthentication() {
        String path = SaHolder.getRequest().getRequestPath();
        if(path != null && path.startsWith("/api/oauth")){
            path = path.replace("/api/oauth", "");
        }
        return pathMatcher.match(authenticationUrl, path);
    }
}
