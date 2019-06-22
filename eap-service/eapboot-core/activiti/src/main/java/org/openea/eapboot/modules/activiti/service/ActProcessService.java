package org.openea.eapboot.modules.activiti.service;

import org.openea.eapboot.base.EapBaseService;
import org.openea.eapboot.common.vo.SearchVo;
import org.openea.eapboot.modules.activiti.entity.ActBusiness;
import org.openea.eapboot.modules.activiti.entity.ActProcess;
import org.openea.eapboot.modules.activiti.vo.ProcessNodeVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 流程管理接口
 */
public interface ActProcessService extends EapBaseService<ActProcess,String> {

    /**
     * 多条件分页获取
     * @param showLatest
     * @param actProcess
     * @param searchVo
     * @param pageable
     * @return
     */
    Page<ActProcess> findByCondition(Boolean showLatest, ActProcess actProcess, SearchVo searchVo, Pageable pageable);

    /**
     * 通过key和latest获取
     * @param processKey
     * @return
     */
    ActProcess findByProcessKeyAndLatest(String processKey, Boolean latest);

    /**
     * 通过key设置所有版本为旧
     * @param processKey
     */
    void setAllOldByProcessKey(String processKey);

    /**
     * 通过key设置最新版本
     * @param processKey
     */
    void setLatestByProcessKey(String processKey);

    /**
     * 通过分类获取
     * @param categoryId
     * @return
     */
    List<ActProcess> findByCategoryId(String categoryId);

    /**
     * 通过流程定义id启动流程 返回流程实例id
     * @param actBusiness
     * @return
     */
    String startProcess(ActBusiness actBusiness);

    /**
     * 通过流程定义id获取第一个任务节点
     * @param procDefId
     * @return
     */
    ProcessNodeVo getFirstNode(String procDefId);

    /**
     * 获取下一节点
     * @param procInstId
     * @return
     */
    ProcessNodeVo getNextNode(String procInstId);

    /**
     * 获取节点审批人
     * @param nodeId
     * @return
     */
    ProcessNodeVo getNode(String nodeId);
}