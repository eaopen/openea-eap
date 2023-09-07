package org.openea.eap.extj.engine.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.openea.eap.extj.base.mapper.SuperMapper;
import org.openea.eap.extj.engine.entity.FlowTaskEntity;
import org.openea.eap.extj.engine.model.flowtask.FlowTaskListModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 流程任务
 *
 *
 */
@Mapper
public interface FlowTaskMapper extends SuperMapper<FlowTaskEntity> {
    /**
     * 已办事宜
     *
     * @return
     */
    List<FlowTaskListModel> getTrialList(@Param("map") Map<String, Object> map);

    /**
     * 抄送事宜
     *
     * @return
     */
    List<FlowTaskListModel> getCirculateList(@Param("map") Map<String, Object> map);

    /**
     * 待办事宜
     *
     * @return
     */
    List<FlowTaskListModel> getWaitList(@Param("map") Map<String, Object> map);

    /**
     * 获取在线数量
     */
    String getVisualFormId(@Param("id") String id);
}
