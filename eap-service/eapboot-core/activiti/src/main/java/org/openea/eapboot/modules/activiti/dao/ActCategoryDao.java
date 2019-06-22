package org.openea.eapboot.modules.activiti.dao;

import org.openea.eapboot.base.EapBaseDao;
import org.openea.eapboot.modules.activiti.entity.ActCategory;

import java.util.List;

/**
 * 流程分类数据处理层
 */
public interface ActCategoryDao extends EapBaseDao<ActCategory,String> {

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