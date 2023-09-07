package org.openea.eap.extj.portal.service;

import org.openea.eap.extj.base.model.VisualFunctionModel;
import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.portal.entity.PortalEntity;
import org.openea.eap.extj.portal.model.PortalPagination;
import org.openea.eap.extj.portal.model.PortalSelectModel;
import org.openea.eap.extj.portal.model.PortalSelectVO;
import org.openea.eap.extj.portal.model.PortalViewPrimary;

import java.util.List;


/**
 * base_portal
 */

public interface PortalService extends SuperService<PortalEntity> {

    PortalEntity getInfo(String id);

    /**
     * 是否重名
     */
    Boolean isExistByFullName(String fullName, String id);

    /**
     * 是否重码
     */
    Boolean isExistByEnCode(String encode, String id);

    void create(PortalEntity entity);

    Boolean update(String id, PortalEntity entity);

    void delete(PortalEntity entity) throws Exception;

    List<PortalEntity> getList(PortalPagination pagination);

    String getModListFirstId(PortalViewPrimary primary);

    List<PortalSelectModel> getModList(PortalViewPrimary primary);

    /**
     * 获取门户模型集合
     *
     * @param pagination 分页信息
     * @return 模型集合
     */
    List<VisualFunctionModel> getModelList(PortalPagination pagination);


    /**
     * 获取门户管理下拉
     *
     * @param pagination 分页信息
     * @param systemId   系统ID
     * @return 分页结婚
     */
    List<PortalSelectVO> getManageSelectorPage(PortalPagination pagination, String systemId);

}
