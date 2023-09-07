package org.openea.eap.extj.base.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.openea.eap.extj.base.entity.PrintLogEntity;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface PrintLogMapper extends SuperMapper<PrintLogEntity> {
}