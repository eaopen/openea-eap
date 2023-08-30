package org.openea.eap.extj.base.controller.admin.dictionary;

import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.controller.SuperController;
import org.openea.eap.extj.base.entity.DictionaryDataEntity;
import org.openea.eap.extj.base.entity.DictionaryTypeEntity;
import org.openea.eap.extj.base.model.DictionaryDataExportModel;
import org.openea.eap.extj.base.service.DictionaryDataService;
import org.openea.eap.extj.base.service.DictionaryTypeService;
import org.openea.eap.extj.base.vo.ListVO;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.treeutil.SumTree;
import org.openea.eap.extj.util.treeutil.newtreeutil.TreeDotUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "数据字典", description = "dictionary")
@RequestMapping("/api/system/DictionaryData")
public class DictionaryDataController  extends SuperController<DictionaryDataService, DictionaryDataEntity> {
    @Autowired
    private DictionaryDataService dictionaryDataService;
    @Autowired
    private DictionaryTypeService dictionaryTypeService;

    @ApiOperation("获取数据字典列表(分类+内容)")
    @GetMapping("/All-simple")
    public ActionResult<ListVO<Map<String, Object>>> allBindDictionarySimple() {
        List<DictionaryTypeEntity> dictionaryTypeList = dictionaryTypeService.getList();
        List<Map<String, Object>> list = new ArrayList<>();
        for (DictionaryTypeEntity dictionaryTypeEntity : dictionaryTypeList) {
            List<DictionaryDataEntity> childNodeList = dictionaryDataService.getList(dictionaryTypeEntity.getId(), true);
                for (DictionaryDataEntity item : childNodeList) {
                    Map<String, Object> htItem = new HashMap<>(16);
                    htItem.put("dictType",dictionaryTypeEntity.getEnCode());
                    htItem.put("lable", item.getFullName());
                    htItem.put("value", item.getId());
                    htItem.put("parentId", dictionaryTypeEntity.getId());
                    list.add(htItem);
                }
        }
        ListVO<Map<String, Object>> vo = new ListVO<>();
        vo.setList(list);
        return ActionResult.success(vo);
    }

}
