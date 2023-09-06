package org.openea.eap.extj.config;

import cn.dev33.satoken.config.SaTokenConfig;
import org.openea.eap.extj.model.BaseSystemInfo;
import org.openea.eap.extj.consts.AuthConsts;
import org.openea.eap.extj.util.TenantProvider;

import java.util.Optional;

/**
 * @deprecated
 */
public class JnpfTokenConfig extends SaTokenConfig {


    @Override
    public long getTimeout() {
        BaseSystemInfo baseSystemInfo = getSycConfig();
        if(baseSystemInfo == null){
            return super.getTimeout();
        }else {
            return Long.parseLong(getSycConfig().getTokenTimeout()) * 60L;
        }
    }

    @Override
    public Boolean getIsConcurrent() {
        BaseSystemInfo baseSystemInfo = getSycConfig();
        if(baseSystemInfo == null){
            return super.getIsConcurrent();
        }else {
            return Optional.ofNullable(getSycConfig().getSingleLogin()).orElse(1)==2;
        }
    }

    @Override
    public String getJwtSecretKey() {
        String secrekey = super.getJwtSecretKey();
        if(secrekey == null){
            return AuthConsts.JWT_SECRET;
        }
        return secrekey;
    }

    @Override
    public String getCurrDomain() {
        return super.getCurrDomain();
    }

    @Override
    public String getTokenPrefix() {
        return AuthConsts.TOKEN_PREFIX;
    }

    @Override
    public Boolean getTokenSessionCheckLogin() {
        return false;
    }

    @Override
    public Boolean getIsPrint() {
        return false;
    }

    @Override
    public Boolean getIsShare() {
        return false;
    }

    @Override
    public String getTokenName() {
        return "Authorization";
    }

    @Override
    public Boolean getIsReadCookie() {
        return false;
    }

    @Override
    public Boolean getIsReadBody() {
        return false;
    }

    @Override
    public Boolean getIsReadHeader() {
        return true;
    }

    @Override
    public int getMaxLoginCount() {
        return -1;
    }

    private BaseSystemInfo getSycConfig(){
        return TenantProvider.getBaseSystemInfo();
    }


}
