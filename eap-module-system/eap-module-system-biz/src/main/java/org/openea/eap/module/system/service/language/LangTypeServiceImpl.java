package org.openea.eap.module.system.service.language;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import org.openea.eap.module.system.controller.admin.language.vo.*;
import org.openea.eap.module.system.dal.dataobject.language.LangTypeDO;
import org.openea.eap.framework.common.pojo.PageResult;

import org.openea.eap.module.system.convert.language.LangTypeConvert;
import org.openea.eap.module.system.dal.mysql.language.LangTypeMapper;

import static org.openea.eap.framework.common.exception.util.ServiceExceptionUtil.exception;
import static org.openea.eap.module.system.enums.ErrorCodeConstants.*;

/**
 * 语言 Service 实现类
 *
 * @author eap
 */
@Service
@Validated
public class LangTypeServiceImpl implements LangTypeService {

    @Resource
    private LangTypeMapper langTypeMapper;

    @Override
    public Long createLangType(LangTypeCreateReqVO createReqVO) {
        // 插入
        LangTypeDO langType = LangTypeConvert.INSTANCE.convert(createReqVO);
        langTypeMapper.insert(langType);
        // 返回
        return langType.getId();
    }

    @Override
    public void updateLangType(LangTypeUpdateReqVO updateReqVO) {
        // 校验存在
        validateLangTypeExists(updateReqVO.getId());
        // 更新
        LangTypeDO updateObj = LangTypeConvert.INSTANCE.convert(updateReqVO);
        langTypeMapper.updateById(updateObj);
    }

    @Override
    public void deleteLangType(Long id) {
        // 校验存在
        validateLangTypeExists(id);
        // 删除
        langTypeMapper.deleteById(id);
    }

    private void validateLangTypeExists(Long id) {
        if (langTypeMapper.selectById(id) == null) {
            throw exception(LANG_TYPE_NOT_EXISTS);
        }
    }

    @Override
    public LangTypeDO getLangType(Long id) {
        return langTypeMapper.selectById(id);
    }

    @Override
    public List<LangTypeDO> getLangTypeList(Collection<Long> ids) {
        return langTypeMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<LangTypeDO> getLangTypePage(LangTypePageReqVO pageReqVO) {
        return langTypeMapper.selectPage(pageReqVO);
    }

    @Override
    public List<LangTypeDO> getLangTypeList(LangTypeExportReqVO exportReqVO) {
        return langTypeMapper.selectList(exportReqVO);
    }

}
