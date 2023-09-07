package org.openea.eap.extj.base.service.impl;

import org.openea.eap.extj.base.service.SysconfigService;
import org.openea.eap.extj.model.BaseSystemInfo;
import org.springframework.stereotype.Service;

@Service
public class NullSysconfigService implements SysconfigService {
    @Override
    public BaseSystemInfo getSysInfo() {
        return new BaseSystemInfo();
    }
}
