package org.openea.eap.module.system.service.notify;

import org.openea.eap.framework.common.pojo.PageResult;
import org.openea.eap.module.system.controller.admin.notify.vo.template.NotifyTemplateCreateReqVO;
import org.openea.eap.module.system.controller.admin.notify.vo.template.NotifyTemplatePageReqVO;
import org.openea.eap.module.system.controller.admin.notify.vo.template.NotifyTemplateUpdateReqVO;
import org.openea.eap.module.system.dal.dataobject.notify.NotifyTemplateDO;

import javax.validation.Valid;
import java.util.Map;

/**
 * 站内信模版 Service 接口
 *
 * @author xrcoder
 */
public interface NotifyTemplateService {

    /**
     * 创建站内信模版
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createNotifyTemplate(@Valid NotifyTemplateCreateReqVO createReqVO);

    /**
     * 更新站内信模版
     *
     * @param updateReqVO 更新信息
     */
    void updateNotifyTemplate(@Valid NotifyTemplateUpdateReqVO updateReqVO);

    /**
     * 删除站内信模版
     *
     * @param id 编号
     */
    void deleteNotifyTemplate(Long id);

    /**
     * 获得站内信模版
     *
     * @param id 编号
     * @return 站内信模版
     */
    NotifyTemplateDO getNotifyTemplate(Long id);

    /**
     * 获得站内信模板，从缓存中
     *
     * @param code 模板编码
     * @return 站内信模板
     */
    NotifyTemplateDO getNotifyTemplateByCodeFromCache(String code);

    /**
     * 获得站内信模版分页
     *
     * @param pageReqVO 分页查询
     * @return 站内信模版分页
     */
    PageResult<NotifyTemplateDO> getNotifyTemplatePage(NotifyTemplatePageReqVO pageReqVO);

    /**
     * 格式化站内信内容
     *
     * @param content 站内信模板的内容
     * @param params 站内信内容的参数
     * @return 格式化后的内容
     */
    String formatNotifyTemplateContent(String content, Map<String, Object> params);

}
