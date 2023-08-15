package org.openea.eap.module.system.service.language;

import cn.hutool.core.collection.CollectionUtil;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import org.openea.eap.module.system.controller.admin.language.vo.*;
import org.openea.eap.module.system.dal.dataobject.language.I18nJsonDataDO;
import org.openea.eap.framework.common.pojo.PageResult;

import org.openea.eap.module.system.convert.language.I18nJsonDataConvert;
import org.openea.eap.module.system.dal.mysql.language.I18nJsonDataMapper;

import static org.openea.eap.framework.common.exception.util.ServiceExceptionUtil.exception;
import static org.openea.eap.module.system.enums.ErrorCodeConstants.*;

/**
 * 翻译 Service 实现类
 *
 * @author eap
 */
@Service
@Validated
public class I18nJsonDataServiceImpl implements I18nJsonDataService {

    @Resource
    private I18nJsonDataMapper i18nJsonDataMapper;

    @Override
    public Long createI18nJsonData(I18nJsonDataCreateReqVO createReqVO) {
        // 插入
        I18nJsonDataDO i18nJsonData = I18nJsonDataConvert.INSTANCE.convert(createReqVO);
        i18nJsonDataMapper.insert(i18nJsonData);
        // 返回
        return i18nJsonData.getId();
    }

    @Override
    public void updateI18nJsonData(I18nJsonDataUpdateReqVO updateReqVO) {
        // 校验存在
        validateI18nJsonDataExists(updateReqVO.getId());
        // 更新
        I18nJsonDataDO updateObj = I18nJsonDataConvert.INSTANCE.convert(updateReqVO);
        i18nJsonDataMapper.updateById(updateObj);
    }

    @Override
    public void deleteI18nJsonData(Long id) {
        // 校验存在
        validateI18nJsonDataExists(id);
        // 删除
        i18nJsonDataMapper.deleteById(id);
    }

    private void validateI18nJsonDataExists(Long id) {
        if (i18nJsonDataMapper.selectById(id) == null) {
            throw exception(I18N_JSON_DATA_NOT_EXISTS);
        }
    }

    @Override
    public I18nJsonDataDO getI18nJsonData(Long id) {
        return i18nJsonDataMapper.selectById(id);
    }

    @Override
    public List<I18nJsonDataDO> getI18nJsonDataList(Collection<Long> ids) {
        return i18nJsonDataMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<I18nJsonDataDO> getI18nJsonDataPage(I18nJsonDataPageReqVO pageReqVO) {
        return i18nJsonDataMapper.selectPage(pageReqVO);
    }

    @Override
    public List<I18nJsonDataDO> getI18nJsonDataList(I18nJsonDataExportReqVO exportReqVO) {
        return i18nJsonDataMapper.selectList(exportReqVO);
    }

}
