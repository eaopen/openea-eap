package org.openea.eap.extj.engine.service;

import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.base.vo.ListVO;
import org.openea.eap.extj.engine.entity.FlowDelegateEntity;
import org.openea.eap.extj.engine.model.flowcandidate.FlowCandidateUserModel;
import org.openea.eap.extj.engine.model.flowdelegate.FlowDelegateCrForm;
import org.openea.eap.extj.engine.model.flowdelegate.FlowDelegatePagination;
import org.openea.eap.extj.engine.model.flowengine.FlowPagination;
import org.openea.eap.extj.engine.model.flowtemplate.FlowPageListVO;
import org.openea.eap.extj.exception.WorkFlowException;

import java.util.List;

/**
 * 流程委托
 *
 * 
 */
public interface FlowDelegateService extends SuperService<FlowDelegateEntity> {

    /**
     * 列表
     *
     * @param pagination 请求参数
     * @return
     */
    List<FlowDelegateEntity> getList(FlowDelegatePagination pagination);

    /**
     * 列表
     *
     * @return
     */
    List<FlowDelegateEntity> getList();


    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    FlowDelegateEntity getInfo(String id);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(FlowDelegateEntity entity);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(FlowDelegateEntity entity);

    /**
     * 获取被委托人的表单
     *
     * @param touserId 被委托人
     * @return
     */
    List<FlowDelegateEntity> getUser(String touserId);

    /**
     * 获取委托的表单
     *
     * @param userId   委托人
     * @param flowId   流程引擎
     * @param touserId 被委托人
     * @return
     */
    List<FlowDelegateEntity> getUser(String userId, String flowId, String touserId);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return
     */
    boolean update(String id, FlowDelegateEntity entity);

    /**
     * 委托结束
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return
     */
    boolean updateStop(String id, FlowDelegateEntity entity);

    /**
     * 获取我的委托发起
     *
     * @return
     */
    List<FlowPageListVO> getflow(FlowPagination pagination);

    /**
     * 根据流程获取委托人列表。
     *
     * @param flowId 流程版本id
     * @return
     */
    ListVO<FlowCandidateUserModel> getUserListByFlowId(String flowId) throws WorkFlowException;

    /**
     * 根据条件查询相关委托信息
     *
     * @param
     * @return
     */
    List<FlowDelegateEntity> selectSameParamAboutDelaget(FlowDelegateCrForm model);

}
