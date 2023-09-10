package org.openea.eap.extj.controller.admin.onlinedev;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.entity.VisualdevEntity;
import org.openea.eap.extj.base.service.VisualdevService;
import org.openea.eap.extj.base.vo.DownloadVO;
import org.openea.eap.extj.config.ConfigValueUtil;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.onlinedev.model.BaseDevModelVO;
import org.openea.eap.extj.util.*;
import org.openea.eap.extj.util.enums.ExportModelTypeEnum;
import org.openea.eap.extj.util.enums.ModuleTypeEnum;
import org.openea.eap.extj.util.file.FileExport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 0代码app无表开发
 *
 */
@Tag(name = "0代码app无表开发", description = "ModelAppData")
@RestController
@RequestMapping("/api/visualdev/OnlineDev/App")
public class VisualdevModelAppController {

    @Autowired
    private VisualdevService visualdevService;
    @Autowired
    private FileExport fileExport;
    @Autowired
    private ConfigValueUtil configValueUtil;



    @Operation(summary = "功能导出")
    @PostMapping("/{modelId}/Actions/ExportData")
    public ActionResult exportData(@PathVariable("modelId") String modelId){
        VisualdevEntity visualdevEntity = visualdevService.getInfo(modelId);
        BaseDevModelVO vo = JsonUtil.getJsonToBean(visualdevEntity,BaseDevModelVO.class);
        vo.setModelType(ExportModelTypeEnum.App.getMessage());
        DownloadVO downloadVO=fileExport.exportFile(vo,configValueUtil.getTemporaryFilePath(), visualdevEntity.getFullName(), ModuleTypeEnum.VISUAL_APP.getTableName());
        return ActionResult.success(downloadVO);
    }

    @Operation(summary = "功能导入")
    @PostMapping(value = "/Model/Actions/ImportData", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ActionResult ImportData(@RequestPart("file") MultipartFile multipartFile) throws WorkFlowException {
        //判断是否为.json结尾
        if (FileUtil.existsSuffix(multipartFile, ModuleTypeEnum.VISUAL_APP.getTableName())) {
            return ActionResult.fail(MsgCode.IMP002.get());
        }
        //获取文件内容
        String fileContent = FileUtil.getFileContent(multipartFile);
        BaseDevModelVO vo = JsonUtil.getJsonToBean(fileContent, BaseDevModelVO.class);
        if (vo.getModelType() == null || !vo.getModelType().equals(ExportModelTypeEnum.App.getMessage())) {
            return ActionResult.fail("请导入对应功能的json文件");
        }
        VisualdevEntity visualdevEntity = JsonUtil.getJsonToBean(vo, VisualdevEntity.class);
        String modelId = visualdevEntity.getId();
        if (StringUtil.isNotEmpty(modelId)) {
            VisualdevEntity entity = visualdevService.getInfo(modelId);
            if (entity != null) {
                return ActionResult.fail("已存在相同功能");
            }
        }
        visualdevEntity.setCreatorTime(DateUtil.getNowDate());
        visualdevEntity.setLastModifyTime(null);
        visualdevService.create(visualdevEntity);
        return ActionResult.success(MsgCode.IMP001.get());
    }
}
