package org.openea.eap.extj.message.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.openea.eap.extj.base.mapper.SuperMapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.openea.eap.extj.message.entity.ImReplyEntity;
import org.openea.eap.extj.message.model.ImReplyListModel;


import java.util.List;

/**
 * 聊天会话
 *
 *
 */
@Mapper
public interface ImReplyMapper extends SuperMapper<ImReplyEntity> {

    /**
     * 聊天会话列表
     * @return
     */
    List<ImReplyListModel> getImReplyList();

}
