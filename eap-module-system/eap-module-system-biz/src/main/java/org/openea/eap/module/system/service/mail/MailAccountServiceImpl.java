package org.openea.eap.module.system.service.mail;

import lombok.extern.slf4j.Slf4j;
import org.openea.eap.framework.common.pojo.PageResult;
import org.openea.eap.module.system.controller.admin.mail.vo.account.MailAccountCreateReqVO;
import org.openea.eap.module.system.controller.admin.mail.vo.account.MailAccountPageReqVO;
import org.openea.eap.module.system.controller.admin.mail.vo.account.MailAccountUpdateReqVO;
import org.openea.eap.module.system.convert.mail.MailAccountConvert;
import org.openea.eap.module.system.dal.dataobject.mail.MailAccountDO;
import org.openea.eap.module.system.dal.mysql.mail.MailAccountMapper;
import org.openea.eap.module.system.dal.redis.RedisKeyConstants;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

import static org.openea.eap.framework.common.exception.util.ServiceExceptionUtil.exception;
import static org.openea.eap.module.system.enums.ErrorCodeConstants.MAIL_ACCOUNT_NOT_EXISTS;
import static org.openea.eap.module.system.enums.ErrorCodeConstants.MAIL_ACCOUNT_RELATE_TEMPLATE_EXISTS;

/**
 * 邮箱账号 Service 实现类
 *
 * @author wangjingyi
 * @since 2022-03-21
 */
@Service
@Validated
@Slf4j
public class MailAccountServiceImpl implements MailAccountService {

    @Resource
    private MailAccountMapper mailAccountMapper;

    @Resource
    private MailTemplateService mailTemplateService;

    @Override
    public Long createMailAccount(MailAccountCreateReqVO createReqVO) {
        // 插入
        MailAccountDO account = MailAccountConvert.INSTANCE.convert(createReqVO);
        mailAccountMapper.insert(account);
        return account.getId();
    }

    @Override
    @Cacheable(value = RedisKeyConstants.MAIL_ACCOUNT, key = "#updateReqVO.id")
    public void updateMailAccount(MailAccountUpdateReqVO updateReqVO) {
        // 校验是否存在
        validateMailAccountExists(updateReqVO.getId());

        // 更新
        MailAccountDO updateObj = MailAccountConvert.INSTANCE.convert(updateReqVO);
        mailAccountMapper.updateById(updateObj);
    }

    @Override
    @Cacheable(value = RedisKeyConstants.MAIL_ACCOUNT, key = "#id")
    public void deleteMailAccount(Long id) {
        // 校验是否存在账号
        validateMailAccountExists(id);
        // 校验是否存在关联模版
        if (mailTemplateService.countByAccountId(id) > 0) {
            throw exception(MAIL_ACCOUNT_RELATE_TEMPLATE_EXISTS);
        }

        // 删除
        mailAccountMapper.deleteById(id);
    }

    private void validateMailAccountExists(Long id) {
        if (mailAccountMapper.selectById(id) == null) {
            throw exception(MAIL_ACCOUNT_NOT_EXISTS);
        }
    }

    @Override
    public MailAccountDO getMailAccount(Long id) {
        return mailAccountMapper.selectById(id);
    }

    @Override
    @Cacheable(value = RedisKeyConstants.MAIL_ACCOUNT, key = "#id", unless = "#result == null")
    public MailAccountDO getMailAccountFromCache(Long id) {
        return getMailAccount(id);
    }

    @Override
    public PageResult<MailAccountDO> getMailAccountPage(MailAccountPageReqVO pageReqVO) {
        return mailAccountMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MailAccountDO> getMailAccountList() {
        return mailAccountMapper.selectList();
    }

}
