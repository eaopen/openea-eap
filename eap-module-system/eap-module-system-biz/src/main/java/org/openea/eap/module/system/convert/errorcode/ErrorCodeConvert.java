package org.openea.eap.module.system.convert.errorcode;

import org.openea.eap.framework.common.pojo.PageResult;
import org.openea.eap.module.system.api.errorcode.dto.ErrorCodeAutoGenerateReqDTO;
import org.openea.eap.module.system.api.errorcode.dto.ErrorCodeRespDTO;
import org.openea.eap.module.system.controller.admin.errorcode.vo.ErrorCodeCreateReqVO;
import org.openea.eap.module.system.controller.admin.errorcode.vo.ErrorCodeExcelVO;
import org.openea.eap.module.system.controller.admin.errorcode.vo.ErrorCodeRespVO;
import org.openea.eap.module.system.controller.admin.errorcode.vo.ErrorCodeUpdateReqVO;
import org.openea.eap.module.system.dal.dataobject.errorcode.ErrorCodeDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 错误码 Convert
 *
 */
@Mapper
public interface ErrorCodeConvert {

    ErrorCodeConvert INSTANCE = Mappers.getMapper(ErrorCodeConvert.class);

    ErrorCodeDO convert(ErrorCodeCreateReqVO bean);

    ErrorCodeDO convert(ErrorCodeUpdateReqVO bean);

    ErrorCodeRespVO convert(ErrorCodeDO bean);

    List<ErrorCodeRespVO> convertList(List<ErrorCodeDO> list);

    PageResult<ErrorCodeRespVO> convertPage(PageResult<ErrorCodeDO> page);

    List<ErrorCodeExcelVO> convertList02(List<ErrorCodeDO> list);

    ErrorCodeDO convert(ErrorCodeAutoGenerateReqDTO bean);

    List<ErrorCodeRespDTO> convertList03(List<ErrorCodeDO> list);

}
