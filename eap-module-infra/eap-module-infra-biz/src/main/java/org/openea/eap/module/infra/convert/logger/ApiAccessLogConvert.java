package org.openea.eap.module.infra.convert.logger;

import org.openea.eap.framework.common.pojo.PageResult;
import org.openea.eap.module.infra.api.logger.dto.ApiAccessLogCreateReqDTO;
import org.openea.eap.module.infra.controller.admin.logger.vo.apiaccesslog.ApiAccessLogExcelVO;
import org.openea.eap.module.infra.controller.admin.logger.vo.apiaccesslog.ApiAccessLogRespVO;
import org.openea.eap.module.infra.dal.dataobject.logger.ApiAccessLogDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * API 访问日志 Convert
 *
 */
@Mapper
public interface ApiAccessLogConvert {

    ApiAccessLogConvert INSTANCE = Mappers.getMapper(ApiAccessLogConvert.class);

    ApiAccessLogRespVO convert(ApiAccessLogDO bean);

    List<ApiAccessLogRespVO> convertList(List<ApiAccessLogDO> list);

    PageResult<ApiAccessLogRespVO> convertPage(PageResult<ApiAccessLogDO> page);

    List<ApiAccessLogExcelVO> convertList02(List<ApiAccessLogDO> list);

    ApiAccessLogDO convert(ApiAccessLogCreateReqDTO bean);

}
