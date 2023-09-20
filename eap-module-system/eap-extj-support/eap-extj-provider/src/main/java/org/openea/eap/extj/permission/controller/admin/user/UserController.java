package org.openea.eap.extj.permission.controller.admin.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.Pagination;
import org.openea.eap.extj.base.controller.SuperController;
import org.openea.eap.extj.base.vo.ListVO;
import org.openea.eap.extj.base.vo.PaginationVO;
import org.openea.eap.extj.permission.constant.PermissionConst;
import org.openea.eap.extj.permission.entity.OrganizeEntity;
import org.openea.eap.extj.permission.entity.UserEntity;
import org.openea.eap.extj.permission.entity.UserRelationEntity;
import org.openea.eap.extj.permission.model.user.vo.UserSelectorVO;
import org.openea.eap.extj.permission.service.OrganizeService;
import org.openea.eap.extj.permission.service.UserRelationService;
import org.openea.eap.extj.permission.service.UserService;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.XSSEscape;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "用户管理", value = "Users")
@Slf4j
@RestController("UserControllerSelector")
@RequestMapping("/permission/Users")
public class UserController extends SuperController<UserService, UserEntity> {


    @Resource
    private UserService userService;
    @Resource
    private UserRelationService userRelationService;
    @Resource
    private OrganizeService organizeService;

    /**
     * 获取用户下拉框列表
     *
     * @param organizeIdForm 组织id
     * @param pagination     分页模型
     * @return
     */
    @ApiOperation("获取用户下拉框列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizeId", value = "组织id", required = true),
            @ApiImplicitParam(name = "pagination", value = "分页模型", required = true)
    })
    @PostMapping("/ImUser/Selector/{organizeId}")
    public ActionResult<?> imUserSelector(@PathVariable("organizeId") String organizeIdForm, @RequestBody Pagination pagination) {
        String organizeId = XSSEscape.escape(organizeIdForm);
        List<UserSelectorVO> jsonToList = new ArrayList<>();
        //判断是否搜索关键字
        if (StringUtil.isNotEmpty(pagination.getKeyword())) {
            //通过关键字查询
            List<UserEntity> list = userService.getList(pagination, false);
            //遍历用户给要返回的值插入值
            for (UserEntity entity : list) {
                UserSelectorVO vo = JsonUtil.getJsonToBean(entity, UserSelectorVO.class);
                vo.setParentId(entity.getOrganizeId());
                vo.setFullName(entity.getRealName() + "/" + entity.getAccount());
                vo.setType("user");
                vo.setIcon("icon-ym icon-ym-tree-user2");
                vo.setHeadIcon(null);
                List<UserRelationEntity> listByUserId = userRelationService.getListByUserId(entity.getId()).stream().filter(t -> t != null && PermissionConst.ORGANIZE.equals(t.getObjectType())).collect(Collectors.toList());
                StringBuilder stringBuilder = new StringBuilder();
                listByUserId.forEach(t -> {
                    OrganizeEntity organizeEntity = organizeService.getInfo(t.getObjectId());
                    if (organizeEntity != null) {
                        String fullNameByOrgIdTree = organizeService.getFullNameByOrgIdTree(organizeEntity.getOrganizeIdTree(), "/");
                        if (StringUtil.isNotEmpty(fullNameByOrgIdTree)) {
                            stringBuilder.append("," + fullNameByOrgIdTree);
                        }
                    }
                });
                if (stringBuilder.length() > 0) {
                    vo.setOrganize(stringBuilder.toString().replaceFirst(",", ""));
                }
                vo.setHasChildren(false);
                vo.setIsLeaf(true);
                jsonToList.add(vo);
            }
            PaginationVO jsonToBean = JsonUtil.getJsonToBean(pagination, PaginationVO.class);
            return ActionResult.page(jsonToList, jsonToBean);
        }
        //获取所有组织
        List<OrganizeEntity> collect = organizeService.getList().stream().filter(t -> t.getEnabledMark() == 1).collect(Collectors.toList());
        //判断时候传入组织id
        //如果传入组织id，则取出对应的子集
        if (!"0".equals(organizeId)) {
            //通过组织查询部门及人员
            //单个组织
            List<OrganizeEntity> list = collect.stream().filter(t -> organizeId.equals(t.getId())).collect(Collectors.toList());
            if (list.size() > 0) {
                //获取组织信息
                OrganizeEntity organizeEntity = list.get(0);
                //取出组织下的部门
                List<OrganizeEntity> collect1 = collect.stream().filter(t -> t.getParentId().equals(organizeEntity.getId())).collect(Collectors.toList());
                for (OrganizeEntity entitys : collect1) {
                    UserSelectorVO vo = JsonUtil.getJsonToBean(entitys, UserSelectorVO.class);
                    if ("department".equals(entitys.getCategory())) {
                        vo.setIcon("icon-ym icon-ym-tree-department1");
                    } else if ("company".equals(entitys.getCategory())) {
                        vo.setIcon("icon-ym icon-ym-tree-organization3");
                    }
                    vo.setOrganize(organizeService.getFullNameByOrgIdTree(entitys.getOrganizeIdTree(), "/"));
                    // 判断组织下是否有人
                    jsonToList.add(vo);
                    vo.setHasChildren(true);
                    vo.setIsLeaf(false);
                }
                //取出组织下的人员
                List<UserEntity> entityList = userService.getListByOrganizeId(organizeId, null);
                for (UserEntity entity : entityList) {
                    if ("0".equals(String.valueOf(entity.getEnabledMark()))) {
                        continue;
                    }
                    UserSelectorVO vo = JsonUtil.getJsonToBean(entity, UserSelectorVO.class);
                    vo.setParentId(organizeId);
                    vo.setFullName(entity.getRealName() + "/" + entity.getAccount());
                    vo.setType("user");
                    vo.setIcon("icon-ym icon-ym-tree-user2");
                    List<UserRelationEntity> listByUserId = userRelationService.getListByUserId(entity.getId()).stream().filter(t -> t != null && PermissionConst.ORGANIZE.equals(t.getObjectType())).collect(Collectors.toList());
                    StringBuilder stringBuilder = new StringBuilder();
                    listByUserId.forEach(t -> {
                        OrganizeEntity organizeEntity1 = organizeService.getInfo(t.getObjectId());
                        if (organizeEntity1 != null) {
                            String fullNameByOrgIdTree = organizeService.getFullNameByOrgIdTree(organizeEntity1.getOrganizeIdTree(), "/");
                            if (StringUtil.isNotEmpty(fullNameByOrgIdTree)) {
                                stringBuilder.append("," + fullNameByOrgIdTree);
                            }
                        }
                    });
                    if (stringBuilder.length() > 0) {
                        vo.setOrganize(stringBuilder.toString().replaceFirst(",", ""));
                    }
                    vo.setHeadIcon(null);
                    vo.setHasChildren(false);
                    vo.setIsLeaf(true);
                    jsonToList.add(vo);
                }
            }
            ListVO<UserSelectorVO> vo = new ListVO<>();
            vo.setList(jsonToList);
            return ActionResult.success(vo);
        }

        //如果没有组织id，则取出所有组织
        List<OrganizeEntity> organizeEntityList = collect.stream().filter(t -> "-1".equals(t.getParentId())).collect(Collectors.toList());
        List<UserEntity> list = userService.getList(pagination, false);
        jsonToList = JsonUtil.getJsonToList(list, UserSelectorVO.class);
        //添加图标
        ListVO<UserSelectorVO> vo = new ListVO<>();
        vo.setList(jsonToList);
        return ActionResult.success(vo);
    }

    @ApiOperation("获取所有用户下拉框列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizeId", value = "组织id", required = true),
            @ApiImplicitParam(name = "pagination", value = "分页模型", required = true)
    })
    @PostMapping("/ImUser/Selector/SimpleUser/{organizeId}")
    public ActionResult<?> imUserSelectorSimple(@PathVariable("organizeId") String organizeIdForm, @RequestBody Pagination pagination) {
        String organizeId = XSSEscape.escape(organizeIdForm);
        List<UserSelectorVO> jsonToList = new ArrayList<>();
        //通过关键字查询
        List<UserEntity> list = userService.getList(pagination, false);
        //遍历用户给要返回的值插入值
        for (UserEntity entity : list) {
            UserSelectorVO vo = JsonUtil.getJsonToBean(entity, UserSelectorVO.class);
            vo.setParentId(entity.getOrganizeId());
            vo.setFullName(entity.getRealName() + "/" + entity.getAccount());
            vo.setType("user");
            vo.setIcon("icon-ym icon-ym-tree-user2");
            vo.setHeadIcon(null);
            List<UserRelationEntity> listByUserId = userRelationService.getListByUserId(entity.getId()).stream().filter(t -> t != null && PermissionConst.ORGANIZE.equals(t.getObjectType())).collect(Collectors.toList());
            StringBuilder stringBuilder = new StringBuilder();
            listByUserId.forEach(t -> {
                OrganizeEntity organizeEntity = organizeService.getInfo(t.getObjectId());
                if (organizeEntity != null) {
                    String fullNameByOrgIdTree = organizeService.getFullNameByOrgIdTree(organizeEntity.getOrganizeIdTree(), "/");
                    if (StringUtil.isNotEmpty(fullNameByOrgIdTree)) {
                        stringBuilder.append("," + fullNameByOrgIdTree);
                    }
                }
            });
            if (stringBuilder.length() > 0) {
                vo.setOrganize(stringBuilder.toString().replaceFirst(",", ""));
            }
            vo.setHasChildren(false);
            vo.setIsLeaf(true);
            jsonToList.add(vo);
        }
        PaginationVO jsonToBean = JsonUtil.getJsonToBean(pagination, PaginationVO.class);
        return ActionResult.page(jsonToList, jsonToBean);
    }

}
