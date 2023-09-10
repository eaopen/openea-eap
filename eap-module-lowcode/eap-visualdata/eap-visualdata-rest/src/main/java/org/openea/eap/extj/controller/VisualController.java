package org.openea.eap.extj.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.google.common.base.Joiner;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.FileInfo;
import org.openea.eap.extj.base.NoDataSourceBind;
import org.openea.eap.extj.base.controller.SuperController;
import org.openea.eap.extj.base.vo.DownloadVO;
import org.openea.eap.extj.base.vo.ListVO;
import org.openea.eap.extj.config.ConfigValueUtil;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.entity.VisualCategoryEntity;
import org.openea.eap.extj.entity.VisualConfigEntity;
import org.openea.eap.extj.entity.VisualEntity;
import org.openea.eap.extj.enums.VisualImgEnum;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.model.FileListVO;
import org.openea.eap.extj.model.VisualPageVO;
import org.openea.eap.extj.model.visual.*;
import org.openea.eap.extj.model.visualcategory.VisualCategoryListVO;
import org.openea.eap.extj.model.visualconfig.VisualConfigInfoModel;
import org.openea.eap.extj.model.visualfile.ImageVO;
import org.openea.eap.extj.service.VisualCategoryService;
import org.openea.eap.extj.service.VisualConfigService;
import org.openea.eap.extj.service.VisualService;
import org.openea.eap.extj.util.*;
import org.openea.eap.extj.util.enums.ModuleTypeEnum;
import org.openea.eap.extj.util.file.FileExport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 大屏基本信息
 *
 *
 */
@RestController
@Tag(name = "大屏基本信息" , description = "visual")
@RequestMapping("/api/blade-visual/visual")
public class VisualController extends SuperController<VisualService, VisualEntity> {

    @Autowired
    private FileExport fileExport;
    @Autowired
    private VisualService visualService;
    @Autowired
    private VisualConfigService configService;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private VisualCategoryService categoryService;

    /**
     * 列表
     *
     * @param pagination 分页模型
     * @return
     */
    @Operation(summary = "分页")
    @GetMapping("/list")
    @SaCheckPermission("onlineDev.dataScreen")
    public ActionResult<VisualPageVO<VisualListVO>> list(VisualPaginationModel pagination) {
        List<VisualEntity> data = visualService.getList(pagination);
        List<VisualListVO> list = JsonUtil.getJsonToList(data, VisualListVO.class);
        VisualPageVO paginationVO = JsonUtil.getJsonToBean(pagination, VisualPageVO.class);
        paginationVO.setRecords(list);
        return ActionResult.success(paginationVO);
    }

    /**
     * 详情
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "详情")
    @GetMapping("/detail")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ActionResult<VisualInfoVO> info(@RequestParam("id")String id) {
        VisualEntity visual = visualService.getInfo(id);
        VisualConfigEntity config = configService.getInfo(id);
        VisualInfoVO vo = new VisualInfoVO();
        vo.setVisual(JsonUtil.getJsonToBean(visual, VisualInfoModel.class));
        vo.setConfig(JsonUtil.getJsonToBean(config, VisualConfigInfoModel.class));
        return ActionResult.success(vo);
    }

    /**
     * 新增
     *
     * @param visualCrform 大屏模型
     * @return
     */
    @Operation(summary = "新增")
    @PostMapping("/save")
    @Parameters({
            @Parameter(name = "visualCrform", description = "大屏模型",required = true),
    })
    @SaCheckPermission("onlineDev.dataScreen")
    public ActionResult create(@RequestBody @Valid VisualCrForm visualCrform) {
        VisualEntity visual = JsonUtil.getJsonToBean(visualCrform.getVisual(), VisualEntity.class);
        visual.setBackgroundUrl(VisusalImgUrl.url + configValueUtil.getBiVisualPath() + "bg/bg1.png");
        VisualConfigEntity config = JsonUtil.getJsonToBean(visualCrform.getConfig(), VisualConfigEntity.class);
        visualService.create(visual, config);
        Map<String, String> data = new HashMap<>(16);
        data.put("id" , visual.getId());
        return ActionResult.success(data);
    }

    /**
     * 修改
     *
     * @param categoryUpForm 大屏模型
     * @return
     */
    @Operation(summary = "修改")
    @PostMapping("/update")
    @Parameters({
            @Parameter(name = "categoryUpForm", description = "大屏模型",required = true),
    })
    @SaCheckPermission("onlineDev.dataScreen")
    public ActionResult update(@RequestBody VisualUpForm categoryUpForm) {
        VisualEntity visual = JsonUtil.getJsonToBean(categoryUpForm.getVisual(), VisualEntity.class);
        VisualConfigEntity config = JsonUtil.getJsonToBean(categoryUpForm.getConfig(), VisualConfigEntity.class);
        visualService.update(visual.getId(), visual, config);
        return ActionResult.success("更新成功");
    }

    /**
     * 删除
     *
     * @param ids 主键
     * @return
     */
    @Operation(summary = "删除")
    @PostMapping("/remove")
    @Parameters({
            @Parameter(name = "ids", description = "主键", required = true),
    })
    @SaCheckPermission("onlineDev.dataScreen")
    public ActionResult delete(@RequestParam("ids")String ids) {
        VisualEntity entity = visualService.getInfo(ids);
        if (entity != null) {
            visualService.delete(entity);
            return ActionResult.success("删除成功");
        }
        return ActionResult.fail("删除失败，数据不存在");
    }

    /**
     * 复制
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "复制")
    @PostMapping("/copy")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    @SaCheckPermission("onlineDev.dataScreen")
    public ActionResult copy(@RequestParam("id")String id) {
        VisualEntity entity = visualService.getInfo(id);
        VisualConfigEntity config = configService.getInfo(id);
        if (entity != null) {
            entity.setTitle(entity.getTitle() + "_复制");
            visualService.create(entity, config);
            return ActionResult.success(entity.getId());
        }
        return ActionResult.fail("复制失败");
    }

    /**
     * 获取类型
     *
     * @return
     */
    @Operation(summary = "获取类型")
    @GetMapping("/category")
    @SaCheckPermission("onlineDev.dataScreen")
    public ActionResult<List<VisualCategoryListVO>> list() {
        List<VisualCategoryEntity> data = categoryService.getList();
        List<VisualCategoryListVO> list = JsonUtil.getJsonToList(data, VisualCategoryListVO.class);
        return ActionResult.success(list);
    }

    /**
     * 上传文件
     *
     * @param file 文件
     * @param type 类型
     * @return
     */
    @NoDataSourceBind()
    @Operation(summary = "上传文件")
    @Parameters({
            @Parameter(name = "type", description = "类型",required = true),
    })
    @PostMapping(value = "/put-file/{type}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ActionResult<ImageVO> file(MultipartFile file, @PathVariable("type") String type) {
        ImageVO vo = new ImageVO();
        VisualImgEnum imgEnum = VisualImgEnum.getByMessage(type);
        if (imgEnum != null) {
            String path = imgEnum.getMessage();
            String filePath = configValueUtil.getBiVisualPath() + path + "/";
            String name = RandomUtil.uuId() + "." + UpUtil.getFileType(file);
            //上传文件
            FileInfo fileInfo = FileUploadUtils.uploadFile(file, filePath, name);
            vo.setOriginalName(fileInfo.getOriginalFilename());
            vo.setLink(VisusalImgUrl.url + fileInfo.getPath() + fileInfo.getFilename());
            vo.setName(VisusalImgUrl.url + fileInfo.getPath() + fileInfo.getFilename());
        }
        return ActionResult.success(vo);
    }

    /**
     * 获取图片列表
     *
     * @param type 文件夹
     * @return
     */
    @NoDataSourceBind()
    @Operation(summary = "获取图片列表")
    @GetMapping("/{type}")
    @Parameters({
            @Parameter(name = "type", description = "文件夹", required = true),
    })
    public ActionResult<List<ImageVO>> getFile(@PathVariable("type") String type) {
        List<ImageVO> vo = new ArrayList<>();
        VisualImgEnum imgEnum = VisualImgEnum.getByMessage(type);
        if (imgEnum != null) {
            String path = configValueUtil.getBiVisualPath() + imgEnum.getMessage() + "/";
            List<FileListVO> fileList = FileUploadUtils.getFileList(path);
            fileList.forEach(fileListVO -> {
                ImageVO imageVO = new ImageVO();
                imageVO.setName(fileListVO.getFileName());
                imageVO.setLink(VisusalImgUrl.url + fileListVO.getFileName());
                imageVO.setOriginalName(fileListVO.getFileName());
                vo.add(imageVO);
            });
        }
        return ActionResult.success(vo);
    }

    /**
     * 大屏下拉框
     */
    @Operation(summary = "大屏下拉框")
    @GetMapping("/Selector")
    @SaCheckPermission("onlineDev.dataScreen")
    public ActionResult<ListVO<VisualSelectorVO>> selector() {
        List<VisualEntity> visualList = visualService.getList();
        List<VisualCategoryEntity> categoryList = categoryService.getList();
        List<VisualSelectorVO> listVos = new ArrayList<>();
        for (VisualCategoryEntity category : categoryList) {
            VisualSelectorVO categoryModel = new VisualSelectorVO();
            categoryModel.setId(category.getCategoryvalue());
            categoryModel.setFullName(category.getCategorykey());
            List<VisualEntity> visualAll = visualList.stream().filter(t -> t.getCategory().equals(category.getCategoryvalue())).collect(Collectors.toList());
            if (visualAll.size() > 0) {
                List<VisualSelectorVO> childList = new ArrayList<>();
                for (VisualEntity visual : visualAll) {
                    VisualSelectorVO visualModel = new VisualSelectorVO();
                    visualModel.setId(visual.getId());
                    visualModel.setFullName(visual.getTitle());
                    visualModel.setChildren(null);
                    visualModel.setHasChildren(false);
                    childList.add(visualModel);
                }
                categoryModel.setHasChildren(true);
                categoryModel.setChildren(childList);
                listVos.add(categoryModel);
            }
        }
        ListVO vo = new ListVO();
        vo.setList(listVos);
        return ActionResult.success(vo);
    }

    /**
     * 大屏导出
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "大屏导出")
    @PostMapping("/{id}/Actions/ExportData")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    @SaCheckPermission("onlineDev.dataScreen")
    public ActionResult<DownloadVO> exportData(@PathVariable("id") String id) {
        VisualEntity entity = visualService.getInfo(id);
        VisualConfigEntity configEntity = configService.getInfo(id);
        VisualModel model = new VisualModel();
        model.setEntity(entity);
        model.setConfigEntity(configEntity);
        DownloadVO downloadVO = fileExport.exportFile(model, configValueUtil.getTemporaryFilePath(), entity.getTitle(), ModuleTypeEnum.VISUAL_DATA.getTableName());
        return ActionResult.success(downloadVO);
    }

    /**
     * 大屏导入
     *
     * @param multipartFile 文件
     * @return
     */
    @Operation(summary = "大屏导入")
    @SaCheckPermission("onlineDev.dataScreen")
    @PostMapping(value = "/Model/Actions/ImportData", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ActionResult ImportData(MultipartFile multipartFile) throws DataException {
        //判断是否为.json结尾
        if (FileUtil.existsSuffix(multipartFile, ModuleTypeEnum.VISUAL_DATA.getTableName())) {
            return ActionResult.fail(MsgCode.IMP002.get());
        }
        //获取文件内容
        String fileContent = FileUtil.getFileContent(multipartFile);
        VisualModel vo = JsonUtil.getJsonToBean(fileContent, VisualModel.class);
        visualService.createImport(vo.getEntity(), vo.getConfigEntity());
        return ActionResult.success(MsgCode.SU000.get());
    }

    /**
     * 获取API动态数据
     *
     * @param apiRequest 大屏模型
     * @return
     */
    @Operation(summary = "获取API动态数据")
    @PostMapping(value = "/GetApiData")
    @Parameters({
            @Parameter(name = "apiRequest", description = "大屏模型",required = true),
    })
    public String getApiData(@RequestBody @Valid VisualApiRequest apiRequest) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder().callTimeout(Duration.ofSeconds(apiRequest.getTimeout())).build();
        Headers headers;
        Request request;
        if (!apiRequest.getHeaders().isEmpty()) {
            Headers.Builder builder = new Headers.Builder();
            apiRequest.getHeaders().forEach((k, v) -> {
                builder.add(k, v);
            });
            headers = builder.build();
        } else {
            headers = new Headers.Builder().build();
        }
        if (apiRequest.getMethod().equalsIgnoreCase("post")) {
            request = new Request.Builder().url(apiRequest.getUrl())
                    .post(okhttp3.RequestBody.create(okhttp3.MediaType.parse("application/json;charset=utf-8"), apiRequest.getParams().isEmpty() ? "" : JsonUtil.getObjectToString(apiRequest.getParams())))
                    .headers(headers)
                    .build();
        } else {
            String params = Joiner.on("&")
                    .useForNull("")
                    .withKeyValueSeparator("=")
                    .join(apiRequest.getParams());
            request = new Request.Builder().url(apiRequest.getUrl() + (apiRequest.getUrl().contains("?") ? "&" : "?") + params)
                    .get()
                    .headers(headers)
                    .build();
        }
        return client.newCall(request).execute().body().string();
    }
}
