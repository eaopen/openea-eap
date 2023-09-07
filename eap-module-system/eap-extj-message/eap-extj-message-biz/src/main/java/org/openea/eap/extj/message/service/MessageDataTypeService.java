
package org.openea.eap.extj.message.service;


import org.openea.eap.extj.base.service.SuperService;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.extj.message.entity.MessageDataTypeEntity;
import org.openea.eap.extj.message.model.messagedatatype.*;

/**
 *
 * 消息中心类型数据
 * */
public interface MessageDataTypeService extends SuperService<MessageDataTypeEntity> {


    List<MessageDataTypeEntity> getList(MessageDataTypePagination messageDataTypePagination);

    List<MessageDataTypeEntity> getTypeList(MessageDataTypePagination messageDataTypePagination,String dataType);

    /**
     *
     * @param type 1：消息类型，2：渠道，3：webhook类型，4：消息来源
     * @return
     */
    List<MessageDataTypeEntity> getListByType(List<String> type);


    MessageDataTypeEntity getInfo(String id);

    MessageDataTypeEntity getInfo(String type,String code);

    void delete(MessageDataTypeEntity entity);

    void create(MessageDataTypeEntity entity);

    boolean update(String id, MessageDataTypeEntity entity);
    
//  子表方法

    //列表子表数据方法

    //验证表单
    boolean checkForm(MessageDataTypeForm form, int i);
}
