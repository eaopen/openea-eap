package org.openea.eap.extj.engine.service;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.engine.entity.FlowTaskEntity;
import org.openea.eap.extj.engine.model.flowbefore.FlowBatchModel;
import org.openea.eap.extj.engine.model.flowtask.FlowTaskListModel;
import org.openea.eap.extj.engine.model.flowtask.PaginationFlowTask;
import org.openea.eap.extj.exception.WorkFlowException;

import java.util.List;

/**
 * 流程任务
 *
 * 
 */
public interface FlowTaskService extends SuperService<FlowTaskEntity> {

    /**
     * 列表（流程监控）
     *
     * @param paginationFlowTask
     * @return
     */
    List<FlowTaskEntity> getMonitorList(PaginationFlowTask paginationFlowTask);

    /**
     * 列表（我发起的）
     *
     * @param paginationFlowTask
     * @return
     */
    List<FlowTaskEntity> getLaunchList(PaginationFlowTask paginationFlowTask);

    /**
     * 列表（待我审批）
     *
     * @param paginationFlowTask
     * @return
     */
    List<FlowTaskListModel> getWaitList(PaginationFlowTask paginationFlowTask);

    /**
     * 列表（待我审批）
     *
     * @param paginationFlowTask
     * @param paginationFlowTask
     * @return
     */
    List<FlowTaskListModel> getWaitListAll(PaginationFlowTask paginationFlowTask);

    /**
     * 列表（我已审批）
     *
     * @return
     */
    List<FlowTaskListModel> getTrialList();


    /**
     * 列表（待我审批）
     *
     * @return
     */
    List<FlowTaskEntity> getWaitList();

    /**
     * 列表（抄送我的）
     *
     * @return
     */
    List<FlowTaskListModel> getCirculateList();

    /**
     * 列表（待我审批）
     * 门户专用
     *
     * @return
     */
    List<FlowTaskListModel> getDashboardAllWaitList();

    /**
     * 列表（我已审批）
     *
     * @param paginationFlowTask
     * @return
     */
    List<FlowTaskListModel> getTrialList(PaginationFlowTask paginationFlowTask);

    /**
     * 列表（抄送我的）
     *
     * @param paginationFlowTask
     * @return
     */
    List<FlowTaskListModel> getCirculateList(PaginationFlowTask paginationFlowTask);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     * @throws WorkFlowException 异常
     */
    FlowTaskEntity getInfo(String id) throws WorkFlowException;

    /**
     * 更新
     *
     * @param entity 主键值
     * @return
     */
    void update(FlowTaskEntity entity);

    /**
     * 创建
     *
     * @param entity 主键值
     * @return
     */
    void create(FlowTaskEntity entity);

    /**
     * 创建或者修改
     *
     * @param entity 主键值
     * @return
     */
    void createOrUpdate(FlowTaskEntity entity);

    /**
     * 信息
     *
     * @param id      主键值
     * @param columns 指定获取的列数据 , 任务中存了三个JSON数据 ， 排除后可以提高查询速度
     * @return
     */
    FlowTaskEntity getInfoSubmit(String id, SFunction<FlowTaskEntity, ?>... columns);

    /**
     * 信息
     *
     * @param ids     主键值
     * @param columns 指定获取的列数据 , 任务中存了三个JSON数据 ， 排除后可以提高查询速度
     * @return
     */
    List<FlowTaskEntity> getInfosSubmit(String[] ids, SFunction<FlowTaskEntity, ?>... columns);

    /**
     * 删除
     *
     * @param entity 实体对象
     * @throws WorkFlowException 异常
     */
    void delete(FlowTaskEntity entity) throws WorkFlowException;

    /**
     * 递归删除所有字节点
     *
     * @param id
     */
    void deleteChildAll(List<String> id);

    /**
     * 批量删除流程
     *
     * @param ids
     */
    void delete(String[] ids) throws WorkFlowException;

    /**
     * 查询订单状态
     *
     * @param id
     * @return
     */
    List<FlowTaskEntity> getOrderStaList(List<String> id);

    /**
     * 查询子流程
     *
     * @param id
     * @return
     */
    List<FlowTaskEntity> getChildList(String id, SFunction<FlowTaskEntity, ?>... columns);

    /**
     * 查询流程列表
     *
     * @param tempId
     * @return
     */
    List<FlowTaskEntity> getTemplateIdList(String tempId);

    /**
     * 查询流程列表
     *
     * @param flowId
     * @return
     */
    List<FlowTaskEntity> getFlowList(String flowId);

    /**
     * 批量审批引擎
     *
     * @return
     */
    List<FlowBatchModel> batchFlowSelector();

    /**
     * 获取子节点下所有数据
     *
     * @param idList
     * @param idAll
     */
    void deleTaskAll(List<String> idList, List<String> idAll);

    /**
     * 获取所有子流程
     *
     * @param id
     * @return
     */
    List<String> getChildAllList(String id);

    /**
     * 获取在线数量
     *
     * @param id
     * @return
     */
    String getVisualFormId(String id);

    /**
     * 获取子流程
     *
     * @param id
     * @return
     */
    void getChildList(String id, boolean suspend, List<String> list);
}
