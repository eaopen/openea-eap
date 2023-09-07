package org.openea.eap.extj.base.service;


import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.base.entity.VisualdevEntity;
import org.openea.eap.extj.base.model.PaginationVisualdev;

import java.util.List;
import java.util.Map;

/**
 *

 */
public interface VisualdevService extends SuperService<VisualdevEntity> {

    List<VisualdevEntity> getList(PaginationVisualdev paginationVisualdev);

    List<VisualdevEntity> getList();

    VisualdevEntity getInfo(String id);


    /**
     * 获取已发布的版本, 若未发布获取当前版本
     * @param id
     * @return
     */
    VisualdevEntity getReleaseInfo(String id);

    /**
     * 获取动态设计子表名和实际库表名的对应
     * @param formData
     * @return
     */
    Map<String,String> getTableMap(String formData);

    Boolean create(VisualdevEntity entity);

    boolean update(String id, VisualdevEntity entity) throws Exception;

    void delete(VisualdevEntity entity) throws Exception;

    /**
     * 根据encode判断是否有相同值
     * @param encode
     * @return
     */
    Long getObjByEncode (String encode, Integer type);

    /**
     * 根据name判断是否有相同值
     * @param name
     * @return
     */
    Long getCountByName (String name, Integer type);

    /**
     * 无表生成有表
     * @param entity
     */
    void createTable(VisualdevEntity entity) throws Exception;

    Map<String,String> getTableNameToKey(String modelId);

    Boolean getPrimaryDbField(String linkId, String  table) throws Exception;
}
