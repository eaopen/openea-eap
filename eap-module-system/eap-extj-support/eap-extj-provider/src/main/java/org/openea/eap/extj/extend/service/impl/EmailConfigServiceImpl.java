package org.openea.eap.extj.extend.service.impl;

import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.extend.entity.EmailConfigEntity;
import org.openea.eap.extj.extend.mapper.EmailConfigMapper;
import org.openea.eap.extj.extend.service.EmailConfigService;
import org.springframework.stereotype.Service;

@Service
public class EmailConfigServiceImpl extends SuperServiceImpl<EmailConfigMapper, EmailConfigEntity>
        implements EmailConfigService {

}