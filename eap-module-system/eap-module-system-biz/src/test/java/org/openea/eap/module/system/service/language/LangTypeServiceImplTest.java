package org.openea.eap.module.system.service.language;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.annotation.Resource;

import org.openea.eap.framework.test.core.ut.BaseDbUnitTest;

import org.openea.eap.module.system.controller.admin.language.vo.*;
import org.openea.eap.module.system.dal.dataobject.language.LangTypeDO;
import org.openea.eap.module.system.dal.mysql.language.LangTypeMapper;
import org.openea.eap.framework.common.pojo.PageResult;

import javax.annotation.Resource;
import org.springframework.context.annotation.Import;
import java.util.*;
import java.time.LocalDateTime;

import static cn.hutool.core.util.RandomUtil.*;
import static org.openea.eap.module.system.enums.ErrorCodeConstants.*;
import static org.openea.eap.framework.test.core.util.AssertUtils.*;
import static org.openea.eap.framework.test.core.util.RandomUtils.*;
import static org.openea.eap.framework.common.util.date.LocalDateTimeUtils.*;
import static org.openea.eap.framework.common.util.object.ObjectUtils.*;
import static org.openea.eap.framework.common.util.date.DateUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * {@link LangTypeServiceImpl} 的单元测试类
 *
 * @author eap
 */
@Import(LangTypeServiceImpl.class)
public class LangTypeServiceImplTest extends BaseDbUnitTest {

    @Resource
    private LangTypeServiceImpl langTypeService;

    @Resource
    private LangTypeMapper langTypeMapper;

    @Test
    public void testCreateLangType_success() {
        // 准备参数
        LangTypeCreateReqVO reqVO = randomPojo(LangTypeCreateReqVO.class);

        // 调用
        Long langTypeId = langTypeService.createLangType(reqVO);
        // 断言
        assertNotNull(langTypeId);
        // 校验记录的属性是否正确
        LangTypeDO langType = langTypeMapper.selectById(langTypeId);
        assertPojoEquals(reqVO, langType);
    }

    @Test
    public void testUpdateLangType_success() {
        // mock 数据
        LangTypeDO dbLangType = randomPojo(LangTypeDO.class);
        langTypeMapper.insert(dbLangType);// @Sql: 先插入出一条存在的数据
        // 准备参数
        LangTypeUpdateReqVO reqVO = randomPojo(LangTypeUpdateReqVO.class, o -> {
            o.setId(dbLangType.getId()); // 设置更新的 ID
        });

        // 调用
        langTypeService.updateLangType(reqVO);
        // 校验是否更新正确
        LangTypeDO langType = langTypeMapper.selectById(reqVO.getId()); // 获取最新的
        assertPojoEquals(reqVO, langType);
    }

    @Test
    public void testUpdateLangType_notExists() {
        // 准备参数
        LangTypeUpdateReqVO reqVO = randomPojo(LangTypeUpdateReqVO.class);

        // 调用, 并断言异常
        assertServiceException(() -> langTypeService.updateLangType(reqVO), LANG_TYPE_NOT_EXISTS);
    }

    @Test
    public void testDeleteLangType_success() {
        // mock 数据
        LangTypeDO dbLangType = randomPojo(LangTypeDO.class);
        langTypeMapper.insert(dbLangType);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbLangType.getId();

        // 调用
        langTypeService.deleteLangType(id);
       // 校验数据不存在了
       assertNull(langTypeMapper.selectById(id));
    }

    @Test
    public void testDeleteLangType_notExists() {
        // 准备参数
        Long id = randomLongId();

        // 调用, 并断言异常
        assertServiceException(() -> langTypeService.deleteLangType(id), LANG_TYPE_NOT_EXISTS);
    }

    @Test
    @Disabled  // TODO 请修改 null 为需要的值，然后删除 @Disabled 注解
    public void testGetLangTypePage() {
       // mock 数据
       LangTypeDO dbLangType = randomPojo(LangTypeDO.class, o -> { // 等会查询到
           o.setName(null);
           o.setCreateTime(null);
           o.setAlias(null);
       });
       langTypeMapper.insert(dbLangType);
       // 测试 name 不匹配
       langTypeMapper.insert(cloneIgnoreId(dbLangType, o -> o.setName(null)));
       // 测试 createTime 不匹配
       langTypeMapper.insert(cloneIgnoreId(dbLangType, o -> o.setCreateTime(null)));
       // 测试 alias 不匹配
       langTypeMapper.insert(cloneIgnoreId(dbLangType, o -> o.setAlias(null)));
       // 准备参数
       LangTypePageReqVO reqVO = new LangTypePageReqVO();
       reqVO.setName(null);
       reqVO.setCreateTime(buildBetweenTime(2023, 2, 1, 2023, 2, 28));
       reqVO.setAlias(null);

       // 调用
       PageResult<LangTypeDO> pageResult = langTypeService.getLangTypePage(reqVO);
       // 断言
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(dbLangType, pageResult.getList().get(0));
    }

    @Test
    @Disabled  // TODO 请修改 null 为需要的值，然后删除 @Disabled 注解
    public void testGetLangTypeList() {
       // mock 数据
       LangTypeDO dbLangType = randomPojo(LangTypeDO.class, o -> { // 等会查询到
           o.setName(null);
           o.setCreateTime(null);
           o.setAlias(null);
       });
       langTypeMapper.insert(dbLangType);
       // 测试 name 不匹配
       langTypeMapper.insert(cloneIgnoreId(dbLangType, o -> o.setName(null)));
       // 测试 createTime 不匹配
       langTypeMapper.insert(cloneIgnoreId(dbLangType, o -> o.setCreateTime(null)));
       // 测试 alias 不匹配
       langTypeMapper.insert(cloneIgnoreId(dbLangType, o -> o.setAlias(null)));
       // 准备参数
       LangTypeExportReqVO reqVO = new LangTypeExportReqVO();
       reqVO.setName(null);
       reqVO.setCreateTime(buildBetweenTime(2023, 2, 1, 2023, 2, 28));
       reqVO.setAlias(null);

       // 调用
       List<LangTypeDO> list = langTypeService.getLangTypeList(reqVO);
       // 断言
       assertEquals(1, list.size());
       assertPojoEquals(dbLangType, list.get(0));
    }

}
