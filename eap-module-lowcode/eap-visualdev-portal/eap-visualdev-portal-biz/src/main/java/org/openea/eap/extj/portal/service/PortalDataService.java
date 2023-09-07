package org.openea.eap.extj.portal.service;

import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.portal.model.PortalModPrimary;
import org.openea.eap.extj.portal.model.PortalReleasePrimary;
import org.openea.eap.extj.portal.entity.PortalDataEntity;
import org.openea.eap.extj.portal.model.*;

import java.util.List;

/**
 * 门户数据接口
 *
 * 后期门户模板数据，将会以platform再做区分，
 * 故将formData抽离成单独的表
 */
public interface PortalDataService extends SuperService<PortalDataEntity> {

    String getCustomDataForm(PortalCustomPrimary primary) throws Exception;

    String getModelDataForm(PortalModPrimary primary) throws Exception;

    /**
     * 发布
     */
    void release(String platform, String portalId, String systemIdListStr,String releasePlatform) throws Exception;

    Boolean isReleaseFlag(PortalReleasePrimary primary);

    Boolean deleteAll(String portalId);

    /**
     * 创建或更新门户自定义信息
     */
    void createOrUpdate(PortalCustomPrimary primary, String formData) throws Exception;

    /**
     * 创建或更新门户模板信息
     */
    void createOrUpdate(PortalModPrimary primary, String formData) throws Exception;

    /**
     * 创建或更新门户发布信息
     */
    void createOrUpdate(PortalReleasePrimary primary, String formData) throws Exception;

    /**
     * 获取门户显示信息
     *
     * @param portalId 门户ID
     * @param platform 平台：app/pc
     */
    PortalInfoAuthVO getDataFormView(String portalId, String platform) throws Exception;

    /**
     * 设置默认门户
     */
    void setCurrentDefault(String platform, String portalId);

    String getCurrentDefault(String platform) throws Exception;

    /**
     * 获取当前带权限的portalIds
     */
    List<String> getCurrentAuthPortalIds(PortalViewPrimary primary);

}
