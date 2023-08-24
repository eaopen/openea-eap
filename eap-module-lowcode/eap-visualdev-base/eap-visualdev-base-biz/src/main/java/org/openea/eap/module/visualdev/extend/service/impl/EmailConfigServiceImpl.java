package org.openea.eap.module.visualdev.extend.service.impl;

import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.module.visualdev.extend.entity.EmailConfigEntity;
import org.openea.eap.module.visualdev.extend.mapper.EmailConfigMapper;
import org.openea.eap.module.visualdev.extend.service.EmailConfigService;
import org.springframework.stereotype.Service;

@Service
public class EmailConfigServiceImpl extends SuperServiceImpl<EmailConfigMapper, EmailConfigEntity> implements EmailConfigService {

}