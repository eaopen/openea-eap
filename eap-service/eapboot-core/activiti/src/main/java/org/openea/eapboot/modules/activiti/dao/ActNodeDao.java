package org.openea.eapboot.modules.activiti.dao;

import org.openea.eapboot.base.EapBaseDao;
import org.openea.eapboot.modules.activiti.entity.ActNode;

import java.util.List;

/**
 * 流程节点用户数据处理层
 */
public interface ActNodeDao extends EapBaseDao<ActNode,String> {

    /**
     * 通过nodeId获取
     * @param nodeId
     * @param type
     * @return
     */
    List<ActNode> findByNodeIdAndType(String nodeId, Integer type);

    /**
     * 通过nodeId删除
     * @param nodeId
     */
    void deleteByNodeId(String nodeId);

    /**
     * 通过relateId删除
     * @param relateId
     */
    void deleteByRelateId(String relateId);
}