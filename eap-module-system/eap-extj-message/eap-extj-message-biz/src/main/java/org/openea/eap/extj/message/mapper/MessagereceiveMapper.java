package org.openea.eap.extj.message.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.openea.eap.extj.base.mapper.SuperMapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.openea.eap.extj.message.entity.MessageReceiveEntity;

/**
 * 消息接收
 *
 *
 */
@Mapper
public interface MessagereceiveMapper extends SuperMapper<MessageReceiveEntity> {

}
