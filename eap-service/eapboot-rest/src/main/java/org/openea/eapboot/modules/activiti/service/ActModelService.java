package org.openea.eapboot.modules.activiti.service;

import org.openea.eapboot.base.EapBaseService;
import org.openea.eapboot.modules.activiti.entity.ActModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 模型管理接口
 */
public interface ActModelService extends EapBaseService<ActModel,String> {

    /**
     * 多条件分页获取
     * @param actModel
     * @param pageable
     * @return
     */
    Page<ActModel> findByCondition(ActModel actModel, Pageable pageable);
}