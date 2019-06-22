package org.openea.eapboot.modules.activiti.dao;

import org.openea.eapboot.base.EapBaseDao;
import org.openea.eapboot.modules.activiti.entity.ActBusiness;

import java.util.List;

/**
 * 申请业务数据处理层
 */
public interface ActBusinessDao extends EapBaseDao<ActBusiness,String> {

    /**
     * 通过流程定义id获取
     * @param procDefId
     * @return
     */
    List<ActBusiness> findByProcDefId(String procDefId);
}