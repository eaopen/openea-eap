package org.openea.eapboot.modules.activiti.service;

import org.openea.eapboot.base.EapBaseService;
import org.openea.eapboot.common.vo.SearchVo;
import org.openea.eapboot.modules.activiti.entity.ActBusiness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 业务申请接口
 */
public interface ActBusinessService extends EapBaseService<ActBusiness,String> {

    /**
     * 多条件分页获取申请列表
     * @param actBusiness
     * @param searchVo
     * @param pageable
     * @return
     */
    Page<ActBusiness> findByCondition(ActBusiness actBusiness, SearchVo searchVo, Pageable pageable);

    /**
     * 通过流程定义id获取
     * @param procDefId
     * @return
     */
    List<ActBusiness> findByProcDefId(String procDefId);
}