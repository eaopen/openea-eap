package org.openea.eap.module.system.convert.sms;

import org.openea.eap.module.system.controller.admin.sms.vo.channel.SmsChannelCreateReqVO;
import org.openea.eap.module.system.controller.admin.sms.vo.channel.SmsChannelRespVO;
import org.openea.eap.module.system.controller.admin.sms.vo.channel.SmsChannelSimpleRespVO;
import org.openea.eap.module.system.controller.admin.sms.vo.channel.SmsChannelUpdateReqVO;
import org.openea.eap.module.system.dal.dataobject.sms.SmsChannelDO;
import org.openea.eap.framework.common.pojo.PageResult;
import org.openea.eap.framework.sms.core.property.SmsChannelProperties;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 短信渠道 Convert
 *
 */
@Mapper
public interface SmsChannelConvert {

    SmsChannelConvert INSTANCE = Mappers.getMapper(SmsChannelConvert.class);

    SmsChannelDO convert(SmsChannelCreateReqVO bean);

    SmsChannelDO convert(SmsChannelUpdateReqVO bean);

    SmsChannelRespVO convert(SmsChannelDO bean);

    List<SmsChannelRespVO> convertList(List<SmsChannelDO> list);

    PageResult<SmsChannelRespVO> convertPage(PageResult<SmsChannelDO> page);

    List<SmsChannelSimpleRespVO> convertList03(List<SmsChannelDO> list);

    SmsChannelProperties convert02(SmsChannelDO channel);

}
