package org.openea.eap.extj.controller.admin.base;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.util.RandomUtil;
import org.openea.eap.extj.base.entity.FilterEntity;
import org.openea.eap.extj.base.model.filter.FilterInfo;
import org.openea.eap.extj.base.model.filter.FilterQuery;
import org.openea.eap.extj.base.service.FilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/filter")
public class FilterController {
    @Autowired
    private FilterService filterService;
    /**
     * 获取列表
     * @param page
     * @return
     */
    @PostMapping("list")
    public ActionResult<?> list(@RequestBody @Validated FilterQuery page) {
        QueryWrapper<FilterEntity> wrapper = new QueryWrapper<>();
        
        
        FilterQuery info = filterService.page(page, wrapper);
        
        return ActionResult.success(info);
    }

    /**
     * 查询信息 
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ActionResult<?> info(@PathVariable String id) {
        FilterEntity info = filterService.getById(id);
        return ActionResult.success(info);
    }
    /**
     * 保存信息
     * @param info
     * @return
     */
    @PostMapping("save")
    public ActionResult<?> save(@RequestBody @Validated FilterInfo info) {
        FilterEntity filterEntity = BeanUtil.copyProperties(info, FilterEntity.class);
        filterEntity.setId(RandomUtil.uuId());
        filterService.save(filterEntity);
        return ActionResult.success("保存成功");
    }
    /**
     * 更新信息
     * @param info
     * @return
     */
    @PutMapping("update")
    public ActionResult<?> update(@RequestBody @Validated FilterInfo info) {

        FilterEntity filterEntity = BeanUtil.copyProperties(info, FilterEntity.class);
        filterService.updateById(filterEntity);
        return ActionResult.success("更新成功");
    }
    /**
     * 删除信息
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ActionResult<?> delete(@PathVariable String id) {
        filterService.removeById(id);
        return ActionResult.success("删除成功");
    }

}