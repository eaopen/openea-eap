package org.openea.eap.server.controller;

import org.openea.eap.framework.common.pojo.CommonResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.openea.eap.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_IMPLEMENTED;

/**
 * 默认 Controller，解决部分 module 未开启时的 404 提示。
 * 例如说，/bpm/** 路径，工作流
 *
 */
@RestController
public class DefaultController {

//    @RequestMapping("/admin-api/bpm/**")
//    public CommonResult<Boolean> bpm404() {
//        return CommonResult.error(NOT_IMPLEMENTED.getCode(),
//                "[工作流模块 eap-module-bpm - 已禁用][参考 https://doc.iocoder.cn/bpm/ 开启]");
//    }

    @RequestMapping(value = {"/admin-api/report/**"})
    public CommonResult<Boolean> report404() {
        return CommonResult.error(NOT_IMPLEMENTED.getCode(),
                "[报表模块 eap-module-report - 已禁用][参考 https://doc.iocoder.cn/report/ 开启]");
    }

}
