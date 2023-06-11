package org.openea.eap.module.system.convert.sms;

import org.openea.eap.module.system.controller.admin.sms.vo.log.SmsLogExcelVO;
import org.openea.eap.module.system.controller.admin.sms.vo.log.SmsLogRespVO;
import org.openea.eap.module.system.dal.dataobject.sms.SmsLogDO;
import org.openea.eap.framework.common.pojo.PageResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 短信日志 Convert
 *
 */
@Mapper
public interface SmsLogConvert {

    SmsLogConvert INSTANCE = Mappers.getMapper(SmsLogConvert.class);

    SmsLogRespVO convert(SmsLogDO bean);

    List<SmsLogRespVO> convertList(List<SmsLogDO> list);

    PageResult<SmsLogRespVO> convertPage(PageResult<SmsLogDO> page);

    List<SmsLogExcelVO> convertList02(List<SmsLogDO> list);

}
