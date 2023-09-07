package org.openea.eap.extj.controller.admin.data;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.controller.SuperController;
import org.openea.eap.extj.base.vo.ListVO;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.JsonUtilEx;
import org.openea.eap.extj.base.entity.ComFieldsEntity;
import org.openea.eap.extj.base.model.comfields.ComFieldsCrForm;
import org.openea.eap.extj.base.model.comfields.ComFieldsInfoVO;
import org.openea.eap.extj.base.model.comfields.ComFieldsListVO;
import org.openea.eap.extj.base.model.comfields.ComFieldsUpForm;
import org.openea.eap.extj.base.service.ComFieldsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "常用字段", description = "CommonFields")
@RestController
@RequestMapping("/api/system/CommonFields")
public class ComFieldsController extends SuperController<ComFieldsService, ComFieldsEntity> {

    @Autowired
    private ComFieldsService comFieldsService;

    /**
     * 获取常用字段列表
     *
     * @return ignore
     */
    @Operation(summary = "获取常用字段列表")
    @SaCheckPermission("systemData.dataModel")
    @GetMapping
    public ActionResult<ListVO<ComFieldsListVO>> list() {
        List<ComFieldsEntity> data = comFieldsService.getList();
        List<ComFieldsListVO> list = JsonUtil.getJsonToList(data, ComFieldsListVO.class);
        ListVO<ComFieldsListVO> vo = new ListVO<>();
        vo.setList(list);
        return ActionResult.success(vo);
    }

    /**
     * 获取常用字段
     *
     * @param id 主键
     * @return ignore
     */
    @Operation(summary = "获取常用字段")
    @Parameter(name = "id", description = "主键", required = true)
    @SaCheckPermission("systemData.dataModel")
    @GetMapping("/{id}")
    public ActionResult<ComFieldsInfoVO> info(@PathVariable("id") String id) throws DataException {
        ComFieldsEntity entity = comFieldsService.getInfo(id);
        ComFieldsInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, ComFieldsInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新增常用字段
     *
     * @param comFieldsCrForm 新增常用字段模型
     * @return ignore
     */
    @Operation(summary = "添加常用字段")
    @Parameter(name = "comFieldsCrForm", description = "新建模型", required = true)
    @SaCheckPermission("systemData.dataModel")
    @PostMapping
    public ActionResult create(@RequestBody @Valid ComFieldsCrForm comFieldsCrForm) {
        ComFieldsEntity entity = JsonUtil.getJsonToBean(comFieldsCrForm, ComFieldsEntity.class);
        if (comFieldsService.isExistByFullName(entity.getField(), entity.getId())) {
            return ActionResult.fail(MsgCode.EXIST001.get());
        }
        comFieldsService.create(entity);
        return ActionResult.success(MsgCode.SU001.get());
    }

    /**
     * 修改常用字段
     *
     * @param id              主键
     * @param comFieldsUpForm 修改常用字段模型
     * @return ignore
     */
    @Operation(summary = "修改常用字段")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "comFieldsUpForm", description = "修改模型", required = true)
    })
    @SaCheckPermission("systemData.dataModel")
    @PutMapping("/{id}")
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid ComFieldsUpForm comFieldsUpForm) {
        ComFieldsEntity entity = JsonUtil.getJsonToBean(comFieldsUpForm, ComFieldsEntity.class);
        if (comFieldsService.isExistByFullName(entity.getField(), id)) {
            return ActionResult.fail(MsgCode.EXIST001.get());
        }
        boolean flag = comFieldsService.update(id, entity);
        if (!flag) {
            return ActionResult.fail(MsgCode.FA002.get());
        }
        return ActionResult.success(MsgCode.SU004.get());
    }

    /**
     * 删除常用字段
     *
     * @param id 主键
     * @return ignore
     */
    @Operation(summary = "删除常用字段")
    @Parameter(name = "id", description = "主键", required = true)
    @SaCheckPermission("systemData.dataModel")
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") String id) {
        ComFieldsEntity entity = comFieldsService.getInfo(id);
        if (entity != null) {
            comFieldsService.delete(entity);
            return ActionResult.success(MsgCode.SU003.get());
        }
        return ActionResult.fail(MsgCode.FA003.get());
    }
}
