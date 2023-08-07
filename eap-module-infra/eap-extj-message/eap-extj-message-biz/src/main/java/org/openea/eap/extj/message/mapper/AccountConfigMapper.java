package org.openea.eap.extj.message.mapper;



import org.apache.ibatis.annotations.Mapper;
import org.openea.eap.extj.base.mapper.SuperMapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.openea.eap.extj.message.entity.AccountConfigEntity;

/**
 *
 * 账号配置功能
 * */

@Mapper
public interface AccountConfigMapper extends SuperMapper<AccountConfigEntity> {

}
