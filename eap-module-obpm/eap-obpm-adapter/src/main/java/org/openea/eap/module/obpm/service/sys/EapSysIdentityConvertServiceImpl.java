package org.openea.eap.module.obpm.service.sys;

import org.openbpm.org.api.model.IUser;
import org.openbpm.sys.api.model.SysIdentity;
import org.openbpm.sys.api.service.SysIdentityConvert;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnProperty(prefix = "eap.obpm", name = "eap-adapter", havingValue = "true")
public class EapSysIdentityConvertServiceImpl implements SysIdentityConvert {
    @Override
    public IUser convert2User(SysIdentity identity) {
        return null;
    }

    @Override
    public List<? extends IUser> convert2Users(SysIdentity identity) {
        return null;
    }

    @Override
    public List<? extends IUser> convert2Users(List<SysIdentity> identity) {
        return null;
    }
}
