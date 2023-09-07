
package org.openea.eap.extj.message.service;

import org.openea.eap.extj.base.service.SuperService;
import com.baomidou.mybatisplus.extension.service.IService;

import org.openea.eap.extj.message.entity.TemplateParamEntity;
import org.openea.eap.extj.message.model.messagetemplateconfig.*;
import java.util.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
/**
 *
 * 消息模板（新）
 */
public interface TemplateParamService extends SuperService<TemplateParamEntity> {

	QueryWrapper<TemplateParamEntity> getChild(MessageTemplateConfigPagination pagination, QueryWrapper<TemplateParamEntity> templateParamQueryWrapper);

	TemplateParamEntity getInfo(String id);

	List<TemplateParamEntity> getDetailListByParentId(String id);

	List<TemplateParamEntity> getParamList(String id,List<String> params);
}
