package org.openea.eap.module.visualdev.extend.service.impl;

import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.module.visualdev.extend.entity.EmailSendEntity;
import org.openea.eap.module.visualdev.extend.mapper.EmailSendMapper;
import org.openea.eap.module.visualdev.extend.service.EmailSendService;
import org.springframework.stereotype.Service;

@Service
public class EmailSendServiceImpl extends SuperServiceImpl<EmailSendMapper, EmailSendEntity> implements EmailSendService {

}
