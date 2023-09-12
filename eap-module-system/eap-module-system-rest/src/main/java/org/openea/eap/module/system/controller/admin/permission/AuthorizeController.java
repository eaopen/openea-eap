package org.openea.eap.module.system.controller.admin.permission;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.permission.entity.ColumnsPurviewEntity;
import org.openea.eap.extj.permission.model.columnspurview.ColumnsPurviewUpForm;
import org.openea.eap.extj.permission.service.ColumnsPurviewService;
import org.openea.eap.extj.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Tag(name = "操作权限", description = "Authorize")
@RestController
@RequestMapping("/permission/Authority")
public class AuthorizeController {

    @Autowired
    private ColumnsPurviewService columnsPurviewService;

    /**
     * 获取模块列表展示字段
     *
     * @param moduleId 菜单Id
     * @return
     */
    @Operation(summary = "获取模块列表展示字段")
    @Parameters({
            @Parameter(name = "moduleId", description = "菜单id", required = true)
    })
    @GetMapping("/GetColumnsByModuleId/{moduleId}")
    public ActionResult getColumnsByModuleId(@PathVariable("moduleId") String moduleId) {
        ColumnsPurviewEntity entity = columnsPurviewService.getInfo(moduleId);
        List<Map<String, Object>> jsonToListMap = null;
        if (entity != null) {
            jsonToListMap = JsonUtil.getJsonToListMap(entity.getFieldList());
        }
        return ActionResult.success(jsonToListMap != null ? jsonToListMap : new ArrayList<>(16));
    }

    /**
     * 配置模块列表展示字段
     *
     * @param columnsPurviewUpForm 修改模型
     * @return
     */
    @Operation(summary = "配置模块列表展示字段")
    @Parameters({
            @Parameter(name = "columnsPurviewUpForm", description = "修改模型", required = true)
    })
    @PutMapping("/SetColumnsByModuleId")
    public ActionResult setColumnsByModuleId(@RequestBody ColumnsPurviewUpForm columnsPurviewUpForm) {
        ColumnsPurviewEntity entity = JsonUtil.getJsonToBean(columnsPurviewUpForm, ColumnsPurviewEntity.class);
        columnsPurviewService.update(columnsPurviewUpForm.getModuleId(), entity);
        return ActionResult.success(MsgCode.SU005.get());
    }
}
