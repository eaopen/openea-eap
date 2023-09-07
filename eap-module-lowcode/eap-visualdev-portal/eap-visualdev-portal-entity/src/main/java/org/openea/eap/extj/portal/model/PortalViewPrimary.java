package org.openea.eap.extj.portal.model;

import org.openea.eap.extj.base.MyBatisPrimaryBase;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.portal.entity.PortalEntity;
import org.openea.eap.extj.util.UserProvider;
import org.openea.eap.extj.util.context.SpringContext;
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
