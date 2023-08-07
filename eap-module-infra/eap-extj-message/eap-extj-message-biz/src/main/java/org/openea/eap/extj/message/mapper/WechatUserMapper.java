package org.openea.eap.extj.message.mapper;



import org.apache.ibatis.annotations.Mapper;
import org.openea.eap.extj.base.mapper.SuperMapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.openea.eap.extj.message.entity.WechatUserEntity;

/**
 *
 * 消息模板（新）
 * */
@Mapper
public interface WechatUserMapper extends SuperMapper<WechatUserEntity> {

}
