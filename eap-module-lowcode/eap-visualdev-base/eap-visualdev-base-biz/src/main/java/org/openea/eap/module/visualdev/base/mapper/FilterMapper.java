package org.openea.eap.module.visualdev.base.mapper;

import org.openea.eap.extj.base.mapper.SuperMapper;
import org.openea.eap.module.visualdev.base.entity.FilterEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface FilterMapper extends SuperMapper<FilterEntity> {
}