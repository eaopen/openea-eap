package org.openea.eap.module.system.service.mail;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.annotations.VisibleForTesting;
import lombok.extern.slf4j.Slf4j;
import org.openea.eap.framework.common.pojo.PageResult;
import org.openea.eap.module.system.controller.admin.mail.vo.template.MailTemplateCreateReqVO;
import org.openea.eap.module.system.controller.admin.mail.vo.template.MailTemplatePageReqVO;
import org.openea.eap.module.system.controller.admin.mail.vo.template.MailTemplateUpdateReqVO;
import org.openea.eap.module.system.convert.mail.MailTemplateConvert;
import org.openea.eap.module.system.dal.dataobject.mail.MailTemplateDO;
import org.openea.eap.module.system.dal.mysql.mail.MailTemplateMapper;
import org.openea.eap.module.system.dal.redis.RedisKeyConstants;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.openea.eap.framework.common.exception.util.ServiceExceptionUtil.exception;
import static org.openea.eap.module.system.enums.ErrorCodeConstants.MAIL_TEMPLATE_CODE_EXISTS;
import static org.openea.eap.module.system.enums.ErrorCodeConstants.MAIL_TEMPLATE_NOT_EXISTS;

/**
 * 邮箱模版 Service 实现类
 *
 * @author wangjingyi
 * @since 2022-03-21
 */
@Service
@Validated
@Slf4j
public class MailTemplateServiceImpl implements MailTemplateService {

    /**
     * 正则表达式，匹配 {} 中的变量
     */
    private static final Pattern PATTERN_PARAMS = Pattern.compile("\\{(.*?)}");

    @Resource
    private MailTemplateMapper mailTemplateMapper;

    @Override
    public Long createMailTemplate(MailTemplateCreateReqVO createReqVO) {
        // 校验 code 是否唯一
        validateCodeUnique(null, createReqVO.getCode());

        // 插入
        MailTemplateDO template = MailTemplateConvert.INSTANCE.convert(createReqVO)
                .setParams(parseTemplateContentParams(createReqVO.getContent()));
        mailTemplateMapper.insert(template);
        return template.getId();
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.NOTIFY_TEMPLATE,
            allEntries = true) // allEntries 清空所有缓存，因为可能修改到 code 字段，不好清理
    public void updateMailTemplate(@Valid MailTemplateUpdateReqVO updateReqVO) {
        // 校验是否存在
        validateMailTemplateExists(updateReqVO.getId());
        // 校验 code 是否唯一
        validateCodeUnique(updateReqVO.getId(),updateReqVO.getCode());

        // 更新
        MailTemplateDO updateObj = MailTemplateConvert.INSTANCE.convert(updateReqVO)
                .setParams(parseTemplateContentParams(updateReqVO.getContent()));
        mailTemplateMapper.updateById(updateObj);
    }

    @VisibleForTesting
    void validateCodeUnique(Long id, String code) {
        MailTemplateDO template = mailTemplateMapper.selectByCode(code);
        if (template == null) {
            return;
        }
        // 存在 template 记录的情况下
        if (id == null // 新增时，说明重复
                || ObjUtil.notEqual(id, template.getId())) { // 更新时，如果 id 不一致，说明重复
            throw exception(MAIL_TEMPLATE_CODE_EXISTS);
        }
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.NOTIFY_TEMPLATE,
            allEntries = true) // allEntries 清空所有缓存，因为 id 不是直接的缓存 code，不好清理
    public void deleteMailTemplate(Long id) {
        // 校验是否存在
        validateMailTemplateExists(id);

        // 删除
        mailTemplateMapper.deleteById(id);
    }

    private void validateMailTemplateExists(Long id) {
        if (mailTemplateMapper.selectById(id) == null) {
            throw exception(MAIL_TEMPLATE_NOT_EXISTS);
        }
    }

    @Override
    public MailTemplateDO getMailTemplate(Long id) {return mailTemplateMapper.selectById(id);}

    @Override
    @Cacheable(value = RedisKeyConstants.MAIL_TEMPLATE, key = "#code", unless = "#result == null")
    public MailTemplateDO getMailTemplateByCodeFromCache(String code) {
        return mailTemplateMapper.selectByCode(code);
    }

    @Override
    public PageResult<MailTemplateDO> getMailTemplatePage(MailTemplatePageReqVO pageReqVO) {
        return mailTemplateMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MailTemplateDO> getMailTemplateList() {return mailTemplateMapper.selectList();}

    @Override
    public String formatMailTemplateContent(String content, Map<String, Object> params) {
        return StrUtil.format(content, params);
    }

    @VisibleForTesting
    public List<String> parseTemplateContentParams(String content) {
        return ReUtil.findAllGroup1(PATTERN_PARAMS, content);
    }

    @Override
    public long countByAccountId(Long accountId) {
        return mailTemplateMapper.selectCountByAccountId(accountId);
    }

}
