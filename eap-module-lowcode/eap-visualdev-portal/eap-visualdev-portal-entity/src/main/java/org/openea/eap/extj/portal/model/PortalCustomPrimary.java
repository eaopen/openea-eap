package org.openea.eap.extj.portal.model;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;
import org.openea.eap.extj.base.MyBatisPrimaryBase;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.portal.constant.PortalConst;
import org.openea.eap.extj.portal.entity.PortalDataEntity;
import org.openea.eap.extj.util.UserProvider;
import org.openea.eap.framework.common.util.spring.EapAppUtil;

/**
 * 类功能
 *
 */
@Data
public class PortalCustomPrimary extends MyBatisPrimaryBase<PortalDataEntity> {

    /** 平台 */
    private String platform = PortalConst.WEB;
    /** 门户ID */
    private String portalId;
    /** 系统ID */
    private String systemId;
    /** 用户ID */
    private String creatorId;
    /** 类型（mod：模型、custom：自定义） */
    private String type = PortalConst.CUSTOM;

    public PortalCustomPrimary(String platform, String portalId, String systemId, String userId) {
        if(platform != null) this.platform = platform;
        this.portalId = portalId;
        this.systemId = systemId;
        this.creatorId = userId;
    }

    public PortalCustomPrimary(String platform, String portalId){
        if(platform != null) this.platform = platform;
        this.portalId = portalId;
        UserInfo userInfo =  EapAppUtil.getBean(UserProvider.class).get();
        this.systemId = userInfo.getSystemId();
        this.creatorId = userInfo.getUserId();
    }

    public QueryWrapper<PortalDataEntity> getQuery(){
        queryWrapper.lambda().eq(PortalDataEntity::getType, type);
        if(this.platform != null) queryWrapper.lambda().eq(PortalDataEntity::getPlatform, platform);
        if(this.portalId != null) queryWrapper.lambda().eq(PortalDataEntity::getPortalId, portalId);
        if(this.systemId != null) queryWrapper.lambda().eq(PortalDataEntity::getSystemId, systemId);
        if(this.creatorId != null) queryWrapper.lambda().eq(PortalDataEntity::getCreatorUserId, creatorId);
        return queryWrapper;
    }

}
