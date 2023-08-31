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
import org.openea.eap.framework.common.pojo.CommonResult;
import org.openea.eap.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import org.openea.eap.module.system.convert.dict.DictDataConvert;
import org.openea.eap.module.system.dal.dataobject.dict.DictDataDO;
import org.openea.eap.module.system.service.dict.DictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

import static org.openea.eap.framework.common.pojo.CommonResult.success;

@RestController
@Tag(name = "数据字典", description = "dictionary")
@RequestMapping("/api/system/DictionaryData")
public class DictionaryDataController  extends SuperController<DictionaryDataService, DictionaryDataEntity> {
    @Autowired
    private DictionaryDataService dictionaryDataService;
    @Autowired
    private DictionaryTypeService dictionaryTypeService;
    @Resource
    private DictDataService dictDataService;

    @ApiOperation("获取数据字典列表(分类+内容)")
    @GetMapping("/All-simple")
    public ActionResult<ListVO<Map<String, Object>>> allBindDictionarySimple() {
        List<DictionaryTypeEntity> dictionaryTypeList = dictionaryTypeService.getList();
        List<Map<String, Object>> list = new ArrayList<>();
        for (DictionaryTypeEntity dictionaryTypeEntity : dictionaryTypeList) {
            List<DictionaryDataEntity> childNodeList = dictionaryDataService.getList(dictionaryTypeEntity.getId(), true);
            for (DictionaryDataEntity item : childNodeList) {
                Map<String, Object> htItem = new HashMap<>(16);
                htItem.put("dictType", dictionaryTypeEntity.getEnCode());
                htItem.put("lable", item.getFullName());
                htItem.put("value", item.getId());
                htItem.put("parentId", dictionaryTypeEntity.getId());
                list.add(htItem);
            }
        }
        List<DictDataDO> listDictDataDO = dictDataService.getDictDataList();
        for (DictDataDO dictDataDO : listDictDataDO) {
            Map<String, Object> htItem = new HashMap<>(16);
            htItem.put("dictType", dictDataDO.getDictType());
            htItem.put("lable", dictDataDO.getLabel());
            htItem.put("value", dictDataDO.getValue());
            htItem.put("parentId", dictDataDO.getParentId());
            list.add(htItem);
        }
        ListVO<Map<String, Object>> vo = new ListVO<>();
        vo.setList(list);
        return ActionResult.success(vo);
    }

}
