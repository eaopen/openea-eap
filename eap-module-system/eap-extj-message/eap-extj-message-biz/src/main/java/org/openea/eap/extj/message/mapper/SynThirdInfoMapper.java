package org.openea.eap.extj.message.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.openea.eap.extj.base.mapper.SuperMapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.openea.eap.extj.message.entity.SynThirdInfoEntity;

/**
 * 第三方工具对象同步表
 *
 *
 */
@Mapper
public interface SynThirdInfoMapper extends SuperMapper<SynThirdInfoEntity> {

}
