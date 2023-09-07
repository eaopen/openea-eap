
package org.openea.eap.extj.message.service;

import org.openea.eap.extj.base.service.SuperService;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.extj.message.entity.SmsFieldEntity;
import org.openea.eap.extj.message.model.messagetemplateconfig.*;

/**
 *
 * 消息模板（新）
 */
public interface SmsFieldService extends SuperService<SmsFieldEntity> {

	QueryWrapper<SmsFieldEntity> getChild(MessageTemplateConfigPagination pagination, QueryWrapper<SmsFieldEntity> smsFieldQueryWrapper);

	SmsFieldEntity getInfo(String id);

	List<SmsFieldEntity> getDetailListByParentId(String id);

	List<SmsFieldEntity> getParamList(String id,List<String> params);

	Map<String,Object> getParamMap(String templateId,Map<String,Object> map);
}
