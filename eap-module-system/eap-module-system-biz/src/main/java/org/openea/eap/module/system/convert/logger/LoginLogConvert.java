package org.openea.eap.module.system.convert.logger;

import org.openea.eap.framework.common.pojo.PageResult;
import org.openea.eap.module.system.api.logger.dto.LoginLogCreateReqDTO;
import org.openea.eap.module.system.controller.admin.logger.vo.loginlog.LoginLogExcelVO;
import org.openea.eap.module.system.controller.admin.logger.vo.loginlog.LoginLogRespVO;
import org.openea.eap.module.system.dal.dataobject.logger.LoginLogDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface LoginLogConvert {

    LoginLogConvert INSTANCE = Mappers.getMapper(LoginLogConvert.class);

    PageResult<LoginLogRespVO> convertPage(PageResult<LoginLogDO> page);

    List<LoginLogExcelVO> convertList(List<LoginLogDO> list);

    LoginLogDO convert(LoginLogCreateReqDTO bean);

}
