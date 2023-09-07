
package org.openea.eap.extj.message.service;

import org.openea.eap.extj.base.service.SuperService;
import com.baomidou.mybatisplus.extension.service.IService;


import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.extj.message.entity.SendConfigRecordEntity;
import org.openea.eap.extj.message.model.sendconfigrecord.*;

/**
 * 发送配置使用记录
 * */
public interface SendConfigRecordService extends SuperService<SendConfigRecordEntity> {


    List<SendConfigRecordEntity> getList(SendConfigRecordPagination sendConfigRecordPagination);

    List<SendConfigRecordEntity> getTypeList(SendConfigRecordPagination sendConfigRecordPagination, String dataType);


    SendConfigRecordEntity getInfo(String id);

    SendConfigRecordEntity getRecord(String sendConfigId, String usedId);

    void delete(SendConfigRecordEntity entity);

    void create(SendConfigRecordEntity entity);

    boolean update(String id, SendConfigRecordEntity entity);

//  子表方法

    //列表子表数据方法

    //验证表单
    boolean checkForm(SendConfigRecordForm form, int i);
}
