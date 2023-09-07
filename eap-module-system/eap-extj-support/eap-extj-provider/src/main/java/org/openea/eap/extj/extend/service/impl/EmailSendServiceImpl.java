package org.openea.eap.extj.extend.service.impl;

import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.extend.entity.EmailSendEntity;
import org.openea.eap.extj.extend.mapper.EmailSendMapper;
import org.openea.eap.extj.extend.service.EmailSendService;
import org.springframework.stereotype.Service;

@Service
public class EmailSendServiceImpl extends SuperServiceImpl<EmailSendMapper, EmailSendEntity> implements EmailSendService {

}
