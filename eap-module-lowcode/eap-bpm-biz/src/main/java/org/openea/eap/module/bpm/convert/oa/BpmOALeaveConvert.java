package org.openea.eap.module.bpm.convert.oa;

import org.openea.eap.module.bpm.controller.admin.oa.vo.BpmOALeaveCreateReqVO;
import org.openea.eap.module.bpm.controller.admin.oa.vo.BpmOALeaveRespVO;
import org.openea.eap.module.bpm.dal.dataobject.oa.BpmOALeaveDO;
import org.openea.eap.framework.common.pojo.PageResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 请假申请 Convert
 *
 * @author 芋艿
 */
@Mapper
public interface BpmOALeaveConvert {

    BpmOALeaveConvert INSTANCE = Mappers.getMapper(BpmOALeaveConvert.class);

    BpmOALeaveDO convert(BpmOALeaveCreateReqVO bean);

    BpmOALeaveRespVO convert(BpmOALeaveDO bean);

    List<BpmOALeaveRespVO> convertList(List<BpmOALeaveDO> list);

    PageResult<BpmOALeaveRespVO> convertPage(PageResult<BpmOALeaveDO> page);

}
