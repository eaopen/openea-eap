
package org.openea.eap.extj.message.service;


import org.openea.eap.extj.base.service.SuperService;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.extj.message.entity.MessageMonitorEntity;
import org.openea.eap.extj.message.model.messagemonitor.*;

/**
 * 消息监控
 */
public interface MessageMonitorService extends SuperService<MessageMonitorEntity> {


    List<MessageMonitorEntity> getList(MessageMonitorPagination messageMonitorPagination);

    List<MessageMonitorEntity> getTypeList(MessageMonitorPagination messageMonitorPagination, String dataType);


    MessageMonitorEntity getInfo(String id);

    void delete(MessageMonitorEntity entity);

    void create(MessageMonitorEntity entity);

    boolean update(String id, MessageMonitorEntity entity);

//  子表方法

    //列表子表数据方法

    //验证表单
    boolean checkForm(MessageMonitorForm form, int i);


    String userSelectValues(String ids);

    /**
     * 删除
     * @param ids
     * @return
     */
    boolean delete(String[] ids);

    void emptyMonitor();
}
