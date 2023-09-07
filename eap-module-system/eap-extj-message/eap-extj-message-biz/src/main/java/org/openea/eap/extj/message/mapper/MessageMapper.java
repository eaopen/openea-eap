package org.openea.eap.extj.message.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.openea.eap.extj.base.mapper.SuperMapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.openea.eap.extj.message.entity.MessageEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 消息实例
 *
 *
 */
@Mapper
public interface MessageMapper extends SuperMapper<MessageEntity> {

    List<MessageEntity> getMessageList(@Param("map") Map<String, Object> map);

    int getUnreadNoticeCount(@Param("userId") String userId);

    int getUnreadCount(@Param("userId") String userId,@Param("type") Integer type);

    int getUnreadSystemMessageCount(@Param("userId") String userId);

    int getUnreadMessageCount(@Param("userId") String userId);

    List<MessageEntity> getInfoDefault(@Param("type") int type);
}
