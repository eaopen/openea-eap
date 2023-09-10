package org.openea.eap.module.infra.service.file;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.IdUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openea.eap.framework.common.exception.util.ServiceExceptionUtil;
import org.openea.eap.framework.common.pojo.PageResult;
import org.openea.eap.framework.common.util.collection.CollectionUtils;
import org.openea.eap.framework.common.util.json.JsonUtils;
import org.openea.eap.framework.common.util.validation.ValidationUtils;
import org.openea.eap.framework.file.core.client.FileClient;
import org.openea.eap.framework.file.core.client.FileClientConfig;
import org.openea.eap.framework.file.core.client.FileClientFactory;
import org.openea.eap.framework.file.core.enums.FileStorageEnum;
import org.openea.eap.module.infra.controller.admin.file.vo.config.FileConfigCreateReqVO;
import org.openea.eap.module.infra.controller.admin.file.vo.config.FileConfigPageReqVO;
import org.openea.eap.module.infra.controller.admin.file.vo.config.FileConfigUpdateReqVO;
import org.openea.eap.module.infra.convert.file.FileConfigConvert;
import org.openea.eap.module.infra.dal.dataobject.file.FileConfigDO;
import org.openea.eap.module.infra.dal.mysql.file.FileConfigMapper;
import org.openea.eap.module.infra.enums.ErrorCodeConstants;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 文件配置 Service 实现类
 *
 */
@Service
@Validated
@Slf4j
public class FileConfigServiceImpl implements FileConfigService {

    @Resource
    private FileClientFactory fileClientFactory;

    /**
     * 文件配置的缓存
     */
    @Getter
    private List<FileConfigDO> fileConfigCache;
    /**
     * Master FileClient 对象，有且仅有一个，即 {@link FileConfigDO#getMaster()} 对应的
     */
    @Getter
    private FileClient masterFileClient;

    @Resource
    private FileConfigMapper fileConfigMapper;

    @Resource
    private Validator validator;

    @PostConstruct
    public void initLocalCache() {
        // 第一步：查询数据
        List<FileConfigDO> configs = fileConfigMapper.selectList();
        log.info("[initLocalCache][缓存文件配置，数量为:{}]", configs.size());

        // 第二步：构建缓存：创建或更新文件 Client
        configs.forEach(config -> {
            fileClientFactory.createOrUpdateFileClient(config.getId(), config.getStorage(), config.getConfig());
            // 如果是 master，进行设置
            if (Boolean.TRUE.equals(config.getMaster())) {
                masterFileClient = fileClientFactory.getFileClient(config.getId());
            }
        });
        this.fileConfigCache = configs;
    }

    /**
     * 通过定时任务轮询，刷新缓存
     *
     * 目的：多节点部署时，通过轮询”通知“所有节点，进行刷新
     */
    @Scheduled(initialDelay = 60, fixedRate = 60, timeUnit = TimeUnit.SECONDS)
    public void refreshLocalCache() {
        // 情况一：如果缓存里没有数据，则直接刷新缓存
        if (CollUtil.isEmpty(fileConfigCache)) {
            initLocalCache();
            return;
        }

        // 情况二，如果缓存里数据，则通过 updateTime 判断是否有数据变更，有变更则刷新缓存
        LocalDateTime maxTime = CollectionUtils.getMaxValue(fileConfigCache, FileConfigDO::getUpdateTime);
        if (fileConfigMapper.selectCountByUpdateTimeGt(maxTime) > 0) {
            initLocalCache();
        }
    }

    @Override
    public Long createFileConfig(FileConfigCreateReqVO createReqVO) {
        // 插入
        FileConfigDO fileConfig = FileConfigConvert.INSTANCE.convert(createReqVO)
                .setConfig(parseClientConfig(createReqVO.getStorage(), createReqVO.getConfig()))
                .setMaster(false); // 默认非 master
        fileConfigMapper.insert(fileConfig);

        // 刷新缓存
        initLocalCache();
        return fileConfig.getId();
    }

    @Override
    public void updateFileConfig(FileConfigUpdateReqVO updateReqVO) {
        // 校验存在
        FileConfigDO config = validateFileConfigExists(updateReqVO.getId());
        // 更新
        FileConfigDO updateObj = FileConfigConvert.INSTANCE.convert(updateReqVO)
                .setConfig(parseClientConfig(config.getStorage(), updateReqVO.getConfig()));
        fileConfigMapper.updateById(updateObj);

        // 刷新缓存
        initLocalCache();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFileConfigMaster(Long id) {
        // 校验存在
        validateFileConfigExists(id);
        // 更新其它为非 master
        fileConfigMapper.updateBatch(new FileConfigDO().setMaster(false));
        // 更新
        fileConfigMapper.updateById(new FileConfigDO().setId(id).setMaster(true));

        // 刷新缓存
        initLocalCache();
    }

    private FileClientConfig parseClientConfig(Integer storage, Map<String, Object> config) {
        // 获取配置类
        Class<? extends FileClientConfig> configClass = FileStorageEnum.getByStorage(storage)
                .getConfigClass();
        FileClientConfig clientConfig = JsonUtils.parseObject2(JsonUtils.toJsonString(config), configClass);
        // 参数校验
        ValidationUtils.validate(validator, clientConfig);
        // 设置参数
        return clientConfig;
    }

    @Override
    public void deleteFileConfig(Long id) {
        // 校验存在
        FileConfigDO config = validateFileConfigExists(id);
        if (Boolean.TRUE.equals(config.getMaster())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.FILE_CONFIG_DELETE_FAIL_MASTER);
        }
        // 删除
        fileConfigMapper.deleteById(id);

        // 刷新缓存
        initLocalCache();
    }

    private FileConfigDO validateFileConfigExists(Long id) {
        FileConfigDO config = fileConfigMapper.selectById(id);
        if (config == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.FILE_CONFIG_NOT_EXISTS);
        }
        return config;
    }

    @Override
    public FileConfigDO getFileConfig(Long id) {
        return fileConfigMapper.selectById(id);
    }

    @Override
    public PageResult<FileConfigDO> getFileConfigPage(FileConfigPageReqVO pageReqVO) {
        return fileConfigMapper.selectPage(pageReqVO);
    }

    @Override
    public String testFileConfig(Long id) throws Exception {
        // 校验存在
        validateFileConfigExists(id);
        // 上传文件
        byte[] content = ResourceUtil.readBytes("file/erweima.jpg");
        return fileClientFactory.getFileClient(id).upload(content, IdUtil.fastSimpleUUID() + ".jpg", "image/jpeg");
    }

    @Override
    public FileClient getFileClient(Long id) {
        return fileClientFactory.getFileClient(id);
    }

}
