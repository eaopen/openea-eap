package org.openea.eap.extj.portal.model.portalManage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.openea.eap.extj.base.MyBatisPrimaryBase;
import org.openea.eap.extj.portal.entity.PortalManageEntity;

/**
 * 门户管理联合主键类
 *
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
public class PortalManagePrimary extends MyBatisPrimaryBase<PortalManageEntity> {

    @Schema(description = "平台")
    private String platform;

    @Schema(description = "门户ID")
    private String portalId;

    @Schema(description = "系统ID")
    private String systemId;

    public QueryWrapper<PortalManageEntity> getQuery(){
        if(this.platform != null) queryWrapper.lambda().eq(PortalManageEntity::getPlatform, platform);
        if(this.portalId != null) queryWrapper.lambda().eq(PortalManageEntity::getPortalId, portalId);
        if(this.systemId != null) queryWrapper.lambda().eq(PortalManageEntity::getSystemId, systemId);
        return queryWrapper;
    }

    @Override
    public PortalManageEntity getEntity() throws Exception {
        PortalManageEntity entity =  super.getEntity();
        entity.setSortCode(0L);
        return entity;
    }

}
