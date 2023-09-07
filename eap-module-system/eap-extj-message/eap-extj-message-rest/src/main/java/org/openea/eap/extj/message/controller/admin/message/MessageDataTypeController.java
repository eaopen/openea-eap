package org.openea.eap.extj.message.controller.admin.message;

//import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import org.openea.eap.extj.base.controller.SuperController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.vo.PageListVO;
import org.openea.eap.extj.base.vo.PaginationVO;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.message.entity.MessageDataTypeEntity;
import org.openea.eap.extj.message.service.MessageDataTypeService;
import org.springframework.transaction.annotation.Transactional;
import org.openea.eap.extj.message.model.messagedatatype.*;
import org.openea.eap.extj.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

/**
 * 消息中心类型数据
 *
 */
@Slf4j
@RestController
@Tag(name = "消息中心类型数据", description = "message")
@RequestMapping("/api/message/MessageDataType")
public class MessageDataTypeController extends SuperController<MessageDataTypeService, MessageDataTypeEntity> {


    @Autowired
    private UserProvider userProvider;


    @Autowired
    private MessageDataTypeService messageDataTypeService;


    /**
     * 列表
     *
     * @param messageDataTypePagination 分页模型
     * @return ignore
     */
    @Operation(summary = "列表")
    @Parameters({
            @Parameter(name = "messageDataTypePagination", description = "分页模型")
    })
    //@SaCheckPermission("msgTemplate")
    @PostMapping("/getList")
    public ActionResult<PageListVO<MessageDataTypeListVO>> list(@RequestBody MessageDataTypePagination messageDataTypePagination) throws IOException {
        List<MessageDataTypeEntity> list = messageDataTypeService.getList(messageDataTypePagination);
        //处理id字段转名称，若无需转或者为空可删除

        List<MessageDataTypeListVO> listVO = JsonUtil.getJsonToList(list, MessageDataTypeListVO.class);
        for (MessageDataTypeListVO messageDataTypeVO : listVO) {

            //子表数据转换
        }

        PageListVO vo = new PageListVO();
        vo.setList(listVO);
        PaginationVO page = JsonUtil.getJsonToBean(messageDataTypePagination, PaginationVO.class);
        vo.setPagination(page);
        return ActionResult.success(vo);

    }

    /**
     * 获取数据
     *
     * @param type 类型
     * @return ignore
     */
    @Operation(summary = "获取数据")
    @Parameters({
            @Parameter(name = "type", description = "类型", required = true)
    })
    @GetMapping("/getTypeList/{type}")
    public ActionResult<List<MessageDataTypeEntity>> getList(@PathVariable("type") List<String> type){
        List<MessageDataTypeEntity> list = messageDataTypeService.getListByType(type);
        if(list != null && list.size()>0) {
            return ActionResult.success(list);
        }else {
            return ActionResult.fail("该类型数据为空");
        }
    }

    /**
     * 创建
     *
     * @param messageDataTypeForm 消息数据分页模型
     * @return
     */
    @Operation(summary = "创建")
    @Parameters({
            @Parameter(name = "messageDataTypeForm", description = "消息数据分类模型", required = true)
    })
    //@SaCheckPermission("msgTemplate")
    @PostMapping
    @Transactional
    public ActionResult create(@RequestBody @Valid MessageDataTypeForm messageDataTypeForm) throws DataException {
        boolean b = messageDataTypeService.checkForm(messageDataTypeForm, 0);
        if (b) {
            return ActionResult.fail("单行输入不能重复");
        }
        String mainId = RandomUtil.uuId();
        UserInfo userInfo = userProvider.get();
        messageDataTypeForm.setCreatorTime(DateUtil.getNow());
        messageDataTypeForm.setCreatorUserId(userInfo.getUserId());
        MessageDataTypeEntity entity = JsonUtil.getJsonToBean(messageDataTypeForm, MessageDataTypeEntity.class);
        entity.setId(mainId);
        messageDataTypeService.save(entity);


        return ActionResult.success("创建成功");
    }

    /**
     * 信息
     *
     * @param id 主键
     * @return ignore
     */
    @Operation(summary = "信息")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    //@SaCheckPermission("msgTemplate")
    @GetMapping("/{id}")
    public ActionResult<MessageDataTypeInfoVO> info(@PathVariable("id") String id) {
        MessageDataTypeEntity entity = messageDataTypeService.getInfo(id);
        MessageDataTypeInfoVO vo = JsonUtil.getJsonToBean(entity, MessageDataTypeInfoVO.class);
        if (vo.getCreatortime() != null) {
            vo.setCreatortime(vo.getCreatortime());
        }
        if (vo.getLastmodifytime() != null) {
            vo.setLastmodifytime(vo.getLastmodifytime());
        }

        //子表
        //副表
        return ActionResult.success(vo);
    }

    /**
     * 表单信息(详情页)
     *
     * @param id 主键
     * @return ignore
     */
    @Operation(summary = "表单信息(详情页)")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    //@SaCheckPermission("msgTemplate")
    @GetMapping("/detail/{id}")
    public ActionResult<MessageDataTypeInfoVO> detailInfo(@PathVariable("id") String id) {
        MessageDataTypeEntity entity = messageDataTypeService.getInfo(id);
        MessageDataTypeInfoVO vo = JsonUtil.getJsonToBean(entity, MessageDataTypeInfoVO.class);

        //子表数据转换

        //附表数据转换

        //添加到详情表单对象中
        vo.setType(vo.getType());

        vo.setFullName(vo.getFullName());

        vo.setEnCode(vo.getEnCode());

        vo.setCreatortime(vo.getCreatortime());

        vo.setLastmodifytime(vo.getLastmodifytime());

        return ActionResult.success(vo);
    }


    /**
     * 更新
     *
     * @param id 主键
     * @param messageDataTypeForm 消息数据分类模型
     * @return ignore
     */
    @Operation(summary = "更新")
    @PutMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "messageDataTypeForm", description = "消息数据分类模型", required = true)
    })
    //@SaCheckPermission("msgTemplate")
    @Transactional
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid MessageDataTypeForm messageDataTypeForm) throws DataException {

        boolean b = messageDataTypeService.checkForm(messageDataTypeForm, 1);
        if (b) {
            return ActionResult.fail("单行输入不能重复");
        }
        UserInfo userInfo = userProvider.get();
        MessageDataTypeEntity entity = messageDataTypeService.getInfo(id);
        if (entity != null) {
            messageDataTypeForm.setLastModifyTime(DateUtil.getNow());
            messageDataTypeForm.setLastModifyUserId(userInfo.getUserId());
            MessageDataTypeEntity subentity = JsonUtil.getJsonToBean(messageDataTypeForm, MessageDataTypeEntity.class);
            subentity.setCreatorTime(entity.getCreatorTime());
            subentity.setCreatorUserId(entity.getCreatorUserId());
            boolean b1 = messageDataTypeService.updateById(subentity);
            if (!b1) {
                return ActionResult.fail("当前表单原数据已被调整，请重新进入该页面编辑并提交数据");
            }
            return ActionResult.success("更新成功");
        } else {
            return ActionResult.fail("更新失败，数据不存在");
        }
    }

    /**
     * 删除
     *
     * @param id 主键
     * @return ignore
     */
    @Operation(summary = "删除")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    //@SaCheckPermission("msgTemplate")
    @DeleteMapping("/{id}")
    @Transactional
    public ActionResult delete(@PathVariable("id") String id) {
        MessageDataTypeEntity entity = messageDataTypeService.getInfo(id);
        if (entity != null) {
            messageDataTypeService.delete(entity);

        }
        return ActionResult.success("删除成功");
    }


}
