package org.openea.eap.extj.message.mapper;



import org.apache.ibatis.annotations.Mapper;
import org.openea.eap.extj.base.mapper.SuperMapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.openea.eap.extj.message.entity.UserDeviceEntity;

/**
 *
 * 消息模板（新）
 * */
@Mapper
public interface UserDeviceMapper extends SuperMapper<UserDeviceEntity> {

}
