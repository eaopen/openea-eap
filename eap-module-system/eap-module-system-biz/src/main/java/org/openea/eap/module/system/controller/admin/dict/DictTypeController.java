package org.openea.eap.module.system.controller.admin.dict;

import org.openea.eap.framework.common.pojo.CommonResult;
import org.openea.eap.framework.common.pojo.PageResult;
import org.openea.eap.framework.excel.core.util.ExcelUtils;
import org.openea.eap.framework.operatelog.core.annotations.OperateLog;
import org.openea.eap.module.system.controller.admin.dict.vo.type.*;
import org.openea.eap.module.system.convert.dict.DictTypeConvert;
import org.openea.eap.module.system.dal.dataobject.dict.DictTypeDO;
import org.openea.eap.module.system.service.dict.DictTypeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

import static org.openea.eap.framework.common.pojo.CommonResult.success;
import static org.openea.eap.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;

@Tag(name = "管理后台 - 字典类型")
@RestController
@RequestMapping("/system/dict-type")
@Validated
public class DictTypeController {

    @Resource
    private DictTypeService dictTypeService;

    @PostMapping("/create")
    @Operation(summary = "创建字典类型")
    @PreAuthorize("@ss.hasPermission('system:dict:create')")
    public CommonResult<Long> createDictType(@Valid @RequestBody DictTypeCreateReqVO reqVO) {
        Long dictTypeId = dictTypeService.createDictType(reqVO);
        return success(dictTypeId);
    }

    @PutMapping("/update")
    @Operation(summary = "修改字典类型")
    @PreAuthorize("@ss.hasPermission('system:dict:update')")
    public CommonResult<Boolean> updateDictType(@Valid @RequestBody DictTypeUpdateReqVO reqVO) {
        dictTypeService.updateDictType(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除字典类型")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:dict:delete')")
    public CommonResult<Boolean> deleteDictType(Long id) {
        dictTypeService.deleteDictType(id);
        return success(true);
    }

    @Operation(summary = "/获得字典类型的分页列表")
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    public CommonResult<PageResult<DictTypeRespVO>> pageDictTypes(@Valid DictTypePageReqVO reqVO) {
        return success(DictTypeConvert.INSTANCE.convertPage(dictTypeService.getDictTypePage(reqVO)));
    }

    @Operation(summary = "/查询字典类型详细")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @GetMapping(value = "/get")
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    public CommonResult<DictTypeRespVO> getDictType(@RequestParam("id") Long id) {
        return success(DictTypeConvert.INSTANCE.convert(dictTypeService.getDictType(id)));
    }

    @GetMapping("/list-all-simple")
    @Operation(summary = "获得全部字典类型列表", description = "包括开启 + 禁用的字典类型，主要用于前端的下拉选项")
    // 无需添加权限认证，因为前端全局都需要
    public CommonResult<List<DictTypeSimpleRespVO>> getSimpleDictTypeList() {
        List<DictTypeDO> list = dictTypeService.getDictTypeList();
        return success(DictTypeConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/list-all-parent/{id}")
    @Operation(summary = "获取所有字典分类下拉框列表", description = "包括开启 + 禁用的字典类型，主要用于前端的下拉选项")
    // 无需添加权限认证，因为前端全局都需要
    public CommonResult<List<Map<String,Object>>> getParentDictTypeList(@PathVariable(value = "id", required = true) String id) {
        List<DictTypeDO> list = dictTypeService.getDictTypeList();
        if (!"0".equals(id)){
            list.remove(dictTypeService.getDictType(id));
        }
        List<Map<String,Object>> listVo=new ArrayList<>();
        for (DictTypeDO dictTypeDO : list) {
            if (dictTypeDO.getParentId()==null || dictTypeDO.getParentId()==0){
                Map<String,Object> map=new HashMap<>();
                map.put("id",dictTypeDO.getId());
                map.put("enCode",dictTypeDO.getType());
                map.put("parentid","-1");
                map.put("fullname",dictTypeDO.getName());
                map.put("dataType",dictTypeDO.getDataType());
                map.put("hasChildren",false);
                listVo.add(map);
            }
        }
        for (DictTypeDO dictTypeDO : list) {
            if (dictTypeDO.getParentId()!=null && dictTypeDO.getParentId()!=0){
                for (Map<String, Object> mapo : listVo) {
                    if(mapo.get("id").equals(dictTypeDO.getParentId())){
                        List<Map<String,Object>> list1=new ArrayList<>();
                        Map<String,Object> map=new HashMap<>();
                        map.put("id",dictTypeDO.getId());
                        map.put("enCode",dictTypeDO.getType());
                        map.put("parentid","-1");
                        map.put("fullname",dictTypeDO.getName());
                        map.put("hasChildren",false);
                        map.put("dataType",dictTypeDO.getDataType());
                        list1.add(map);
                        if (mapo.get("children")!=null){
                            List children = Collections.singletonList(mapo.get("children"));
                            children.add(map);
                            mapo.put("hasChildren",true);
                            mapo.put("children",children);
                        }else {
                            mapo.put("hasChildren", true);
                            mapo.put("children", list1);
                        }
                    }
                }
            }
        }
        return success(listVo);
    }

    @Operation(summary = "导出数据类型")
    @GetMapping("/export")
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    @OperateLog(type = EXPORT)
    public void export(HttpServletResponse response, @Valid DictTypeExportReqVO reqVO) throws IOException {
        List<DictTypeDO> list = dictTypeService.getDictTypeList(reqVO);
        List<DictTypeExcelVO> data = DictTypeConvert.INSTANCE.convertList02(list);
        // 输出
        ExcelUtils.write(response, "字典类型.xls", "类型列表", DictTypeExcelVO.class, data);
    }

}
