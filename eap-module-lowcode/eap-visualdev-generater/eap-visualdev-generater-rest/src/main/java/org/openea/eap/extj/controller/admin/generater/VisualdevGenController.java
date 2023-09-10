package org.openea.eap.extj.controller.admin.generater;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.FileInfo;
import org.openea.eap.extj.base.NoDataSourceBind;
import org.openea.eap.extj.base.entity.DictionaryDataEntity;
import org.openea.eap.extj.base.entity.VisualdevEntity;
import org.openea.eap.extj.base.model.DownloadCodeForm;
import org.openea.eap.extj.base.model.read.ReadListVO;
import org.openea.eap.extj.base.service.DictionaryDataService;
import org.openea.eap.extj.base.service.VisualdevService;
import org.openea.eap.extj.base.util.ReadFile;
import org.openea.eap.extj.base.util.VisualUtil;
import org.openea.eap.extj.base.vo.DownloadVO;
import org.openea.eap.extj.base.vo.ListVO;
import org.openea.eap.extj.config.ConfigValueUtil;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.generater.service.VisualdevGenService;
import org.openea.eap.extj.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 可视化开发功能表
 * 
 */
@Tag(name = "代码生成器", description = "Generater")
@RestController
@RequestMapping("/api/visualdev/Generater")
public class VisualdevGenController {

    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private VisualdevService visualdevService;
    @Autowired
    private VisualdevGenService visualdevGenService;
    @Autowired
    private DictionaryDataService dictionaryDataApi;


    /**
     * 下载文件
     *
     * @return
     */
    @NoDataSourceBind()
    @Operation(summary = "下载文件")
    @GetMapping("/DownloadVisCode")
    public void downloadCode() throws DataException {
        HttpServletRequest request = ServletUtil.getRequest();
        String reqJson = request.getParameter("encryption");
        String name = request.getParameter("name");
        String fileNameAll = DesUtil.aesDecode(reqJson);
        if (!StringUtil.isEmpty(fileNameAll)) {
            String token = fileNameAll.split("#")[0];
            if (TicketUtil.parseTicket(token) != null) {
                TicketUtil.deleteTicket(token);
                String fileName = fileNameAll.split("#")[1];
                String path = configValueUtil.getServiceDirectoryPath();
                byte[] bytes = FileUploadUtils.downloadFileByte(path, fileName, false);
                FileDownloadUtil.downloadFile(bytes, fileName, name);
            }else {
                throw new DataException("下载链接已失效");
            }
        }else {
            throw new DataException("下载链接已失效");
        }
    }

    @Operation(summary = "获取命名空间")
    @GetMapping("/AreasName")
    @SaCheckPermission("generator.webForm")
    public ActionResult getAreasName() {
        String areasName = configValueUtil.getCodeAreasName();
        List<String> areasNameList = new ArrayList(Arrays.asList(areasName.split(",")));
        return ActionResult.success(areasNameList);
    }

    @Operation(summary = "下载代码")
    @Parameters({
            @Parameter(name = "id", description = "主键"),
    })
    @PostMapping("/{id}/Actions/DownloadCode")
    @SaCheckPermission("generator.webForm")
    @DSTransactional
    public ActionResult downloadCode(@PathVariable("id") String id, @RequestBody DownloadCodeForm downloadCodeForm) throws Exception {
        if(downloadCodeForm.getModule()!=null){
            DictionaryDataEntity info = dictionaryDataApi.getInfo(downloadCodeForm.getModule());
            if(info!=null){
                downloadCodeForm.setModule(info.getEnCode());
            }
        }
        DownloadVO vo;
        VisualdevEntity visualdevEntity = visualdevService.getInfo(id);
        String s = VisualUtil.checkPublishVisualModel(visualdevEntity,"下载");
        if (s!=null) {
            return ActionResult.fail(s);
        }
        String fileName = visualdevGenService.codeGengerate(visualdevEntity, downloadCodeForm);
        //上传到minio
        String filePath = FileUploadUtils.getLocalBasePath() + configValueUtil.getServiceDirectoryPath() + fileName + ".zip";
        FileUtil.toZip(filePath, true, FileUploadUtils.getLocalBasePath() + configValueUtil.getServiceDirectoryPath() + fileName);
        // 删除源文件
        FileUtil.deleteFileAll(new File(FileUploadUtils.getLocalBasePath() + configValueUtil.getServiceDirectoryPath() + fileName));
        MultipartFile multipartFile = FileUtil.createFileItem(new File(XSSEscape.escapePath(filePath)));
        FileInfo fileInfo = FileUploadUtils.uploadFile(multipartFile, configValueUtil.getServiceDirectoryPath(), fileName + ".zip");
        vo = DownloadVO.builder().name(fileInfo.getFilename()).url(UploaderUtil.uploaderVisualFile(fileInfo.getFilename()) + "&name=" + fileName + ".zip").build();
        if (vo == null) {
            return ActionResult.fail(MsgCode.FA006.get());
        }
        return ActionResult.success(vo);
    }


    /**
     * 输出移动开发模板
     *
     * @return
     */
    @Operation(summary = "预览代码")
    @Parameters({
            @Parameter(name = "id", description = "主键"),
    })
    @PostMapping("/{id}/Actions/CodePreview")
    @SaCheckPermission("generator.webForm")
    public ActionResult codePreview(@PathVariable("id") String id, @RequestBody DownloadCodeForm downloadCodeForm) throws Exception {
        VisualdevEntity visualdevEntity = visualdevService.getInfo(id);
        String s = VisualUtil.checkPublishVisualModel(visualdevEntity,"预览");
        if (s!=null) {
            return ActionResult.fail(s);
        }
        String fileName = visualdevGenService.codeGengerate(visualdevEntity, downloadCodeForm);
        List<ReadListVO> dataList = ReadFile.priviewCode(FileUploadUtils.getLocalBasePath() + configValueUtil.getServiceDirectoryPath() + fileName);
        if (dataList.size() == 0) {
            return ActionResult.fail(MsgCode.FA015.get());
        }
        ListVO datas = new ListVO<>();
        datas.setList(dataList);
        return ActionResult.success(datas);
    }

    /**
     * App预览(后台APP表单设计)
     *
     * @return
     */
    @Operation(summary = "App预览(后台APP表单设计)")
    @Parameters({
            @Parameter(name = "data", description = "数据"),
    })
    @PostMapping("/App/Preview")
    @SaCheckPermission("generator.webForm")
    public ActionResult appPreview(String data) {
        String id = RandomUtil.uuId();
        redisUtil.insert(id, data, 300);
        return ActionResult.success((Object) id);
    }

    /**
     * App预览(后台APP表单设计)
     *
     * @return
     */
    @Operation(summary = "App预览查看")
    @Parameters({
            @Parameter(name = "id", description = "主键"),
    })
    @GetMapping("/App/{id}/Preview")
    @SaCheckPermission("generator.webForm")
    public ActionResult preview(@PathVariable("id") String id) {
        if (redisUtil.exists(id)) {
            Object object = redisUtil.getString(id);
            return ActionResult.success(object);
        } else {
            return ActionResult.fail(MsgCode.FA019.get());
        }
    }

}
