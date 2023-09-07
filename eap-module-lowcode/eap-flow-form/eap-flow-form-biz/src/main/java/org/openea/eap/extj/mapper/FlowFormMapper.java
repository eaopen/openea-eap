package org.openea.eap.extj.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.openea.eap.extj.base.mapper.SuperMapper;
import org.openea.eap.extj.entity.FlowFormEntity;
import org.openea.eap.extj.model.flow.FlowTempInfoModel;
import org.apache.ibatis.annotations.Param;

/**
 * 流程设计
 *
 *
 */
@Mapper
public interface FlowFormMapper extends SuperMapper<FlowFormEntity> {

    FlowTempInfoModel findFLowInfo(@Param("tempId") String tempId);

}
