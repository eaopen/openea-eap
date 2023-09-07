package org.openea.eap.extj.base.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.openea.eap.extj.base.mapper.SuperMapper;
import org.openea.eap.extj.base.entity.FilterEntity;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface FilterMapper extends SuperMapper<FilterEntity> {
}