package org.openea.eap.module.system.service.sensitiveword;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openea.eap.framework.common.enums.CommonStatusEnum;
import org.openea.eap.framework.common.pojo.PageResult;
import org.openea.eap.framework.common.util.collection.CollectionUtils;
import org.openea.eap.module.system.controller.admin.sensitiveword.vo.SensitiveWordCreateReqVO;
import org.openea.eap.module.system.controller.admin.sensitiveword.vo.SensitiveWordExportReqVO;
import org.openea.eap.module.system.controller.admin.sensitiveword.vo.SensitiveWordPageReqVO;
import org.openea.eap.module.system.controller.admin.sensitiveword.vo.SensitiveWordUpdateReqVO;
import org.openea.eap.module.system.convert.sensitiveword.SensitiveWordConvert;
import org.openea.eap.module.system.dal.dataobject.sensitiveword.SensitiveWordDO;
import org.openea.eap.module.system.dal.mysql.sensitiveword.SensitiveWordMapper;
import org.openea.eap.module.system.util.collection.SimpleTrie;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

import static org.openea.eap.framework.common.exception.util.ServiceExceptionUtil.exception;
import static org.openea.eap.framework.common.util.collection.CollectionUtils.filterList;
import static org.openea.eap.framework.common.util.collection.CollectionUtils.getMaxValue;
import static org.openea.eap.module.system.enums.ErrorCodeConstants.SENSITIVE_WORD_EXISTS;
import static org.openea.eap.module.system.enums.ErrorCodeConstants.SENSITIVE_WORD_NOT_EXISTS;

/**
 * 敏感词 Service 实现类
 *
 */
@Service
@Slf4j
@Validated
public class SensitiveWordServiceImpl implements SensitiveWordService {

    /**
     * 敏感词列表缓存
     */
    @Getter
    private volatile List<SensitiveWordDO> sensitiveWordCache = Collections.emptyList();
    /**
     * 敏感词标签缓存
     * key：敏感词编号 {@link SensitiveWordDO#getId()}
     * <p>
     * 这里声明 volatile 修饰的原因是，每次刷新时，直接修改指向
     */
    @Getter
    private volatile Set<String> sensitiveWordTagsCache = Collections.emptySet();

    @Resource
    private SensitiveWordMapper sensitiveWordMapper;

    /**
     * 默认的敏感词的字典树，包含所有敏感词
     */
    @Getter
    private volatile SimpleTrie defaultSensitiveWordTrie = new SimpleTrie(Collections.emptySet());
    /**
     * 标签与敏感词的字段数的映射
     */
    @Getter
    private volatile Map<String, SimpleTrie> tagSensitiveWordTries = Collections.emptyMap();

    /**
     * 初始化缓存
     */
    @PostConstruct
    public void initLocalCache() {
        // 第一步：查询数据
        List<SensitiveWordDO> sensitiveWords = sensitiveWordMapper.selectList();
        log.info("[initLocalCache][缓存敏感词，数量为:{}]", sensitiveWords.size());

        // 第二步：构建缓存
        // 写入 sensitiveWordTagsCache 缓存
        Set<String> tags = new HashSet<>();
        sensitiveWords.forEach(word -> tags.addAll(word.getTags()));
        sensitiveWordTagsCache = tags;
        sensitiveWordCache = sensitiveWords;
        // 写入 defaultSensitiveWordTrie、tagSensitiveWordTries 缓存
        initSensitiveWordTrie(sensitiveWords);
    }

    private void initSensitiveWordTrie(List<SensitiveWordDO> wordDOs) {
        // 过滤禁用的敏感词
        wordDOs = filterList(wordDOs, word -> word.getStatus().equals(CommonStatusEnum.ENABLE.getStatus()));

        // 初始化默认的 defaultSensitiveWordTrie
        this.defaultSensitiveWordTrie = new SimpleTrie(CollectionUtils.convertList(wordDOs, SensitiveWordDO::getName));

        // 初始化 tagSensitiveWordTries
        Multimap<String, String> tagWords = HashMultimap.create();
        for (SensitiveWordDO word : wordDOs) {
            if (CollUtil.isEmpty(word.getTags())) {
                continue;
            }
            word.getTags().forEach(tag -> tagWords.put(tag, word.getName()));
        }
        // 添加到 tagSensitiveWordTries 中
        Map<String, SimpleTrie> tagSensitiveWordTries = new HashMap<>();
        tagWords.asMap().forEach((tag, words) -> tagSensitiveWordTries.put(tag, new SimpleTrie(words)));
        this.tagSensitiveWordTries = tagSensitiveWordTries;
    }

    /**
     * 通过定时任务轮询，刷新缓存
     *
     * 目的：多节点部署时，通过轮询”通知“所有节点，进行刷新
     */
    //@Scheduled(initialDelay = 60, fixedRate = 60, timeUnit = TimeUnit.SECONDS)
    public void refreshLocalCache() {
        // 情况一：如果缓存里没有数据，则直接刷新缓存
        if (CollUtil.isEmpty(sensitiveWordCache)) {
            initLocalCache();
            return;
        }

        // 情况二，如果缓存里数据，则通过 updateTime 判断是否有数据变更，有变更则刷新缓存
        LocalDateTime maxTime = getMaxValue(sensitiveWordCache, SensitiveWordDO::getUpdateTime);
        if (sensitiveWordMapper.selectCountByUpdateTimeGt(maxTime) > 0) {
            initLocalCache();
        }
    }

    @Override
    public Long createSensitiveWord(SensitiveWordCreateReqVO createReqVO) {
        // 校验唯一性
        validateSensitiveWordNameUnique(null, createReqVO.getName());

        // 插入
        SensitiveWordDO sensitiveWord = SensitiveWordConvert.INSTANCE.convert(createReqVO);
        sensitiveWordMapper.insert(sensitiveWord);

        // 刷新缓存
        initLocalCache();
        return sensitiveWord.getId();
    }

    @Override
    public void updateSensitiveWord(SensitiveWordUpdateReqVO updateReqVO) {
        // 校验唯一性
        validateSensitiveWordExists(updateReqVO.getId());
        validateSensitiveWordNameUnique(updateReqVO.getId(), updateReqVO.getName());

        // 更新
        SensitiveWordDO updateObj = SensitiveWordConvert.INSTANCE.convert(updateReqVO);
        sensitiveWordMapper.updateById(updateObj);

        // 刷新缓存
        initLocalCache();
    }

    @Override
    public void deleteSensitiveWord(Long id) {
        // 校验存在
        validateSensitiveWordExists(id);
        // 删除
        sensitiveWordMapper.deleteById(id);

        // 刷新缓存
        initLocalCache();
    }

    private void validateSensitiveWordNameUnique(Long id, String name) {
        SensitiveWordDO word = sensitiveWordMapper.selectByName(name);
        if (word == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的敏感词
        if (id == null) {
            throw exception(SENSITIVE_WORD_EXISTS);
        }
        if (!word.getId().equals(id)) {
            throw exception(SENSITIVE_WORD_EXISTS);
        }
    }

    private void validateSensitiveWordExists(Long id) {
        if (sensitiveWordMapper.selectById(id) == null) {
            throw exception(SENSITIVE_WORD_NOT_EXISTS);
        }
    }

    @Override
    public SensitiveWordDO getSensitiveWord(Long id) {
        return sensitiveWordMapper.selectById(id);
    }

    @Override
    public List<SensitiveWordDO> getSensitiveWordList() {
        return sensitiveWordMapper.selectList();
    }

    @Override
    public PageResult<SensitiveWordDO> getSensitiveWordPage(SensitiveWordPageReqVO pageReqVO) {
        return sensitiveWordMapper.selectPage(pageReqVO);
    }

    @Override
    public List<SensitiveWordDO> getSensitiveWordList(SensitiveWordExportReqVO exportReqVO) {
        return sensitiveWordMapper.selectList(exportReqVO);
    }

    @Override
    public Set<String> getSensitiveWordTagSet() {
        return sensitiveWordTagsCache;
    }

    @Override
    public List<String> validateText(String text, List<String> tags) {
        if (CollUtil.isEmpty(tags)) {
            return defaultSensitiveWordTrie.validate(text);
        }
        // 有标签的情况
        Set<String> result = new HashSet<>();
        tags.forEach(tag -> {
            SimpleTrie trie = tagSensitiveWordTries.get(tag);
            if (trie == null) {
                return;
            }
            result.addAll(trie.validate(text));
        });
        return new ArrayList<>(result);
    }

    @Override
    public boolean isTextValid(String text, List<String> tags) {
        if (CollUtil.isEmpty(tags)) {
            return defaultSensitiveWordTrie.isValid(text);
        }
        // 有标签的情况
        for (String tag : tags) {
            SimpleTrie trie = tagSensitiveWordTries.get(tag);
            if (trie == null) {
                continue;
            }
            if (!trie.isValid(text)) {
                return false;
            }
        }
        return true;
    }

}
