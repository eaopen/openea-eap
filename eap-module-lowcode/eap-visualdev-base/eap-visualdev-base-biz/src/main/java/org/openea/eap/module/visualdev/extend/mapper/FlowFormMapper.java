package org.openea.eap.module.visualdev.extend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openea.eap.extj.base.mapper.SuperMapper;
import org.openea.eap.module.visualdev.extend.entity.FlowFormEntity;
import org.openea.eap.module.visualdev.extend.model.flow.FlowTempInfoModel;

@Mapper
public interface FlowFormMapper extends SuperMapper<FlowFormEntity> {

    FlowTempInfoModel findFLowInfo(@Param("tempId") String tempId);

}
