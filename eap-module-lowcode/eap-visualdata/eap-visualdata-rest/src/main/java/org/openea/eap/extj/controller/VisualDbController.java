package org.openea.eap.extj.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.controller.SuperController;
import org.openea.eap.extj.entity.VisualDbEntity;
import org.openea.eap.extj.model.visualdb.*;
import org.openea.eap.extj.model.visualdb.*;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.StringUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import org.openea.eap.extj.model.VisualPageVO;
import org.openea.eap.extj.model.VisualPagination;
import org.openea.eap.extj.model.visualdb.*;
import org.openea.eap.extj.service.VisualDbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 大屏数据源配置
 *
 *
 */
@RestController
@Tag(name = "大屏数据源配置", description = "db")
@RequestMapping("/api/blade-visual/db")
public class VisualDbController extends SuperController<VisualDbService, VisualDbEntity> {

    @Autowired
    private VisualDbService dbService;

    /**
     * 分页
     *
     * @param pagination 分页模型
     * @return
     */
    @Operation(summary = "分页")
    @GetMapping("/list")
    public ActionResult<VisualPageVO<VisualDbListVO>> list(VisualPagination pagination) {
        List<VisualDbEntity> data = dbService.getList(pagination);
        List<VisualDbListVO> list = JsonUtil.getJsonToList(data, VisualDbListVO.class);
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
    @SaCheckPermission("onlineDev.dataScreen")
    public ActionResult<VisualDbInfoVO> info(@RequestParam("id")String id) {
        VisualDbEntity entity = dbService.getInfo(id);
        VisualDbInfoVO vo = JsonUtil.getJsonToBean(entity, VisualDbInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新增或修改
     *
     * @param dbUpForm 数据模型
     * @return
     */
    @Operation(summary = "新增或修改")
    @PostMapping("/submit")
    @Parameters({
            @Parameter(name = "dbUpForm", description = "数据模型",required = true),
    })
    @SaCheckPermission("onlineDev.dataScreen")
    public ActionResult submit(@RequestBody VisualDbUpForm dbUpForm) {
        VisualDbEntity entity = JsonUtil.getJsonToBean(dbUpForm, VisualDbEntity.class);
        if (StringUtil.isEmpty(entity.getId())) {
            dbService.create(entity);
            return ActionResult.success("新建成功");
        } else {
            dbService.update(entity.getId(), entity);
            return ActionResult.success("更新成功");
        }
    }

    /**
     * 新增
     *
     * @param dbCrForm 数据模型
     * @return
     */
    @Operation(summary = "新增")
    @PostMapping("/save")
    @Parameters({
            @Parameter(name = "dbCrForm", description = "数据模型",required = true),
    })
    @SaCheckPermission("onlineDev.dataScreen")
    public ActionResult create(@RequestBody VisualDbCrForm dbCrForm) {
        VisualDbEntity entity = JsonUtil.getJsonToBean(dbCrForm, VisualDbEntity.class);
        dbService.create(entity);
        return ActionResult.success("新建成功");
    }

    /**
     * 修改
     *
     * @param dbUpForm 数据模型
     * @return
     */
    @Operation(summary = "修改")
    @PostMapping("/update")
    @Parameters({
            @Parameter(name = "dbUpForm", description = "数据模型",required = true),
    })
    @SaCheckPermission("onlineDev.dataScreen")
    public ActionResult update(@RequestBody VisualDbUpForm dbUpForm) {
        VisualDbEntity entity = JsonUtil.getJsonToBean(dbUpForm, VisualDbEntity.class);
        dbService.update(entity.getId(), entity);
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
    public ActionResult delete(String ids) {
        VisualDbEntity entity = dbService.getInfo(ids);
        if (entity != null) {
            dbService.delete(entity);
            return ActionResult.success("删除成功");
        }
        return ActionResult.fail("删除失败，数据不存在");
    }

    /**
     * 下拉数据源
     *
     * @return
     */
    @Operation(summary = "下拉数据源")
    @GetMapping("/db-list")
    @SaCheckPermission("onlineDev.dataScreen")
    public ActionResult<List<VisualDbSelectVO>> list() {
        List<VisualDbEntity> data = dbService.getList();
        List<VisualDbSelectVO> list = JsonUtil.getJsonToList(data, VisualDbSelectVO.class);
        return ActionResult.success(list);
    }

    /**
     * 数据源测试连接
     *
     * @param dbCrForm 数据源模型
     * @return
     */
    @Operation(summary = "数据源测试连接")
    @PostMapping("/db-test")
    @Parameters({
            @Parameter(name = "dbCrForm", description = "数据源模型",required = true),
    })
    @SaCheckPermission("onlineDev.dataScreen")
    public ActionResult test(@RequestBody VisualDbCrForm dbCrForm) {
        VisualDbEntity entity = JsonUtil.getJsonToBean(dbCrForm, VisualDbEntity.class);
        boolean flag = dbService.dbTest(entity);
        if (flag) {
            return ActionResult.success("连接成功");
        }
        return ActionResult.fail("连接失败");
    }

    /**
     * 动态执行SQL
     *
     * @param queryForm 数据模型
     * @return
     */
    @Operation(summary = "动态执行SQL")
    @PostMapping("/dynamic-query")
    @Parameters({
            @Parameter(name = "queryForm", description = "数据模型",required = true),
    })
    public ActionResult query(@RequestBody VisualDbQueryForm queryForm) {
        VisualDbEntity entity = dbService.getInfo(queryForm.getId());
        List<Map<String, Object>> data = new ArrayList<>();
        if (entity != null) {
            data = dbService.query(entity, queryForm.getSql());
        }
        return ActionResult.success(data);
    }

}
