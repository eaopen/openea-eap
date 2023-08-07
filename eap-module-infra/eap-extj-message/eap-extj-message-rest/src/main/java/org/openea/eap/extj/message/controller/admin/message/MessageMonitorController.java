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
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.message.entity.MessageDataTypeEntity;
import org.openea.eap.extj.message.entity.MessageMonitorEntity;
import org.openea.eap.extj.message.model.messagemonitor.*;
import org.openea.eap.extj.message.service.MessageDataTypeService;
import org.openea.eap.extj.message.service.MessageMonitorService;
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
 * 消息监控
 *
 *
 */
@Slf4j
@RestController
@Tag(name = "消息监控", description = "message")
@RequestMapping("/api/message/MessageMonitor")
public class MessageMonitorController extends SuperController<MessageMonitorService, MessageMonitorEntity> {


    @Autowired
    private UserProvider userProvider;


    @Autowired
    private MessageMonitorService messageMonitorService;
    @Autowired
    private MessageDataTypeService messageDataTypeService;


    /**
     * 列表
     *
     * @param messageMonitorPagination 消息监控分页模型
     * @return ignore
     */
    @Operation(summary = "列表")
    //@SaCheckPermission("msgCenter.msgMonitor")
    @GetMapping
    public ActionResult<PageListVO<MessageMonitorListVO>> list(MessageMonitorPagination messageMonitorPagination) throws IOException {
        List<MessageMonitorEntity> list = messageMonitorService.getList(messageMonitorPagination);
        //处理id字段转名称，若无需转或者为空可删除

        List<MessageMonitorListVO> listVO = JsonUtil.getJsonToList(list, MessageMonitorListVO.class);
        for (MessageMonitorListVO messageMonitorVO : listVO) {

            //消息类型
            if(StringUtil.isNotBlank(messageMonitorVO.getMessageType())){
                MessageDataTypeEntity dataTypeEntity = messageDataTypeService.getInfo("1",messageMonitorVO.getMessageType());
                if(dataTypeEntity != null){
                    messageMonitorVO.setMessageType(dataTypeEntity.getFullName());
                }
            }
            //消息来源
            if(StringUtil.isNotBlank(messageMonitorVO.getMessageSource())) {
                MessageDataTypeEntity dataTypeEntity1 = messageDataTypeService.getInfo("4", messageMonitorVO.getMessageSource());
                if (dataTypeEntity1 != null) {
                    messageMonitorVO.setMessageSource(dataTypeEntity1.getFullName());
                }
            }
            //子表数据转换
        }

        PageListVO vo = new PageListVO();
        vo.setList(listVO);
        PaginationVO page = JsonUtil.getJsonToBean(messageMonitorPagination, PaginationVO.class);
        vo.setPagination(page);
        return ActionResult.success(vo);

    }

    /**
     * 创建
     *
     * @param messageMonitorForm 消息监控模型
     * @return ignore
     */
    @Operation(summary = ("创建"))
    @PostMapping
    @Parameters({
            @Parameter(name = "messageMonitorForm", description = "消息监控模型", required = true)
    })
    //@SaCheckPermission("msgCenter.msgMonitor")
    @Transactional
    public ActionResult create(@RequestBody @Valid MessageMonitorForm messageMonitorForm) throws DataException {
        String mainId = RandomUtil.uuId();
        UserInfo userInfo = userProvider.get();
        MessageMonitorEntity entity = JsonUtil.getJsonToBean(messageMonitorForm, MessageMonitorEntity.class);
        entity.setCreatorTime(DateUtil.getNowDate());
        entity.setCreatorUserId(userInfo.getUserId());
        entity.setId(mainId);
        messageMonitorService.save(entity);

        return ActionResult.success("创建成功");
    }


    /**
     * 批量删除
     *
     * @param msgDelForm 消息删除模型
     * @return ignore
     */
    @Operation(summary = ("批量删除"))
    @DeleteMapping("/batchRemove")
    @Parameters({
            @Parameter(name = "msgDelForm", description = "消息删除模型", required = true)
    })
    //@SaCheckPermission("msgCenter.msgMonitor")
    @Transactional
    public ActionResult batchRemove(@RequestBody MsgDelForm msgDelForm) {
        boolean flag = messageMonitorService.delete(msgDelForm.getIds());
        if (flag == false) {
            return ActionResult.fail("删除失败");
        }
        return ActionResult.success("删除成功");
    }


    /**
     * 一键清空消息监控记录
     *
     * @return
     */
    @Operation(summary = "一键清空消息监控记录")
    //@SaCheckPermission("msgCenter.msgMonitor")
    @DeleteMapping("/empty")
    public ActionResult deleteHandelLog() {
        messageMonitorService.emptyMonitor();
        return ActionResult.success(MsgCode.SU005.get());
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
    //@SaCheckPermission("msgCenter.msgMonitor")
    @GetMapping("/{id}")
    public ActionResult<MessageMonitorInfoVO> info(@PathVariable("id") String id) {
        MessageMonitorEntity entity = messageMonitorService.getInfo(id);
        MessageMonitorInfoVO vo = JsonUtil.getJsonToBean(entity, MessageMonitorInfoVO.class);

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
    //@SaCheckPermission("msgCenter.msgMonitor")
    @GetMapping("/detail/{id}")
    public ActionResult<MessageMonitorInfoVO> detailInfo(@PathVariable("id") String id) {
        MessageMonitorEntity entity = messageMonitorService.getInfo(id);
        MessageMonitorInfoVO vo = JsonUtil.getJsonToBean(entity, MessageMonitorInfoVO.class);
        if(StringUtil.isNotBlank(vo.getMessageType())){
            MessageDataTypeEntity dataTypeEntity = messageDataTypeService.getInfo("1",vo.getMessageType());
            if(dataTypeEntity != null){
                vo.setMessageType(dataTypeEntity.getFullName());
            }
            MessageDataTypeEntity dataTypeEntity1 = messageDataTypeService.getInfo("4",vo.getMessageSource());
            if(dataTypeEntity1 != null){
                vo.setMessageSource(dataTypeEntity1.getFullName());
            }
        }
        if(!"webhook".equals(vo.getMessageType())) {
            vo.setReceiveUser(messageMonitorService.userSelectValues(vo.getReceiveUser()));
        }
        return ActionResult.success(vo);
    }

    /**
     * 删除
     *
     * @param id 主键
     * @return ignore
     */
    @DeleteMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    //@SaCheckPermission("msgCenter.msgMonitor")
    @Transactional
    public ActionResult delete(@PathVariable("id") String id) {
        MessageMonitorEntity entity = messageMonitorService.getInfo(id);
        if (entity != null) {
            messageMonitorService.delete(entity);

        }
        return ActionResult.success("删除成功");
    }


}
