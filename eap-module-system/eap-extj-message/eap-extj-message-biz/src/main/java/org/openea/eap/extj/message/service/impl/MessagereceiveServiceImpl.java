package org.openea.eap.extj.message.service.impl;


import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.message.entity.MessageReceiveEntity;
import org.openea.eap.extj.message.mapper.MessagereceiveMapper;
import org.openea.eap.extj.message.service.MessagereceiveService;
import org.springframework.stereotype.Service;

/**
 * 消息接收 服务实现类
 *
 *
 */
@Service
public class MessagereceiveServiceImpl extends SuperServiceImpl<MessagereceiveMapper, MessageReceiveEntity> implements MessagereceiveService {

}
