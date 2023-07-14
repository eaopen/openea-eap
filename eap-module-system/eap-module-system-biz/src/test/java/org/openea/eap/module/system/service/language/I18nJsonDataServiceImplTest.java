package org.openea.eap.module.system.service.language;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.annotation.Resource;

import org.openea.eap.framework.test.core.ut.BaseDbUnitTest;

import org.openea.eap.module.system.controller.admin.language.vo.*;
import org.openea.eap.module.system.dal.dataobject.language.I18nJsonDataDO;
import org.openea.eap.module.system.dal.mysql.language.I18nJsonDataMapper;
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
 * {@link I18nJsonDataServiceImpl} 的单元测试类
 *
 * @author eap
 */
@Import(I18nJsonDataServiceImpl.class)
public class I18nJsonDataServiceImplTest extends BaseDbUnitTest {

    @Resource
    private I18nJsonDataServiceImpl i18nJsonDataService;

    @Resource
    private I18nJsonDataMapper i18nJsonDataMapper;

    @Test
    public void testCreateI18nJsonData_success() {
        // 准备参数
        I18nJsonDataCreateReqVO reqVO = randomPojo(I18nJsonDataCreateReqVO.class);

        // 调用
        Long i18nJsonDataId = i18nJsonDataService.createI18nJsonData(reqVO);
        // 断言
        assertNotNull(i18nJsonDataId);
        // 校验记录的属性是否正确
        I18nJsonDataDO i18nJsonData = i18nJsonDataMapper.selectById(i18nJsonDataId);
        assertPojoEquals(reqVO, i18nJsonData);
    }

    @Test
    public void testUpdateI18nJsonData_success() {
        // mock 数据
        I18nJsonDataDO dbI18nJsonData = randomPojo(I18nJsonDataDO.class);
        i18nJsonDataMapper.insert(dbI18nJsonData);// @Sql: 先插入出一条存在的数据
        // 准备参数
        I18nJsonDataUpdateReqVO reqVO = randomPojo(I18nJsonDataUpdateReqVO.class, o -> {
            o.setId(dbI18nJsonData.getId()); // 设置更新的 ID
        });

        // 调用
        i18nJsonDataService.updateI18nJsonData(reqVO);
        // 校验是否更新正确
        I18nJsonDataDO i18nJsonData = i18nJsonDataMapper.selectById(reqVO.getId()); // 获取最新的
        assertPojoEquals(reqVO, i18nJsonData);
    }

    @Test
    public void testUpdateI18nJsonData_notExists() {
        // 准备参数
        I18nJsonDataUpdateReqVO reqVO = randomPojo(I18nJsonDataUpdateReqVO.class);

        // 调用, 并断言异常
        assertServiceException(() -> i18nJsonDataService.updateI18nJsonData(reqVO), I18N_JSON_DATA_NOT_EXISTS);
    }

    @Test
    public void testDeleteI18nJsonData_success() {
        // mock 数据
        I18nJsonDataDO dbI18nJsonData = randomPojo(I18nJsonDataDO.class);
        i18nJsonDataMapper.insert(dbI18nJsonData);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbI18nJsonData.getId();

        // 调用
        i18nJsonDataService.deleteI18nJsonData(id);
       // 校验数据不存在了
       assertNull(i18nJsonDataMapper.selectById(id));
    }

    @Test
    public void testDeleteI18nJsonData_notExists() {
        // 准备参数
        Long id = randomLongId();

        // 调用, 并断言异常
        assertServiceException(() -> i18nJsonDataService.deleteI18nJsonData(id), I18N_JSON_DATA_NOT_EXISTS);
    }

    @Test
    @Disabled  // TODO 请修改 null 为需要的值，然后删除 @Disabled 注解
    public void testGetI18nJsonDataPage() {
       // mock 数据
       I18nJsonDataDO dbI18nJsonData = randomPojo(I18nJsonDataDO.class, o -> { // 等会查询到
           o.setModule(null);
           o.setAlias(null);
           o.setName(null);
           o.setJson(null);
           o.setCreateTime(null);
       });
       i18nJsonDataMapper.insert(dbI18nJsonData);
       // 测试 module 不匹配
       i18nJsonDataMapper.insert(cloneIgnoreId(dbI18nJsonData, o -> o.setModule(null)));
       // 测试 alias 不匹配
       i18nJsonDataMapper.insert(cloneIgnoreId(dbI18nJsonData, o -> o.setAlias(null)));
       // 测试 name 不匹配
       i18nJsonDataMapper.insert(cloneIgnoreId(dbI18nJsonData, o -> o.setName(null)));
       // 测试 json 不匹配
       i18nJsonDataMapper.insert(cloneIgnoreId(dbI18nJsonData, o -> o.setJson(null)));
       // 测试 createTime 不匹配
       i18nJsonDataMapper.insert(cloneIgnoreId(dbI18nJsonData, o -> o.setCreateTime(null)));
       // 准备参数
       I18nJsonDataPageReqVO reqVO = new I18nJsonDataPageReqVO();
       reqVO.setModule(null);
       reqVO.setAlias(null);
       reqVO.setName(null);
       reqVO.setJson(null);
       reqVO.setCreateTime(buildBetweenTime(2023, 2, 1, 2023, 2, 28));

       // 调用
       PageResult<I18nJsonDataDO> pageResult = i18nJsonDataService.getI18nJsonDataPage(reqVO);
       // 断言
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(dbI18nJsonData, pageResult.getList().get(0));
    }

    @Test
    @Disabled  // TODO 请修改 null 为需要的值，然后删除 @Disabled 注解
    public void testGetI18nJsonDataList() {
       // mock 数据
       I18nJsonDataDO dbI18nJsonData = randomPojo(I18nJsonDataDO.class, o -> { // 等会查询到
           o.setModule(null);
           o.setAlias(null);
           o.setName(null);
           o.setJson(null);
           o.setCreateTime(null);
       });
       i18nJsonDataMapper.insert(dbI18nJsonData);
       // 测试 module 不匹配
       i18nJsonDataMapper.insert(cloneIgnoreId(dbI18nJsonData, o -> o.setModule(null)));
       // 测试 alias 不匹配
       i18nJsonDataMapper.insert(cloneIgnoreId(dbI18nJsonData, o -> o.setAlias(null)));
       // 测试 name 不匹配
       i18nJsonDataMapper.insert(cloneIgnoreId(dbI18nJsonData, o -> o.setName(null)));
       // 测试 json 不匹配
       i18nJsonDataMapper.insert(cloneIgnoreId(dbI18nJsonData, o -> o.setJson(null)));
       // 测试 createTime 不匹配
       i18nJsonDataMapper.insert(cloneIgnoreId(dbI18nJsonData, o -> o.setCreateTime(null)));
       // 准备参数
       I18nJsonDataExportReqVO reqVO = new I18nJsonDataExportReqVO();
       reqVO.setModule(null);
       reqVO.setAlias(null);
       reqVO.setName(null);
       reqVO.setJson(null);
       reqVO.setCreateTime(buildBetweenTime(2023, 2, 1, 2023, 2, 28));

       // 调用
       List<I18nJsonDataDO> list = i18nJsonDataService.getI18nJsonDataList(reqVO);
       // 断言
       assertEquals(1, list.size());
       assertPojoEquals(dbI18nJsonData, list.get(0));
    }

}
