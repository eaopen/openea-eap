package org.openea.eap.module.system.service.sms;

import org.openea.eap.framework.common.enums.CommonStatusEnum;
import org.openea.eap.framework.common.exception.enums.GlobalErrorCodeConstants;
import org.openea.eap.framework.common.pojo.PageResult;
import org.openea.eap.framework.common.util.collection.ArrayUtils;
import org.openea.eap.framework.common.util.object.ObjectUtils;
import org.openea.eap.framework.sms.core.client.SmsClient;
import org.openea.eap.framework.sms.core.client.SmsClientFactory;
import org.openea.eap.framework.sms.core.client.SmsCommonResult;
import org.openea.eap.framework.sms.core.client.dto.SmsTemplateRespDTO;
import org.openea.eap.framework.test.core.ut.BaseDbUnitTest;
import org.openea.eap.module.system.controller.admin.sms.vo.template.SmsTemplateCreateReqVO;
import org.openea.eap.module.system.controller.admin.sms.vo.template.SmsTemplateExportReqVO;
import org.openea.eap.module.system.controller.admin.sms.vo.template.SmsTemplatePageReqVO;
import org.openea.eap.module.system.controller.admin.sms.vo.template.SmsTemplateUpdateReqVO;
import org.openea.eap.module.system.dal.dataobject.sms.SmsChannelDO;
import org.openea.eap.module.system.dal.dataobject.sms.SmsTemplateDO;
import org.openea.eap.module.system.dal.mysql.sms.SmsTemplateMapper;
import org.openea.eap.module.system.enums.sms.SmsTemplateTypeEnum;
import org.openea.eap.module.system.mq.producer.sms.SmsProducer;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static cn.hutool.core.util.RandomUtil.randomEle;
import static org.openea.eap.framework.common.util.date.LocalDateTimeUtils.buildBetweenTime;
import static org.openea.eap.framework.common.util.date.LocalDateTimeUtils.buildTime;
import static org.openea.eap.framework.test.core.util.AssertUtils.assertPojoEquals;
import static org.openea.eap.framework.test.core.util.AssertUtils.assertServiceException;
import static org.openea.eap.framework.test.core.util.RandomUtils.*;
import static org.openea.eap.module.system.enums.ErrorCodeConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@Import(SmsTemplateServiceImpl.class)
public class SmsTemplateServiceImplTest extends BaseDbUnitTest {

    @Resource
    private SmsTemplateServiceImpl smsTemplateService;

    @Resource
    private SmsTemplateMapper smsTemplateMapper;

    @MockBean
    private SmsChannelService smsChannelService;
    @MockBean
    private SmsClientFactory smsClientFactory;
    @MockBean
    private SmsClient smsClient;

    @Test
    public void testParseTemplateContentParams() {
        // 准备参数
        String content = "正在进行登录操作{operation}，您的验证码是{code}";
        // mock 方法

        // 调用
        List<String> params = smsTemplateService.parseTemplateContentParams(content);
        // 断言
        assertEquals(Lists.newArrayList("operation", "code"), params);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateSmsTemplate_success() {
        // 准备参数
        SmsTemplateCreateReqVO reqVO = randomPojo(SmsTemplateCreateReqVO.class, o -> {
            o.setContent("正在进行登录操作{operation}，您的验证码是{code}");
            o.setStatus(randomEle(CommonStatusEnum.values()).getStatus()); // 保证 status 的范围
            o.setType(randomEle(SmsTemplateTypeEnum.values()).getType()); // 保证 type 的 范围
        });
        // mock Channel 的方法
        SmsChannelDO channelDO = randomPojo(SmsChannelDO.class, o -> {
            o.setId(reqVO.getChannelId());
            o.setStatus(CommonStatusEnum.ENABLE.getStatus()); // 保证 status 开启，创建必须处于这个状态
        });
        when(smsChannelService.getSmsChannel(eq(channelDO.getId()))).thenReturn(channelDO);
        // mock 获得 API 短信模板成功
        when(smsClientFactory.getSmsClient(eq(reqVO.getChannelId()))).thenReturn(smsClient);
        when(smsClient.getSmsTemplate(eq(reqVO.getApiTemplateId()))).thenReturn(randomPojo(SmsCommonResult.class, SmsTemplateRespDTO.class,
                o -> o.setCode(GlobalErrorCodeConstants.SUCCESS.getCode())));

        // 调用
        Long smsTemplateId = smsTemplateService.createSmsTemplate(reqVO);
        // 断言
        assertNotNull(smsTemplateId);
        // 校验记录的属性是否正确
        SmsTemplateDO smsTemplate = smsTemplateMapper.selectById(smsTemplateId);
        assertPojoEquals(reqVO, smsTemplate);
        assertEquals(Lists.newArrayList("operation", "code"), smsTemplate.getParams());
        assertEquals(channelDO.getCode(), smsTemplate.getChannelCode());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUpdateSmsTemplate_success() {
        // mock 数据
        SmsTemplateDO dbSmsTemplate = randomSmsTemplateDO();
        smsTemplateMapper.insert(dbSmsTemplate);// @Sql: 先插入出一条存在的数据
        // 准备参数
        SmsTemplateUpdateReqVO reqVO = randomPojo(SmsTemplateUpdateReqVO.class, o -> {
            o.setId(dbSmsTemplate.getId()); // 设置更新的 ID
            o.setContent("正在进行登录操作{operation}，您的验证码是{code}");
            o.setStatus(randomEle(CommonStatusEnum.values()).getStatus()); // 保证 status 的范围
            o.setType(randomEle(SmsTemplateTypeEnum.values()).getType()); // 保证 type 的 范围
        });
        // mock 方法
        SmsChannelDO channelDO = randomPojo(SmsChannelDO.class, o -> {
            o.setId(reqVO.getChannelId());
            o.setStatus(CommonStatusEnum.ENABLE.getStatus()); // 保证 status 开启，创建必须处于这个状态
        });
        when(smsChannelService.getSmsChannel(eq(channelDO.getId()))).thenReturn(channelDO);
        // mock 获得 API 短信模板成功
        when(smsClientFactory.getSmsClient(eq(reqVO.getChannelId()))).thenReturn(smsClient);
        when(smsClient.getSmsTemplate(eq(reqVO.getApiTemplateId()))).thenReturn(randomPojo(SmsCommonResult.class, SmsTemplateRespDTO.class,
                o -> o.setCode(GlobalErrorCodeConstants.SUCCESS.getCode())));

        // 调用
        smsTemplateService.updateSmsTemplate(reqVO);
        // 校验是否更新正确
        SmsTemplateDO smsTemplate = smsTemplateMapper.selectById(reqVO.getId()); // 获取最新的
        assertPojoEquals(reqVO, smsTemplate);
        assertEquals(Lists.newArrayList("operation", "code"), smsTemplate.getParams());
        assertEquals(channelDO.getCode(), smsTemplate.getChannelCode());
    }

    @Test
    public void testUpdateSmsTemplate_notExists() {
        // 准备参数
        SmsTemplateUpdateReqVO reqVO = randomPojo(SmsTemplateUpdateReqVO.class);

        // 调用, 并断言异常
        assertServiceException(() -> smsTemplateService.updateSmsTemplate(reqVO), SMS_TEMPLATE_NOT_EXISTS);
    }

    @Test
    public void testDeleteSmsTemplate_success() {
        // mock 数据
        SmsTemplateDO dbSmsTemplate = randomSmsTemplateDO();
        smsTemplateMapper.insert(dbSmsTemplate);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbSmsTemplate.getId();

        // 调用
        smsTemplateService.deleteSmsTemplate(id);
        // 校验数据不存在了
        assertNull(smsTemplateMapper.selectById(id));
    }

    @Test
    public void testDeleteSmsTemplate_notExists() {
        // 准备参数
        Long id = randomLongId();

        // 调用, 并断言异常
        assertServiceException(() -> smsTemplateService.deleteSmsTemplate(id), SMS_TEMPLATE_NOT_EXISTS);
    }

    @Test
    public void testGetSmsTemplatePage() {
        // mock 数据
        SmsTemplateDO dbSmsTemplate = randomPojo(SmsTemplateDO.class, o -> { // 等会查询到
            o.setType(SmsTemplateTypeEnum.PROMOTION.getType());
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
            o.setCode("tudou");
            o.setContent("芋道源码");
            o.setApiTemplateId("yunai");
            o.setChannelId(1L);
            o.setCreateTime(buildTime(2021, 11, 11));
        });
        smsTemplateMapper.insert(dbSmsTemplate);
        // 测试 type 不匹配
        smsTemplateMapper.insert(ObjectUtils.cloneIgnoreId(dbSmsTemplate, o -> o.setType(SmsTemplateTypeEnum.VERIFICATION_CODE.getType())));
        // 测试 status 不匹配
        smsTemplateMapper.insert(ObjectUtils.cloneIgnoreId(dbSmsTemplate, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));
        // 测试 code 不匹配
        smsTemplateMapper.insert(ObjectUtils.cloneIgnoreId(dbSmsTemplate, o -> o.setCode("yuanma")));
        // 测试 content 不匹配
        smsTemplateMapper.insert(ObjectUtils.cloneIgnoreId(dbSmsTemplate, o -> o.setContent("源码")));
        // 测试 apiTemplateId 不匹配
        smsTemplateMapper.insert(ObjectUtils.cloneIgnoreId(dbSmsTemplate, o -> o.setApiTemplateId("nai")));
        // 测试 channelId 不匹配
        smsTemplateMapper.insert(ObjectUtils.cloneIgnoreId(dbSmsTemplate, o -> o.setChannelId(2L)));
        // 测试 createTime 不匹配
        smsTemplateMapper.insert(ObjectUtils.cloneIgnoreId(dbSmsTemplate, o -> o.setCreateTime(buildTime(2021, 12, 12))));
        // 准备参数
        SmsTemplatePageReqVO reqVO = new SmsTemplatePageReqVO();
        reqVO.setType(SmsTemplateTypeEnum.PROMOTION.getType());
        reqVO.setStatus(CommonStatusEnum.ENABLE.getStatus());
        reqVO.setCode("tu");
        reqVO.setContent("芋道");
        reqVO.setApiTemplateId("yu");
        reqVO.setChannelId(1L);
        reqVO.setCreateTime(buildBetweenTime(2021, 11, 1, 2021, 12, 1));

        // 调用
        PageResult<SmsTemplateDO> pageResult = smsTemplateService.getSmsTemplatePage(reqVO);
        // 断言
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbSmsTemplate, pageResult.getList().get(0));
    }

    @Test
    public void testGetSmsTemplateList() {
        // mock 数据
        SmsTemplateDO dbSmsTemplate = randomPojo(SmsTemplateDO.class, o -> { // 等会查询到
            o.setType(SmsTemplateTypeEnum.PROMOTION.getType());
            o.setStatus(CommonStatusEnum.ENABLE.getStatus());
            o.setCode("tudou");
            o.setContent("芋道源码");
            o.setApiTemplateId("yunai");
            o.setChannelId(1L);
            o.setCreateTime(buildTime(2021, 11, 11));
        });
        smsTemplateMapper.insert(dbSmsTemplate);
        // 测试 type 不匹配
        smsTemplateMapper.insert(ObjectUtils.cloneIgnoreId(dbSmsTemplate, o -> o.setType(SmsTemplateTypeEnum.VERIFICATION_CODE.getType())));
        // 测试 status 不匹配
        smsTemplateMapper.insert(ObjectUtils.cloneIgnoreId(dbSmsTemplate, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));
        // 测试 code 不匹配
        smsTemplateMapper.insert(ObjectUtils.cloneIgnoreId(dbSmsTemplate, o -> o.setCode("yuanma")));
        // 测试 content 不匹配
        smsTemplateMapper.insert(ObjectUtils.cloneIgnoreId(dbSmsTemplate, o -> o.setContent("源码")));
        // 测试 apiTemplateId 不匹配
        smsTemplateMapper.insert(ObjectUtils.cloneIgnoreId(dbSmsTemplate, o -> o.setApiTemplateId("nai")));
        // 测试 channelId 不匹配
        smsTemplateMapper.insert(ObjectUtils.cloneIgnoreId(dbSmsTemplate, o -> o.setChannelId(2L)));
        // 测试 createTime 不匹配
        smsTemplateMapper.insert(ObjectUtils.cloneIgnoreId(dbSmsTemplate, o -> o.setCreateTime(buildTime(2021, 12, 12))));
        // 准备参数
        SmsTemplateExportReqVO reqVO = new SmsTemplateExportReqVO();
        reqVO.setType(SmsTemplateTypeEnum.PROMOTION.getType());
        reqVO.setStatus(CommonStatusEnum.ENABLE.getStatus());
        reqVO.setCode("tu");
        reqVO.setContent("芋道");
        reqVO.setApiTemplateId("yu");
        reqVO.setChannelId(1L);
        reqVO.setCreateTime(buildBetweenTime(2021, 11, 1, 2021, 12, 1));

        // 调用
        List<SmsTemplateDO> list = smsTemplateService.getSmsTemplateList(reqVO);
        // 断言
        assertEquals(1, list.size());
        assertPojoEquals(dbSmsTemplate, list.get(0));
    }

    @Test
    public void testValidateSmsChannel_success() {
        // 准备参数
        Long channelId = randomLongId();
        // mock 方法
        SmsChannelDO channelDO = randomPojo(SmsChannelDO.class, o -> {
            o.setId(channelId);
            o.setStatus(CommonStatusEnum.ENABLE.getStatus()); // 保证 status 开启，创建必须处于这个状态
        });
        when(smsChannelService.getSmsChannel(eq(channelId))).thenReturn(channelDO);

        // 调用
        SmsChannelDO returnChannelDO = smsTemplateService.validateSmsChannel(channelId);
        // 断言
        assertPojoEquals(returnChannelDO, channelDO);
    }

    @Test
    public void testValidateSmsChannel_notExists() {
        // 准备参数
        Long channelId = randomLongId();

        // 调用，校验异常
        assertServiceException(() -> smsTemplateService.validateSmsChannel(channelId),
                SMS_CHANNEL_NOT_EXISTS);
    }

    @Test
    public void testValidateSmsChannel_disable() {
        // 准备参数
        Long channelId = randomLongId();
        // mock 方法
        SmsChannelDO channelDO = randomPojo(SmsChannelDO.class, o -> {
            o.setId(channelId);
            o.setStatus(CommonStatusEnum.DISABLE.getStatus()); // 保证 status 禁用，触发失败
        });
        when(smsChannelService.getSmsChannel(eq(channelId))).thenReturn(channelDO);

        // 调用，校验异常
        assertServiceException(() -> smsTemplateService.validateSmsChannel(channelId),
                SMS_CHANNEL_DISABLE);
    }

    @Test
    public void testValidateDictDataValueUnique_success() {
        // 调用，成功
        smsTemplateService.validateSmsTemplateCodeDuplicate(randomLongId(), randomString());
    }

    @Test
    public void testValidateSmsTemplateCodeDuplicate_valueDuplicateForCreate() {
        // 准备参数
        String code = randomString();
        // mock 数据
        smsTemplateMapper.insert(randomSmsTemplateDO(o -> o.setCode(code)));

        // 调用，校验异常
        assertServiceException(() -> smsTemplateService.validateSmsTemplateCodeDuplicate(null, code),
                SMS_TEMPLATE_CODE_DUPLICATE, code);
    }

    @Test
    public void testValidateDictDataValueUnique_valueDuplicateForUpdate() {
        // 准备参数
        Long id = randomLongId();
        String code = randomString();
        // mock 数据
        smsTemplateMapper.insert(randomSmsTemplateDO(o -> o.setCode(code)));

        // 调用，校验异常
        assertServiceException(() -> smsTemplateService.validateSmsTemplateCodeDuplicate(id, code),
                SMS_TEMPLATE_CODE_DUPLICATE, code);
    }

    // ========== 随机对象 ==========

    @SafeVarargs
    private static SmsTemplateDO randomSmsTemplateDO(Consumer<SmsTemplateDO>... consumers) {
        Consumer<SmsTemplateDO> consumer = (o) -> {
            o.setStatus(randomEle(CommonStatusEnum.values()).getStatus()); // 保证 status 的范围
            o.setType(randomEle(SmsTemplateTypeEnum.values()).getType()); // 保证 type 的 范围
        };
        return randomPojo(SmsTemplateDO.class, ArrayUtils.append(consumer, consumers));
    }

}
