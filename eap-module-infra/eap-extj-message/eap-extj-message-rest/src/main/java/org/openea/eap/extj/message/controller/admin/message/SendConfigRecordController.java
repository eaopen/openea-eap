


package org.openea.eap.extj.message.controller.admin.message;

//import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import org.openea.eap.extj.base.controller.SuperController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.vo.PageListVO;
import org.openea.eap.extj.base.vo.PaginationVO;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.message.entity.SendConfigRecordEntity;
import org.openea.eap.extj.message.model.sendconfigrecord.*;
import org.openea.eap.extj.message.service.SendConfigRecordService;
import org.springframework.transaction.annotation.Transactional;
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
 * 发送配置使用记录
 *
 *
 */
@Slf4j
@RestController
@Tag(name = "发送配置使用记录", description = "message")
@RequestMapping("/api/message/SendConfigRecord")
public class SendConfigRecordController extends SuperController<SendConfigRecordService, SendConfigRecordEntity> {


    @Autowired
    private UserProvider userProvider;


    @Autowired
    private SendConfigRecordService sendConfigRecordService;


    /**
     * 列表
     *
     * @param sendConfigRecordPagination 分页模型
     * @return ignore
     */
    @Operation(summary = "列表")
    @Parameters({
            @Parameter(name = "sendConfigRecordPagination", description = "分页模型", required = true)
    })
    //@SaCheckPermission("msgCenter.sendConfig")
    @PostMapping("/getList")
    public ActionResult<PageListVO<SendConfigRecordListVO>> list(@RequestBody SendConfigRecordPagination sendConfigRecordPagination) throws IOException {
        List<SendConfigRecordEntity> list = sendConfigRecordService.getList(sendConfigRecordPagination);
        //处理id字段转名称，若无需转或者为空可删除

        List<SendConfigRecordListVO> listVO = JsonUtil.getJsonToList(list, SendConfigRecordListVO.class);
        for (SendConfigRecordListVO sendConfigRecordVO : listVO) {

        }

        PageListVO vo = new PageListVO();
        vo.setList(listVO);
        PaginationVO page = JsonUtil.getJsonToBean(sendConfigRecordPagination, PaginationVO.class);
        vo.setPagination(page);
        return ActionResult.success(vo);

    }

    /**
     * 信息
     *
     * @param sendConfigRecordForm 发送配置模型
     * @return
     */
    @Operation(summary = "信息")
    @Parameters({
            @Parameter(name = "sendConfigRecordForm", description = "发送配置模型", required = true)
    })
    //@SaCheckPermission("msgCenter.sendConfig")
    @PostMapping
    @Transactional
    public ActionResult create(@RequestBody @Valid SendConfigRecordForm sendConfigRecordForm) throws DataException {
        boolean b = sendConfigRecordService.checkForm(sendConfigRecordForm, 0);
        if (b) {
            return ActionResult.fail("单行输入不能重复");
        }
        String mainId = RandomUtil.uuId();
        UserInfo userInfo = userProvider.get();
        SendConfigRecordEntity entity = JsonUtil.getJsonToBean(sendConfigRecordForm, SendConfigRecordEntity.class);
        entity.setId(mainId);
        sendConfigRecordService.save(entity);


        return ActionResult.success("创建成功");
    }


    /**
     * 详情
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "详情")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    //@SaCheckPermission("msgCenter.sendConfig")
    @GetMapping("/{id}")
    public ActionResult<SendConfigRecordInfoVO> info(@PathVariable("id") String id) {
        SendConfigRecordEntity entity = sendConfigRecordService.getInfo(id);
        SendConfigRecordInfoVO vo = JsonUtil.getJsonToBean(entity, SendConfigRecordInfoVO.class);

        //子表
        //副表
        return ActionResult.success(vo);
    }

    /**
     * 表单信息(详情页)
     *
     * @param id
     * @return
     */
    @Operation(summary = "表单信息(详情页)")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    //@SaCheckPermission("msgCenter.sendConfig")
    @GetMapping("/detail/{id}")
    public ActionResult<SendConfigRecordInfoVO> detailInfo(@PathVariable("id") String id) {
        return info(id);
    }


    /**
     * 更新
     *
     * @param id 主键
     * @param sendConfigRecordForm 消息模板页模型
     * @return
     */
    @Operation(summary = "更新")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "sendConfigRecordForm", description = "发送配置模型", required = true)
    })
    //@SaCheckPermission("msgCenter.sendConfig")
    @PutMapping("/{id}")
    @Transactional
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid SendConfigRecordForm sendConfigRecordForm) throws DataException {

        boolean b = sendConfigRecordService.checkForm(sendConfigRecordForm, 1);
        if (b) {
            return ActionResult.fail("单行输入不能重复");
        }
        UserInfo userInfo = userProvider.get();
        SendConfigRecordEntity entity = sendConfigRecordService.getInfo(id);
        if (entity != null) {
            SendConfigRecordEntity subentity = JsonUtil.getJsonToBean(sendConfigRecordForm, SendConfigRecordEntity.class);
            boolean b1 = sendConfigRecordService.updateById(subentity);
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
     * @return
     */
    @Operation(summary = "删除")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    //@SaCheckPermission("msgCenter.sendConfig")
    @DeleteMapping("/{id}")
    @Transactional
    public ActionResult delete(@PathVariable("id") String id) {
        SendConfigRecordEntity entity = sendConfigRecordService.getInfo(id);
        if (entity != null) {
            sendConfigRecordService.delete(entity);

        }
        return ActionResult.success("删除成功");
    }


}
