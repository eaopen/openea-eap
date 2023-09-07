package org.openea.eap.extj.base.service.impl;

import org.openea.eap.extj.base.entity.SystemEntity;
import org.openea.eap.extj.base.service.SystemService;
import org.springframework.stereotype.Service;

@Service
public class NullSystemService implements SystemService {
    @Override
    public SystemEntity getInfo(String id) {
        return new SystemEntity();
    }
}
