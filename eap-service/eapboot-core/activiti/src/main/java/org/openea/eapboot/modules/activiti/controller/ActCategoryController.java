package org.openea.eapboot.modules.activiti.controller;

import org.openea.eapboot.common.constant.CommonConstant;
import org.openea.eapboot.common.utils.ResultUtil;
import org.openea.eapboot.common.vo.Result;
import org.openea.eapboot.modules.activiti.entity.ActCategory;
import org.openea.eapboot.modules.activiti.entity.ActProcess;
import org.openea.eapboot.modules.activiti.service.ActCategoryService;
import org.openea.eapboot.modules.activiti.service.ActProcessService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 */
@Slf4j
@RestController
@Api(description = "流程分类管理接口")
@RequestMapping("/eapboot/actCategory")
@CacheConfig(cacheNames = "actCategory")
@Transactional
public class ActCategoryController {

    @Autowired
    private ActCategoryService actCategoryService;

    @Autowired
    private ActProcessService actProcessService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RequestMapping(value = "/getByParentId/{parentId}", method = RequestMethod.GET)
    @ApiOperation(value = "通过id获取")
    public Result<List<ActCategory>> getByParentId(@PathVariable String parentId){

        List<ActCategory> list = actCategoryService.findByParentIdOrderBySortOrder(parentId);
        list.forEach(item -> {
            if(!CommonConstant.PARENT_ID.equals(item.getParentId())){
                ActCategory parent = actCategoryService.get(item.getParentId());
                item.setParentTitle(parent.getTitle());
            }else{
                item.setParentTitle("一级节点");
            }
        });
        return new ResultUtil<List<ActCategory>>().setData(list);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "添加")
    public Result<Object> add(@ModelAttribute ActCategory actCategory){

        ActCategory c = actCategoryService.save(actCategory);
        // 如果不是添加的一级 判断设置上级为父节点标识
        if(!CommonConstant.PARENT_ID.equals(actCategory.getParentId())){
            ActCategory parent = actCategoryService.get(actCategory.getParentId());
            if(parent.getIsParent()==null||!parent.getIsParent()){
                parent.setIsParent(true);
                actCategoryService.update(parent);
            }
        }
        // 更新缓存
        Set<String> keys = redisTemplate.keys("actCategory::*");
        redisTemplate.delete(keys);
        return new ResultUtil<Object>().setSuccessMsg("添加成功");
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ApiOperation(value = "编辑")
    public Result<Object> edit(@ModelAttribute ActCategory department){

        ActCategory c = actCategoryService.update(department);
        // 手动删除所有分类缓存
        Set<String> keys = redisTemplate.keys("actCategory:" + "*");
        redisTemplate.delete(keys);
        return new ResultUtil<Object>().setSuccessMsg("编辑成功");
    }

    @RequestMapping(value = "/delByIds/{ids}", method = RequestMethod.DELETE)
    @ApiOperation(value = "批量通过id删除")
    public Result<Object> delByIds(@PathVariable String[] ids){

        for(String id:ids){
            List<ActProcess> list = actProcessService.findByCategoryId(id);
            if(list!=null&&list.size()>0){
                return new ResultUtil<Object>().setErrorMsg("删除失败，包含正被流程使用关联的分类");
            }
        }
        for(String id:ids){
            actCategoryService.delete(id);
        }
        // 手动删除所有缓存
        Set<String> keys = redisTemplate.keys("actCategory:" + "*");
        redisTemplate.delete(keys);
        return new ResultUtil<Object>().setSuccessMsg("批量通过id删除数据成功");
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ApiOperation(value = "名称模糊搜索")
    public Result<List<ActCategory>> searchByTitle(@RequestParam String title) {

        List<ActCategory> list = actCategoryService.findByTitleLikeOrderBySortOrder("%"+title+"%");
        list.forEach(item -> {
            if(!CommonConstant.PARENT_ID.equals(item.getParentId())){
                ActCategory parent = actCategoryService.get(item.getParentId());
                item.setParentTitle(parent.getTitle());
            }else{
                item.setParentTitle("一级节点");
            }
        });
        return new ResultUtil<List<ActCategory>>().setData(list);
    }
}
