
package org.openea.eap.extj.message.service;


import org.openea.eap.extj.base.service.SuperService;
import com.baomidou.mybatisplus.extension.service.IService;


import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.extj.message.entity.SendConfigTemplateEntity;
import org.openea.eap.extj.message.entity.TemplateParamEntity;
import org.openea.eap.extj.message.model.sendmessageconfig.SendMessageConfigPagination;

/**
 * 消息发送配置
 */
public interface SendConfigTemplateService extends SuperService<SendConfigTemplateEntity> {

    QueryWrapper<SendConfigTemplateEntity> getChild(SendMessageConfigPagination pagination, QueryWrapper<SendConfigTemplateEntity> sendConfigTemplateQueryWrapper);

    SendConfigTemplateEntity getInfo(String id);

    List<SendConfigTemplateEntity> getDetailListByParentId(String id);

    /**
     * 根据消息发送配置id获取启用的配置模板
     * @param id
     * @return
     */
    List<SendConfigTemplateEntity> getConfigTemplateListByConfigId(String id);

    boolean isUsedAccount(String accountId);

    boolean isUsedTemplate(String templateId);
}
