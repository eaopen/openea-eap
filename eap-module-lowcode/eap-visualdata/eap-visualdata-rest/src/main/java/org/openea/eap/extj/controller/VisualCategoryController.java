package org.openea.eap.extj.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.base.controller.SuperController;
import org.openea.eap.extj.config.ConfigValueUtil;
import org.openea.eap.extj.entity.VisualCategoryEntity;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.UserProvider;
import org.openea.eap.extj.util.data.DataSourceContextHolder;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import org.openea.eap.extj.model.VisualPageVO;
import org.openea.eap.extj.model.VisualPagination;
import org.openea.eap.extj.model.visualcategory.VisualCategoryCrForm;
import org.openea.eap.extj.model.visualcategory.VisualCategoryInfoVO;
import org.openea.eap.extj.model.visualcategory.VisualCategoryListVO;
import org.openea.eap.extj.model.visualcategory.VisualCategoryUpForm;
import org.openea.eap.extj.util.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.openea.eap.extj.service.VisualCategoryService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import java.util.List;

/**
 * 大屏分类
 *
 *
 */
@RestController
@Tag(name = "大屏分类", description = "category")
@RequestMapping("/api/blade-visual/category")
public class VisualCategoryController extends SuperController<VisualCategoryService, VisualCategoryEntity> {

    @Autowired
    private VisualCategoryService categoryService;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private UserProvider userProvider;

    /**
     * 列表
     *
     * @param pagination 分页模型
     * @return
     */
    @Operation(summary = "列表")
    @GetMapping("/page")
    @SaCheckPermission("onlineDev.dataScreen")
    public ActionResult<VisualPageVO<VisualCategoryListVO>> list(VisualPagination pagination) {
        if (configValueUtil.isMultiTenancy()) {
            String jwtToken = ServletUtil.getRequest().getHeader("Authorization");
            if(StringUtil.isEmpty(jwtToken)){
                //兼容旧版大屏前端
                jwtToken = ServletUtil.getRequest().getParameter("token");
            }
            UserInfo userInfo = userProvider.get(jwtToken);
            if(userInfo.getTenantId() == null){
                throw new RuntimeException("租户信息为空: " + jwtToken);
            }
            //设置租户
            DataSourceContextHolder.setDatasource(userInfo.getTenantId(), userInfo.getTenantDbConnectionString(), userInfo.isAssignDataSource());
        }
        List<VisualCategoryEntity> data = categoryService.getList(pagination);
        List<VisualCategoryListVO> list = JsonUtil.getJsonToList(data, VisualCategoryListVO.class);
        VisualPageVO paginationVO = JsonUtil.getJsonToBean(pagination, VisualPageVO.class);
        paginationVO.setRecords(list);
        return ActionResult.success(paginationVO);
    }

    /**
     * 列表
     *
     * @return
     */
    @Operation(summary = "列表")
    @GetMapping("/list")
    @SaCheckPermission("onlineDev.dataScreen")
    public ActionResult<List<VisualCategoryListVO>> list() {
        List<VisualCategoryEntity> data = categoryService.getList();
        List<VisualCategoryListVO> list = JsonUtil.getJsonToList(data, VisualCategoryListVO.class);
        return ActionResult.success(list);
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
    public ActionResult<VisualCategoryInfoVO> info(@RequestParam("id") String id) {
        VisualCategoryEntity entity = categoryService.getInfo(id);
        VisualCategoryInfoVO vo = JsonUtil.getJsonToBean(entity, VisualCategoryInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新增
     *
     * @param categoryCrForm 大屏分类模型
     * @return
     */
    @Operation(summary = "新增")
    @PostMapping("/save")
    @Parameters({
            @Parameter(name = "categoryCrForm", description = "大屏分类模型",required = true),
    })
    @SaCheckPermission("onlineDev.dataScreen")
    public ActionResult create(@RequestBody @Valid VisualCategoryCrForm categoryCrForm) {
        VisualCategoryEntity entity = JsonUtil.getJsonToBean(categoryCrForm, VisualCategoryEntity.class);
        if (categoryService.isExistByValue(entity.getCategoryvalue(), entity.getId())) {
            return ActionResult.fail("模块键值已存在");
        }
        categoryService.create(entity);
        return ActionResult.success("新建成功");
    }

    /**
     * 修改
     *
     * @param categoryUpForm 大屏分类模型
     * @return
     */
    @Operation(summary = "修改")
    @PostMapping("/update")
    @Parameters({
            @Parameter(name = "categoryUpForm", description = "大屏分类模型",required = true),
    })
    @SaCheckPermission("onlineDev.dataScreen")
    public ActionResult update(@RequestBody VisualCategoryUpForm categoryUpForm) {
        VisualCategoryEntity entity = JsonUtil.getJsonToBean(categoryUpForm, VisualCategoryEntity.class);
        if (categoryService.isExistByValue(entity.getCategoryvalue(), entity.getId())) {
            return ActionResult.fail("模块键值已存在");
        }
        boolean flag = categoryService.update(categoryUpForm.getId(), entity);
        if (!flag) {
            return ActionResult.fail("更新失败，数据不存在");
        }
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
        VisualCategoryEntity entity = categoryService.getInfo(ids);
        if (entity != null) {
            categoryService.delete(entity);
            return ActionResult.success("删除成功");
        }
        return ActionResult.fail("删除失败，数据不存在");
    }

}
