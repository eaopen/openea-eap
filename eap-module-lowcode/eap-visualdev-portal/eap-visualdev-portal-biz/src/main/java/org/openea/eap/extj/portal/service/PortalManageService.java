package org.openea.eap.extj.portal.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.portal.entity.PortalManageEntity;
import org.openea.eap.extj.portal.model.portalManage.PortalManagePage;
import org.openea.eap.extj.portal.model.portalManage.PortalManagePageDO;
import org.openea.eap.extj.portal.model.portalManage.PortalManagePrimary;
import org.openea.eap.extj.portal.model.portalManage.PortalManageVO;

import java.util.List;

/**
 * <p>
 * 门户管理 服务类
 * </p>
 *
 */
public interface PortalManageService extends SuperService<PortalManageEntity> {

    void checkCreUp(PortalManageEntity portalManageEntity) throws Exception;

    PortalManageVO convertVO(PortalManageEntity entity);

    List<PortalManageVO> getList(PortalManagePrimary primary);

    List<PortalManageVO> getListByEnable(PortalManagePrimary primary);

    PageDTO<PortalManagePageDO> getPage(PortalManagePage portalPagination);

    List<PortalManagePageDO> getSelectList(PortalManagePage pmPage);

    void createBatch(List<PortalManagePrimary> primaryLit) throws Exception;

}
