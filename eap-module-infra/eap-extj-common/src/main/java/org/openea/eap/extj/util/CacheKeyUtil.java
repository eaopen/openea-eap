package org.openea.eap.extj.util;

import org.openea.eap.extj.util.data.DataSourceContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CacheKeyUtil {
    private static final Logger log = LoggerFactory.getLogger(CacheKeyUtil.class);
    public static String SYSTEMINFO = "systeminfo";
    public static String WECHATCONFIG = "wechatconfig";
    public static String VALIDCODE = "validcode_";
    public static String SMSVALIDCODE = "sms_validcode_";
    public static String LOGINTOKEN = "login_token_";
    public static String LOGINONLINE = "login_online_";
    public static String MOBILELOGINONLINE = "login_online_mobile_";
    public static String MOBILEDEVICELIST = "mobiledevicelist";
    public static String USERAUTHORIZE = "authorize_";
    public static String COMPANYSELECT = "companyselect";
    public static String ORGANIZELIST = "organizeList";
    public static String DICTIONARY = "dictionary_";
    public static String DYNAMIC = "dynamic_";
    public static String POSITIONLIST = "positionlist_";
    public static String ALLUSER = "alluser";
    public static String VISIUALDATA = "visiualdata_";
    public static String IDGENERATOR = "idgenerator_";
    public static final String ORGANIZEINFOLIST = "organizeinfolist_";

    public CacheKeyUtil() {
    }

    public String getOrganizeInfoList() {
        String tenantId = DataSourceContextHolder.getDatasourceId();
        return !StringUtil.isEmpty(tenantId) ? tenantId + VISIUALDATA : "organizeinfolist_";
    }

    public String getVisiualData() {
        String tenantId = DataSourceContextHolder.getDatasourceId();
        return !StringUtil.isEmpty(tenantId) ? tenantId + VISIUALDATA : VISIUALDATA;
    }

    public String getCompanySelect() {
        String tenantId = DataSourceContextHolder.getDatasourceId();
        return !StringUtil.isEmpty(tenantId) ? tenantId + COMPANYSELECT : COMPANYSELECT;
    }

    public String getOrganizeList() {
        String tenantId = DataSourceContextHolder.getDatasourceId();
        return !StringUtil.isEmpty(tenantId) ? tenantId + ORGANIZELIST : ORGANIZELIST;
    }

    public String getDictionary() {
        String tenantId = DataSourceContextHolder.getDatasourceId();
        return !StringUtil.isEmpty(tenantId) ? tenantId + DICTIONARY : DICTIONARY;
    }

    public String getDynamic() {
        String tenantId = DataSourceContextHolder.getDatasourceId();
        return !StringUtil.isEmpty(tenantId) ? tenantId + DYNAMIC : DYNAMIC;
    }

    public String getPositionList() {
        String tenantId = DataSourceContextHolder.getDatasourceId();
        return !StringUtil.isEmpty(tenantId) ? tenantId + POSITIONLIST : POSITIONLIST;
    }

    public String getAllUser() {
        String tenantId = DataSourceContextHolder.getDatasourceId();
        return !StringUtil.isEmpty(tenantId) ? tenantId + ALLUSER : ALLUSER;
    }

    public String getSystemInfo() {
        String tenantId = DataSourceContextHolder.getDatasourceId();
        return !StringUtil.isEmpty(tenantId) ? tenantId + SYSTEMINFO : SYSTEMINFO;
    }

    public String getWechatConfig() {
        String tenantId = DataSourceContextHolder.getDatasourceId();
        return !StringUtil.isEmpty(tenantId) ? tenantId + WECHATCONFIG : WECHATCONFIG;
    }

    public String getValidCode() {
        String tenantId = DataSourceContextHolder.getDatasourceId();
        return !StringUtil.isEmpty(tenantId) ? tenantId + VALIDCODE : VALIDCODE;
    }

    public String getSmsValidCode() {
        String tenantId = DataSourceContextHolder.getDatasourceId();
        return !StringUtil.isEmpty(tenantId) ? tenantId + SMSVALIDCODE : SMSVALIDCODE;
    }

    public String getLoginToken(String tenantId) {
        return !StringUtil.isEmpty(tenantId) ? tenantId + LOGINTOKEN : LOGINTOKEN;
    }

    public String getLoginOnline() {
        String tenantId = DataSourceContextHolder.getDatasourceId();
        return !StringUtil.isEmpty(tenantId) ? tenantId + LOGINONLINE : LOGINONLINE;
    }

    public String getMobileLoginOnline() {
        String tenantId = DataSourceContextHolder.getDatasourceId();
        return !StringUtil.isEmpty(tenantId) ? tenantId + MOBILELOGINONLINE : MOBILELOGINONLINE;
    }

    public String getMobileDeviceList() {
        String tenantId = DataSourceContextHolder.getDatasourceId();
        return !StringUtil.isEmpty(tenantId) ? tenantId + MOBILEDEVICELIST : MOBILEDEVICELIST;
    }

    public String getUserAuthorize() {
        String tenantId = DataSourceContextHolder.getDatasourceId();
        return !StringUtil.isEmpty(tenantId) ? tenantId + USERAUTHORIZE : USERAUTHORIZE;
    }
}