package org.openea.eapboot.modules.activiti.service;

import org.openea.eapboot.base.EapBaseService;
import org.openea.eapboot.modules.activiti.entity.ActCategory;

import java.util.List;

/**
 * 流程分类接口
 */
public interface ActCategoryService extends EapBaseService<ActCategory,String> {

    /**
     * 通过父id获取
     * @param parentId
     * @return
     */
    List<ActCategory> findByParentIdOrderBySortOrder(String parentId);

    /**
     * 通过名称模糊搜索
     * @param title
     * @return
     */
    List<ActCategory> findByTitleLikeOrderBySortOrder(String title);
}