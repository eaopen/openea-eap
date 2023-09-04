
package org.openea.eap.extj.message.service;


import org.openea.eap.extj.base.service.SuperService;

import java.util.*;

import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.message.entity.MessageTemplateConfigEntity;
import org.openea.eap.extj.message.entity.SmsFieldEntity;
import org.openea.eap.extj.message.entity.TemplateParamEntity;
import org.openea.eap.extj.message.model.messagetemplateconfig.*;

/**
 * 消息模板（新）
 */
public interface MessageTemplateConfigService extends SuperService<MessageTemplateConfigEntity> {


    List<MessageTemplateConfigEntity> getList(MessageTemplateConfigPagination MessageTemplateConfigPagination);

    List<MessageTemplateConfigEntity> getTypeList(MessageTemplateConfigPagination MessageTemplateConfigPagination, String dataType);


    MessageTemplateConfigEntity getInfo(String id);

    MessageTemplateConfigEntity getInfoByEnCode(String enCode,String messageType);

    void delete(MessageTemplateConfigEntity entity);

    void create(MessageTemplateConfigEntity entity);

    boolean update(String id, MessageTemplateConfigEntity entity);

    //  子表方法
    List<TemplateParamEntity> getTemplateParamList(String id, MessageTemplateConfigPagination MessageTemplateConfigPagination);

    List<TemplateParamEntity> getTemplateParamList(String id);

    List<SmsFieldEntity> getSmsFieldList(String id, MessageTemplateConfigPagination MessageTemplateConfigPagination);

    List<SmsFieldEntity> getSmsFieldList(String id);

    //列表子表数据方法

    //验证表单
    boolean checkForm(MessageTemplateConfigForm form, int i,String id);

    /**
     * 验证名称
     *
     * @param fullName 名称
     * @param id       主键值
     * @return ignore
     */
    boolean isExistByFullName(String fullName, String id);

    /**
     * 验证编码
     *
     * @param enCode 编码
     * @param id     主键值
     * @return ignore
     */
    boolean isExistByEnCode(String enCode, String id);

    /**
     * 消息模板导入
     *
     * @param entity 实体对象
     * @return ignore
     * @throws DataException ignore
     */
    ActionResult ImportData(MessageTemplateConfigEntity entity) throws DataException;

//    /**
//     * 获取模板被引用的参数（用json格式存储参数数据）
//     * @param id 模板id
//     * @return
//     */
//    List<BaseTemplateParamModel> getParamJson(String id);

    /**
     * 获取模板被引用的参数（消息模板参数数据用子表保存）
     * @param id 模板id
     * @return
     */
    List<TemplateParamModel> getParamJson(String id);
}
