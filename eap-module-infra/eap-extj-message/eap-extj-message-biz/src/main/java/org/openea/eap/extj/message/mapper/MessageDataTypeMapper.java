package org.openea.eap.extj.message.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.openea.eap.extj.base.mapper.SuperMapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.openea.eap.extj.message.entity.MessageDataTypeEntity;

/**
 *
 * 消息中心类型数据
 * */
@Mapper
public interface MessageDataTypeMapper extends SuperMapper<MessageDataTypeEntity> {

}
