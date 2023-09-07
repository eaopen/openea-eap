package org.openea.eap.extj.message.mapper;



import org.apache.ibatis.annotations.Mapper;
import org.openea.eap.extj.base.mapper.SuperMapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.openea.eap.extj.message.entity.SendConfigTemplateEntity;

/**
 *
 * 消息发送配置
 * */
@Mapper
public interface SendConfigTemplateMapper extends SuperMapper<SendConfigTemplateEntity> {

}
