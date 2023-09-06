package org.openea.eap.extj.consts;


import cn.dev33.satoken.id.SaIdUtil;

public class AuthConsts {

    public static final String DEF_TENANT_ID = "";
    public static final String DEF_TENANT_DB = "";

    public static final String PAR_GRANT_TYPE = "grant_type";

    public static final String SYSTEM_INFO = "system_info";

    /**
     * 跨服务调用验证KEY
     */
    public static final String INNER_TOKEN_KEY = SaIdUtil.ID_TOKEN;

    public static final String TENANT_SESSION = "tenant:";

    public static final String TOKEN_PREFIX = "bearer";
    public static final String TOKEN_PREFIX_SP = TOKEN_PREFIX + " ";

    public static final String PARAMS_JNPF_TICKET = "jnpf_ticket";
    public static final String PARAMS_SSO_LOGOUT_TICKET = "ticket";

    public static final Integer REDIRECT_PAGETYPE_LOGIN = 1;
    public static final Integer REDIRECT_PAGETYPE_LOGOUT = 2;

    public static final Integer TMP_TOKEN_UNLOGIN = -1;
    public static final Integer TMP_TOKEN_ERRLOGIN = -2;

    public static final String ONLINE_TICKET_KEY = "online_ticket:";
    public static final String ONLINE_TICKET_TOKEN = "online_token";

    public static final String JWT_SECRET = "WviMjFNC72VKwGqm5LPoheQo5XN9iN4d";

    /**
     * clientId
     */
    public static final String Client_Id = "Client_Id";



}
