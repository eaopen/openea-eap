package org.openea.eap.extj.message.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.openea.eap.extj.base.mapper.SuperMapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.openea.eap.extj.message.entity.SendConfigRecordEntity;

/**
 *
 * 发送配置使用记录
 * */
@Mapper
public interface SendConfigRecordMapper extends SuperMapper<SendConfigRecordEntity> {

}
