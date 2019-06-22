package org.openea.eapboot.modules.activiti.controller.business;

import org.openea.eapboot.base.EapBaseController;
import org.openea.eapboot.common.utils.ResultUtil;
import org.openea.eapboot.common.utils.SecurityUtil;
import org.openea.eapboot.common.vo.Result;
import org.openea.eapboot.modules.activiti.entity.ActBusiness;
import org.openea.eapboot.modules.activiti.entity.business.Leave;
import org.openea.eapboot.modules.activiti.service.ActBusinessService;
import org.openea.eapboot.modules.activiti.service.business.LeaveService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 */
@Slf4j
@RestController
@Api(description = "请假申请接口")
@Transactional
@RequestMapping(value = "/eapboot/leave")
public class LeaveController extends EapBaseController<Leave, String> {

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private ActBusinessService actBusinessService;

    @Autowired
    private SecurityUtil securityUtil;

    @Override
    public LeaveService getService() {
        return leaveService;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "添加申请草稿状态")
    public Result<Object> add(@ModelAttribute Leave leave,
                              @RequestParam String procDefId){

        Leave le = leaveService.save(leave);
        // 保存至我的申请业务
        String userId = securityUtil.getCurrUser().getId();
        ActBusiness actBusiness = new ActBusiness();
        actBusiness.setUserId(userId);
        actBusiness.setTableId(le.getId());
        actBusiness.setProcDefId(procDefId);
        actBusiness.setTitle(leave.getTitle());
        actBusinessService.save(actBusiness);
        return new ResultUtil<Object>().setData(null);
    }
}
