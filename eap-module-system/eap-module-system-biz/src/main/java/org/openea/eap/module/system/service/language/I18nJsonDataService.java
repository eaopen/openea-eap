package org.openea.eap.module.system.service.language;

import java.util.*;
import javax.validation.*;
import org.openea.eap.module.system.controller.admin.language.vo.*;
import org.openea.eap.module.system.dal.dataobject.language.I18nJsonDataDO;
import org.openea.eap.framework.common.pojo.PageResult;

/**
 * 翻译 Service 接口
 *
 * @author eap
 */
public interface I18nJsonDataService {

    /**
     * 创建翻译
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createI18nJsonData(@Valid I18nJsonDataCreateReqVO createReqVO);

    /**
     * 更新翻译
     *
     * @param updateReqVO 更新信息
     */
    void updateI18nJsonData(@Valid I18nJsonDataUpdateReqVO updateReqVO);

    /**
     * 删除翻译
     *
     * @param id 编号
     */
    void deleteI18nJsonData(Long id);

    /**
     * 获得翻译
     *
     * @param id 编号
     * @return 翻译
     */
    I18nJsonDataDO getI18nJsonData(Long id);


    /**
     * 获得翻译列表
     *
     * @param ids 编号
     * @return 翻译列表
     */
    List<I18nJsonDataDO> getI18nJsonDataList(Collection<Long> ids);

    /**
     * 获得翻译分页
     *
     * @param pageReqVO 分页查询
     * @return 翻译分页
     */
    PageResult<I18nJsonDataDO> getI18nJsonDataPage(I18nJsonDataPageReqVO pageReqVO);

    /**
     * 获得翻译列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 翻译列表
     */
    List<I18nJsonDataDO> getI18nJsonDataList(I18nJsonDataExportReqVO exportReqVO);

}
