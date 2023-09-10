package org.openea.eap.extj.controller.admin.engine;

import cn.dev33.satoken.annotation.SaCheckPermission;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.base.controller.SuperController;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.util.UserProvider;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import org.openea.eap.extj.engine.entity.FlowTaskEntity;
import org.openea.eap.extj.engine.enums.FlowStatusEnum;
import org.openea.eap.extj.engine.model.flowengine.FlowModel;
import org.openea.eap.extj.engine.service.FlowDynamicService;
import org.openea.eap.extj.engine.service.FlowTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 流程引擎
 *
 * 
 */
@Tag(name = "流程引擎", description = "FlowTask")
@RestController
@RequestMapping("/api/workflow/Engine/FlowTask")
public class FlowTaskController extends SuperController<FlowTaskService, FlowTaskEntity> {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private FlowDynamicService flowDynamicService;

    /**
     * 保存
     *
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "保存")
    @PostMapping
    @Parameters({
            @Parameter(name = "flowModel", description = "流程模型", required = true),
    })
    @SaCheckPermission("workFlow.flowLaunch")
    public ActionResult save(@RequestBody FlowModel flowModel) throws WorkFlowException {
        UserInfo userInfo = userProvider.get();
        flowModel.setUserInfo(userInfo);
        flowDynamicService.batchCreateOrUpdate(flowModel);
        String msg = FlowStatusEnum.save.getMessage().equals(flowModel.getStatus()) ? MsgCode.SU002.get() : MsgCode.SU006.get();
        return ActionResult.success(msg);
    }

    /**
     * 提交
     *
     * @param id        主键
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "提交")
    @PutMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "flowModel", description = "流程模型", required = true),
    })
    @SaCheckPermission("workFlow.flowLaunch")
    public ActionResult submit(@RequestBody FlowModel flowModel, @PathVariable("id") String id) throws WorkFlowException {
        UserInfo userInfo = userProvider.get();
        flowModel.setId(id);
        flowModel.setUserInfo(userInfo);
        flowDynamicService.batchCreateOrUpdate(flowModel);
        String msg = FlowStatusEnum.save.getMessage().equals(flowModel.getStatus()) ? MsgCode.SU002.get() : MsgCode.SU006.get();
        return ActionResult.success(msg);
    }

}
