
package org.openea.eap.extj.message.service;

import org.openea.eap.extj.base.service.SuperService;
import com.baomidou.mybatisplus.extension.service.IService;

import org.openea.eap.extj.message.entity.UserDeviceEntity;

import java.util.List;

/**
 *
 * 消息模板（新）
 * */
public interface UserDeviceService extends SuperService<UserDeviceEntity> {

    UserDeviceEntity getInfoByUserId(String userId);

    List<String> getCidList(String userId);

    UserDeviceEntity getInfoByClientId(String clientId);

    void create(UserDeviceEntity entity);

    boolean update(String id, UserDeviceEntity entity);

    void delete(UserDeviceEntity entity);

}
