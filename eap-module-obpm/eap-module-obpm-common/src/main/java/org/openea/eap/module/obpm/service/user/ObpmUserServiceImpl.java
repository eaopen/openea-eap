package org.openea.eap.module.obpm.service.user;


import lombok.extern.slf4j.Slf4j;
import org.openea.eap.module.system.dal.dataobject.user.AdminUserDO;
import org.openea.eap.module.system.service.user.AdminUserService;
import org.openea.eap.module.system.service.user.AdminUserServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service("obpmUserService")
@ConditionalOnProperty(prefix = "eap", name = "enableOpenBpm", havingValue = "true")
@Slf4j
public class ObpmUserServiceImpl extends AdminUserServiceImpl implements AdminUserService {

    @Override
    public AdminUserDO getUser(Long id) {
        AdminUserDO user = super.getUser(id);
        // more info from obpm
        return user;
    }
}
