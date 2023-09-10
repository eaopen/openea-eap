package org.openea.eap.module.infra.service.file;

import cn.hutool.core.io.resource.ResourceUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.openea.eap.framework.common.pojo.PageResult;
import org.openea.eap.framework.common.util.object.ObjectUtils;
import org.openea.eap.framework.file.core.client.FileClient;
import org.openea.eap.framework.test.core.ut.BaseDbUnitTest;
import org.openea.eap.framework.test.core.util.AssertUtils;
import org.openea.eap.framework.test.core.util.RandomUtils;
import org.openea.eap.module.infra.controller.admin.file.vo.file.FilePageReqVO;
import org.openea.eap.module.infra.dal.dataobject.file.FileDO;
import org.openea.eap.module.infra.dal.mysql.file.FileMapper;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.openea.eap.framework.common.util.date.LocalDateTimeUtils.buildTime;
import static org.openea.eap.module.infra.enums.ErrorCodeConstants.FILE_NOT_EXISTS;

@Import({FileServiceImpl.class})
public class FileServiceImplTest extends BaseDbUnitTest {

    @Resource
    private FileService fileService;

    @Resource
    private FileMapper fileMapper;

    @MockBean
    private FileConfigService fileConfigService;

    @Test
    public void testGetFilePage() {
        // mock 数据
        FileDO dbFile = RandomUtils.randomPojo(FileDO.class, o -> { // 等会查询到
            o.setPath("yunai");
            o.setType("image/jpg");
            o.setCreateTime(buildTime(2021, 1, 15));
        });
        fileMapper.insert(dbFile);
        // 测试 path 不匹配
        fileMapper.insert(ObjectUtils.cloneIgnoreId(dbFile, o -> o.setPath("tudou")));
        // 测试 type 不匹配
        fileMapper.insert(ObjectUtils.cloneIgnoreId(dbFile, o -> {
            o.setType("image/png");
        }));
        // 测试 createTime 不匹配
        fileMapper.insert(ObjectUtils.cloneIgnoreId(dbFile, o -> {
            o.setCreateTime(buildTime(2020, 1, 15));
        }));
        // 准备参数
        FilePageReqVO reqVO = new FilePageReqVO();
        reqVO.setPath("yunai");
        reqVO.setType("jp");
        reqVO.setCreateTime((new LocalDateTime[]{buildTime(2021, 1, 10), buildTime(2021, 1, 20)}));

        // 调用
        PageResult<FileDO> pageResult = fileService.getFilePage(reqVO);
        // 断言
        Assertions.assertEquals(1, pageResult.getTotal());
        Assertions.assertEquals(1, pageResult.getList().size());
        AssertUtils.assertPojoEquals(dbFile, pageResult.getList().get(0));
    }

    @Test
    public void testCreateFile_success() throws Exception {
        // 准备参数
        String path = RandomUtils.randomString();
        byte[] content = ResourceUtil.readBytes("file/erweima.jpg");
        // mock Master 文件客户端
        FileClient client = Mockito.mock(FileClient.class);
        Mockito.when(fileConfigService.getMasterFileClient()).thenReturn(client);
        String url = RandomUtils.randomString();
        Mockito.when(client.upload(ArgumentMatchers.same(content), ArgumentMatchers.same(path), ArgumentMatchers.eq("image/jpeg"))).thenReturn(url);
        Mockito.when(client.getId()).thenReturn(10L);
        String name = "单测文件名";
        // 调用
        String result = fileService.createFile(name, path, content);
        // 断言
        Assertions.assertEquals(result, url);
        // 校验数据
        FileDO file = fileMapper.selectOne(FileDO::getPath, path);
        assertEquals(10L, file.getConfigId());
        assertEquals(path, file.getPath());
        assertEquals(url, file.getUrl());
        assertEquals("image/jpeg", file.getType());
        assertEquals(content.length, file.getSize());
    }

    @Test
    public void testDeleteFile_success() throws Exception {
        // mock 数据
        FileDO dbFile = RandomUtils.randomPojo(FileDO.class, o -> o.setConfigId(10L).setPath("tudou.jpg"));
        fileMapper.insert(dbFile);// @Sql: 先插入出一条存在的数据
        // mock Master 文件客户端
        FileClient client = Mockito.mock(FileClient.class);
        Mockito.when(fileConfigService.getFileClient(ArgumentMatchers.eq(10L))).thenReturn(client);
        // 准备参数
        Long id = dbFile.getId();

        // 调用
        fileService.deleteFile(id);
        // 校验数据不存在了
        Assertions.assertNull(fileMapper.selectById(id));
        // 校验调用
        Mockito.verify(client).delete(ArgumentMatchers.eq("tudou.jpg"));
    }

    @Test
    public void testDeleteFile_notExists() {
        // 准备参数
        Long id = RandomUtils.randomLongId();

        // 调用, 并断言异常
        AssertUtils.assertServiceException(() -> fileService.deleteFile(id), FILE_NOT_EXISTS);
    }

    @Test
    public void testGetFileContent() throws Exception {
        // 准备参数
        Long configId = 10L;
        String path = "tudou.jpg";
        // mock 方法
        FileClient client = Mockito.mock(FileClient.class);
        Mockito.when(fileConfigService.getFileClient(ArgumentMatchers.eq(10L))).thenReturn(client);
        byte[] content = new byte[]{};
        Mockito.when(client.getContent(ArgumentMatchers.eq("tudou.jpg"))).thenReturn(content);

        // 调用
        byte[] result = fileService.getFileContent(configId, path);
        // 断言
        Assertions.assertSame(result, content);
    }
}
