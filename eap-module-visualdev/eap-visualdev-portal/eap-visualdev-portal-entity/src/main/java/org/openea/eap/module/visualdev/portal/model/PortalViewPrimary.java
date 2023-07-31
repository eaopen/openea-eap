package org.openea.eap.module.visualdev.portal.model;

import org.openea.eap.module.visualdev.base.MyBatisPrimaryBase;
import org.openea.eap.module.visualdev.base.UserInfo;
import org.openea.eap.module.visualdev.portal.entity.PortalEntity;
import org.openea.eap.module.visualdev.util.UserProvider;
import org.openea.eap.module.visualdev.util.context.SpringContext;
import lombok.Data;

/**
 * 类功能
 *
 */
@Data
public class PortalViewPrimary extends MyBatisPrimaryBase<PortalEntity> {

    private String creatorId;

    private String portalId;

    private String platForm = "web";

    private String systemId;

    public PortalViewPrimary(String platForm, String portalId){
        if(platForm != null) this.platForm = platForm;
        this.portalId = portalId;
        UserInfo userInfo = SpringContext.getBean(UserProvider.class).get();
        this.systemId = userInfo.getSystemId();
        this.creatorId = userInfo.getId();
    }

}
