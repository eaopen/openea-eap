package org.openea.eap.module.visualdev.portal.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openea.eap.extj.base.mapper.SuperMapper;
import org.openea.eap.module.visualdev.portal.entity.PortalManageEntity;
import org.openea.eap.module.visualdev.portal.model.portalManage.PortalManagePage;
import org.openea.eap.module.visualdev.portal.model.portalManage.PortalManagePageDO;

import java.util.List;

/**
 * 门户管理
 *
 */

@Mapper
public interface PortalManageMapper extends SuperMapper<PortalManageEntity> {

    @Select("SELECT F_FullName FROM base_portal WHERE F_Id = #{portalId}")
    String getPortalFullName(String portalId);

    @Select("SELECT F_Category FROM base_portal WHERE F_Id = #{portalId}")
    String getPortalCategoryId(String portalId);

    PageDTO<PortalManagePageDO> selectPortalManageDoPage(PageDTO<PortalManagePageDO> page, @Param("pmPage") PortalManagePage pmPage);

    List<PortalManagePageDO> selectPortalManageDoList(@Param("pmPage") PortalManagePage pmPage);

}
