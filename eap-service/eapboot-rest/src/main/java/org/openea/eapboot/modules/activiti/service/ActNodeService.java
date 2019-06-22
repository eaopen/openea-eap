package org.openea.eapboot.modules.activiti.service;

import org.openea.eapboot.base.EapBaseService;
import org.openea.eapboot.modules.base.entity.Department;
import org.openea.eapboot.modules.base.entity.Role;
import org.openea.eapboot.modules.base.entity.User;
import org.openea.eapboot.modules.activiti.entity.ActNode;

import java.util.List;

/**
 * 流程节点用户接口
 */
public interface ActNodeService extends EapBaseService<ActNode,String> {

    /**
     * 通过nodeId获取用户
     * @param nodeId
     * @return
     */
    List<User> findUserByNodeId(String nodeId);

    /**
     * 通过nodeId获取角色
     * @param nodeId
     * @return
     */
    List<Role> findRoleByNodeId(String nodeId);

    /**
     * 通过nodeId获取部门
     * @param nodeId
     * @return
     */
    List<Department> findDepartmentByNodeId(String nodeId);

    /**
     * 通过nodeId获取部门id
     * @param nodeId
     * @return
     */
    List<String> findDepartmentIdsByNodeId(String nodeId);

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