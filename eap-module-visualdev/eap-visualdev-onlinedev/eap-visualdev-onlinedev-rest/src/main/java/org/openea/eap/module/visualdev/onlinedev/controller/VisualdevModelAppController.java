package org.openea.eap.module.visualdev.onlinedev.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.openea.eap.module.visualdev.base.ActionResult;
import org.openea.eap.module.visualdev.base.entity.VisualdevEntity;
import org.openea.eap.module.visualdev.base.service.VisualdevService;
import org.openea.eap.module.visualdev.base.vo.DownloadVO;
import org.openea.eap.module.visualdev.config.ConfigValueUtil;
import org.openea.eap.module.visualdev.constant.MsgCode;
import org.openea.eap.module.visualdev.emnus.ExportModelTypeEnum;
import org.openea.eap.module.visualdev.emnus.ModuleTypeEnum;
import org.openea.eap.module.visualdev.exception.WorkFlowException;
import org.openea.eap.module.visualdev.onlinedev.model.*;
import org.openea.eap.module.visualdev.util.*;
import org.openea.eap.module.visualdev.util.FileExport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

/**
 * 0代码app无表开发
 *
 * @author JNPF开发平台组
 * @version V3.1.0
 * @copyright 引迈信息技术有限公司
 * @date 2019年9月27日 上午9:18
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
