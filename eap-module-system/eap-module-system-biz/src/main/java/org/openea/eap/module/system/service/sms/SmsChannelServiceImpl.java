package org.openea.eap.module.system.service.sms;

import cn.hutool.core.util.StrUtil;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openea.eap.framework.common.pojo.PageResult;
import org.openea.eap.framework.sms.core.client.SmsClient;
import org.openea.eap.framework.sms.core.client.SmsClientFactory;
import org.openea.eap.framework.sms.core.property.SmsChannelProperties;
import org.openea.eap.module.system.controller.admin.sms.vo.channel.SmsChannelCreateReqVO;
import org.openea.eap.module.system.controller.admin.sms.vo.channel.SmsChannelPageReqVO;
import org.openea.eap.module.system.controller.admin.sms.vo.channel.SmsChannelUpdateReqVO;
import org.openea.eap.module.system.convert.sms.SmsChannelConvert;
import org.openea.eap.module.system.dal.dataobject.sms.SmsChannelDO;
import org.openea.eap.module.system.dal.mysql.sms.SmsChannelMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.List;

import static org.openea.eap.framework.common.exception.util.ServiceExceptionUtil.exception;
import static org.openea.eap.framework.common.util.cache.CacheUtils.buildAsyncReloadingCache;
import static org.openea.eap.module.system.enums.ErrorCodeConstants.SMS_CHANNEL_HAS_CHILDREN;
import static org.openea.eap.module.system.enums.ErrorCodeConstants.SMS_CHANNEL_NOT_EXISTS;

/**
 * 短信渠道 Service 实现类
 *
 * @author zzf
 */
@Service
@Slf4j
public class SmsChannelServiceImpl implements SmsChannelService {

    /**
     * {@link SmsClient} 缓存，通过它异步刷新 smsClientFactory
     */
    @Getter
    private final LoadingCache<Long, SmsClient> idClientCache = buildAsyncReloadingCache(Duration.ofSeconds(10L),
            new CacheLoader<Long, SmsClient>() {

                @Override
                public SmsClient load(Long id) {
                    // 查询，然后尝试刷新
                    SmsChannelDO channel = smsChannelMapper.selectById(id);
                    if (channel != null) {
                        SmsChannelProperties properties = SmsChannelConvert.INSTANCE.convert02(channel);
                        smsClientFactory.createOrUpdateSmsClient(properties);
                    }
                    return smsClientFactory.getSmsClient(id);
                }

            });

    /**
     * {@link SmsClient} 缓存，通过它异步刷新 smsClientFactory
     */
    @Getter
    private final LoadingCache<String, SmsClient> codeClientCache = buildAsyncReloadingCache(Duration.ofSeconds(60L),
            new CacheLoader<String, SmsClient>() {

                @Override
                public SmsClient load(String code) {
                    // 查询，然后尝试刷新
                    SmsChannelDO channel = smsChannelMapper.selectByCode(code);
                    if (channel != null) {
                        SmsChannelProperties properties = SmsChannelConvert.INSTANCE.convert02(channel);
                        smsClientFactory.createOrUpdateSmsClient(properties);
                    }
                    return smsClientFactory.getSmsClient(code);
                }

            });

    @Resource
    private SmsClientFactory smsClientFactory;

    @Resource
    private SmsChannelMapper smsChannelMapper;

    @Resource
    private SmsTemplateService smsTemplateService;

    @Override
    public Long createSmsChannel(SmsChannelCreateReqVO createReqVO) {
        SmsChannelDO channel = SmsChannelConvert.INSTANCE.convert(createReqVO);
        smsChannelMapper.insert(channel);
        return channel.getId();
    }

    @Override
    public void updateSmsChannel(SmsChannelUpdateReqVO updateReqVO) {
        // 校验存在
        SmsChannelDO channel = validateSmsChannelExists(updateReqVO.getId());
        // 更新
        SmsChannelDO updateObj = SmsChannelConvert.INSTANCE.convert(updateReqVO);
        smsChannelMapper.updateById(updateObj);

        // 清空缓存
        clearCache(updateReqVO.getId(), channel.getCode());
    }

    @Override
    public void deleteSmsChannel(Long id) {
        // 校验存在
        SmsChannelDO channel = validateSmsChannelExists(id);
        // 校验是否有在使用该账号的模版
        if (smsTemplateService.countByChannelId(id) > 0) {
            throw exception(SMS_CHANNEL_HAS_CHILDREN);
        }
        // 删除
        smsChannelMapper.deleteById(id);

        // 清空缓存
        clearCache(id, channel.getCode());
    }

    /**
     * 清空指定渠道编号的缓存
     *
     * @param id 渠道编号
     * @param code 渠道编码
     */
    private void clearCache(Long id, String code) {
        idClientCache.invalidate(id);
        if (StrUtil.isNotEmpty(code)) {
            codeClientCache.invalidate(code);
        }
    }

    private SmsChannelDO validateSmsChannelExists(Long id) {
        SmsChannelDO channel = smsChannelMapper.selectById(id);
        if (channel == null) {
            throw exception(SMS_CHANNEL_NOT_EXISTS);
        }
        return channel;
    }

    @Override
    public SmsChannelDO getSmsChannel(Long id) {
        return smsChannelMapper.selectById(id);
    }

    @Override
    public List<SmsChannelDO> getSmsChannelList() {
        return smsChannelMapper.selectList();
    }

    @Override
    public PageResult<SmsChannelDO> getSmsChannelPage(SmsChannelPageReqVO pageReqVO) {
        return smsChannelMapper.selectPage(pageReqVO);
    }

    @Override
    public SmsClient getSmsClient(Long id) {
        return idClientCache.getUnchecked(id);
    }

    @Override
    public SmsClient getSmsClient(String code) {
        return codeClientCache.getUnchecked(code);
    }

}
