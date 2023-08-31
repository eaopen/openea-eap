package org.openea.eap.extend.mp.controller.admin.message;

import org.openea.eap.framework.common.pojo.CommonResult;
import org.openea.eap.framework.common.pojo.PageResult;
import org.openea.eap.extend.mp.controller.admin.message.vo.message.MpMessagePageReqVO;
import org.openea.eap.extend.mp.controller.admin.message.vo.message.MpMessageRespVO;
import org.openea.eap.extend.mp.controller.admin.message.vo.message.MpMessageSendReqVO;
import org.openea.eap.extend.mp.convert.message.MpMessageConvert;
import org.openea.eap.extend.mp.dal.dataobject.message.MpMessageDO;
import org.openea.eap.extend.mp.service.message.MpMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static org.openea.eap.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 公众号消息")
@RestController
@RequestMapping("/mp/message")
@Validated
public class MpMessageController {

    @Resource
    private MpMessageService mpMessageService;

    @GetMapping("/page")
    @Operation(summary = "获得公众号消息分页")
    @PreAuthorize("@ss.hasPermission('mp:message:query')")
    public CommonResult<PageResult<MpMessageRespVO>> getMessagePage(@Valid MpMessagePageReqVO pageVO) {
        PageResult<MpMessageDO> pageResult = mpMessageService.getMessagePage(pageVO);
        return success(MpMessageConvert.INSTANCE.convertPage(pageResult));
    }

    @PostMapping("/send")
    @Operation(summary = "给粉丝发送消息")
    @PreAuthorize("@ss.hasPermission('mp:message:send')")
    public CommonResult<MpMessageRespVO> sendMessage(@Valid @RequestBody MpMessageSendReqVO reqVO) {
        MpMessageDO message = mpMessageService.sendKefuMessage(reqVO);
        return success(MpMessageConvert.INSTANCE.convert(message));
    }

}
