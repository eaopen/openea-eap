package org.openea.eap.extj.portal.model;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.extj.base.MyBatisPrimaryBase;
import org.openea.eap.extj.portal.constant.PortalConst;
import org.openea.eap.extj.portal.entity.PortalDataEntity;
import lombok.Data;

/**
 * 类功能
 *
 */
@Data
public class PortalModPrimary extends MyBatisPrimaryBase<PortalDataEntity> {

    /**
     * 门户ID
     */
    private String portalId;

    /**
     * 类型（model：模型、custom：自定义）
     */
    private String type = PortalConst.MODEL;

    public PortalModPrimary(String portalId){
        this.portalId = portalId;
    }

    public QueryWrapper<PortalDataEntity> getQuery(){
        queryWrapper.lambda().eq(PortalDataEntity::getType, type);
        if(this.portalId != null) queryWrapper.lambda().eq(PortalDataEntity::getPortalId, portalId);
        return queryWrapper;
    }

}
