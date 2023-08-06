package org.openea.eap.module.visualdev.portal.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.module.visualdev.portal.entity.PortalManageEntity;
import org.openea.eap.module.visualdev.portal.model.PortalManageVO;
import org.openea.eap.module.visualdev.portal.model.portalManage.PortalManagePage;
import org.openea.eap.module.visualdev.portal.model.portalManage.PortalManagePageDO;
import org.openea.eap.module.visualdev.portal.model.portalManage.PortalManagePrimary;

import java.util.List;

public interface PortalManageService extends SuperService<PortalManageEntity> {

    void checkCreUp(PortalManageEntity portalManageEntity) throws Exception;

    PortalManageVO convertVO(PortalManageEntity entity);

    List<PortalManageVO> getList(PortalManagePrimary primary);

    List<PortalManageVO> getListByEnable(PortalManagePrimary primary);

    PageDTO<PortalManagePageDO> getPage(PortalManagePage portalPagination);

    List<PortalManagePageDO> getSelectList(PortalManagePage pmPage);

    void createBatch(List<PortalManagePrimary> primaryLit) throws Exception;

}
