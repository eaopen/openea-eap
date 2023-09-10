package org.openea.eap.extj.controller.admin.engine;

import cn.dev33.satoken.annotation.SaCheckPermission;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.base.vo.PageListVO;
import org.openea.eap.extj.base.vo.PaginationVO;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.UserProvider;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import org.openea.eap.extj.engine.entity.FlowTaskEntity;
import org.openea.eap.extj.engine.model.flowengine.FlowModel;
import org.openea.eap.extj.engine.model.flowlaunch.FlowLaunchListVO;
import org.openea.eap.extj.engine.model.flowtask.PaginationFlowTask;
import org.openea.eap.extj.engine.service.FlowTaskNewService;
import org.openea.eap.extj.engine.service.FlowTaskService;
import org.openea.eap.extj.engine.util.FlowNature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 流程发起
 *
 * 
 */
@Tag(name = "流程发起", description = "FlowLaunch")
@RestController
@RequestMapping("/api/workflow/Engine/FlowLaunch")
public class FlowLaunchController {

    @Autowired
    private FlowTaskService flowTaskService;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private FlowTaskNewService flowTaskNewService;

    /**
     * 获取流程发起列表
     *
     * @param paginationFlowTask 分页模型
     * @return
     */
    @Operation(summary = "获取流程发起列表(带分页)")
    @GetMapping
    @SaCheckPermission("workFlow.flowLaunch")
    public ActionResult<PageListVO<FlowLaunchListVO>> list(PaginationFlowTask paginationFlowTask) {
        List<FlowTaskEntity> list = flowTaskService.getLaunchList(paginationFlowTask);
        List<FlowLaunchListVO> listVO = JsonUtil.getJsonToList(list, FlowLaunchListVO.class);
        PaginationVO paginationVO = JsonUtil.getJsonToBean(paginationFlowTask, PaginationVO.class);
        return ActionResult.page(listVO, paginationVO);
    }

    /**
     * 删除流程发起
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "删除流程发起")
    @DeleteMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    @SaCheckPermission("workFlow.flowLaunch")
    public ActionResult delete(@PathVariable("id") String id) throws WorkFlowException {
        FlowTaskEntity entity = flowTaskService.getInfo(id);
        if (entity != null) {
            if (entity.getFlowType() == 1) {
                return ActionResult.fail("功能流程不能删除");
            }
            if (!FlowNature.ParentId.equals(entity.getParentId()) && StringUtil.isNotEmpty(entity.getParentId())) {
                return ActionResult.fail(entity.getFullName() + "不能删除");
            }
            flowTaskService.delete(entity);
            return ActionResult.success(MsgCode.SU003.get());
        }
        return ActionResult.fail(MsgCode.FA003.get());
    }

    /**
     * 待我审核催办
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "发起催办")
    @PostMapping("/Press/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    @SaCheckPermission("workFlow.flowLaunch")
    public ActionResult press(@PathVariable("id") String id) throws WorkFlowException {
        FlowModel flowModel = new FlowModel();
        UserInfo userInfo = userProvider.get();
        flowModel.setUserInfo(userInfo);
        boolean flag = flowTaskNewService.press(id, flowModel);
        if (flag) {
            return ActionResult.success("催办成功");
        }
        return ActionResult.fail("未找到催办人");
    }

    /**
     * 撤回流程发起
     * 注意：在撤销流程时要保证你的下一节点没有处理这条记录；如已处理则无法撤销流程。
     *
     * @param id        主键
     * @param flowModel 流程模型
     * @return
     */
    @Operation(summary = "撤回流程发起")
    @PutMapping("/{id}/Actions/Withdraw")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "flowModel", description = "流程模型", required = true),
    })
    @SaCheckPermission("workFlow.flowLaunch")
    public ActionResult revoke(@PathVariable("id") String id, @RequestBody FlowModel flowModel) throws WorkFlowException {
        FlowTaskEntity flowTaskEntity = flowTaskService.getInfo(id);
        if (StringUtil.isNotEmpty(flowTaskEntity.getParentId()) && !"0".equals(flowTaskEntity.getParentId())) {
            return ActionResult.fail("子流程不能撤回！");
        }
        UserInfo userInfo = userProvider.get();
        flowModel.setUserInfo(userInfo);
        flowTaskNewService.revoke(flowTaskEntity, flowModel);
        return ActionResult.success("撤回成功");
    }
}
