package org.openea.eap.extj.message.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.openea.eap.extj.base.mapper.SuperMapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Param;
import org.openea.eap.extj.message.entity.ImContentEntity;
import org.openea.eap.extj.message.model.ImUnreadNumModel;

import java.util.List;
import java.util.Map;

/**
 * 聊天内容
 *
 */
@Mapper
public interface ImContentMapper extends SuperMapper<ImContentEntity> {

    List<ImUnreadNumModel> getUnreadList(@Param("receiveUserId") String receiveUserId);

    List<ImUnreadNumModel> getUnreadLists(@Param("receiveUserId") String receiveUserId);

    int readMessage(@Param("map") Map<String, String> map);
}

