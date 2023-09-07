package org.openea.eap.extj.message.mapper;



import org.apache.ibatis.annotations.Mapper;
import org.openea.eap.extj.base.mapper.SuperMapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.openea.eap.extj.message.entity.MessageMonitorEntity;

/**
 *
 * 消息监控
 * */
@Mapper
public interface MessageMonitorMapper extends SuperMapper<MessageMonitorEntity> {

}
