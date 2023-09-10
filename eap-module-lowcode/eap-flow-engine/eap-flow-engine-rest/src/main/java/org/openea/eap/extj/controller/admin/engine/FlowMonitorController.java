package org.openea.eap.extj.controller.admin.engine;

import cn.dev33.satoken.annotation.SaCheckPermission;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.vo.PageListVO;
import org.openea.eap.extj.base.vo.PaginationVO;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.permission.entity.UserEntity;
import org.openea.eap.extj.util.JsonUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import org.openea.eap.extj.engine.entity.FlowTaskEntity;
import org.openea.eap.extj.engine.model.flowmonitor.FlowMonitorListVO;
import org.openea.eap.extj.engine.model.flowtask.FlowAssistModel;
import org.openea.eap.extj.engine.model.flowtask.PaginationFlowTask;
import org.openea.eap.extj.engine.service.FlowTaskService;
import org.openea.eap.extj.util.ServiceAllUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 流程监控
 *
 * 
 */
@Tag(name = "流程监控", description = "FlowMonitor")
@RestController
@RequestMapping("/api/workflow/Engine/FlowMonitor")
public class FlowMonitorController {

    @Autowired
    private FlowTaskService flowTaskService;
    @Autowired
    private ServiceAllUtil serviceUtil;

    /**
     * 获取流程监控列表
     *
     * @param paginationFlowTask 分页模型
     * @return
     */
    @Operation(summary = "获取流程监控列表")
    @GetMapping
    @SaCheckPermission("workFlow.flowMonitor")
    public ActionResult<PageListVO<FlowMonitorListVO>> list(PaginationFlowTask paginationFlowTask) {
        List<FlowTaskEntity> list = flowTaskService.getMonitorList(paginationFlowTask);
        List<UserEntity> userList = serviceUtil.getUserName(list.stream().map(t -> t.getCreatorUserId()).collect(Collectors.toList()));
        List<FlowMonitorListVO> listVO = new LinkedList<>();
        for (FlowTaskEntity taskEntity : list) {
            //用户名称赋值
            FlowMonitorListVO vo = JsonUtil.getJsonToBean(taskEntity, FlowMonitorListVO.class);
            UserEntity user = userList.stream().filter(t -> t.getId().equals(taskEntity.getCreatorUserId())).findFirst().orElse(null);
            vo.setUserName(user != null ? user.getRealName() + "/" + user.getAccount() : "");
            listVO.add(vo);
        }
        PaginationVO paginationVO = JsonUtil.getJsonToBean(paginationFlowTask, PaginationVO.class);
        return ActionResult.page(listVO, paginationVO);
    }

    /**
     * 批量删除流程监控
     *
     * @param assistModel 流程删除模型
     * @return
     */
    @Operation(summary = "批量删除流程监控")
    @DeleteMapping
    @Parameters({
            @Parameter(name = "assistModel", description = "流程删除模型", required = true),
    })
    @SaCheckPermission("workFlow.flowMonitor")
    public ActionResult delete(@RequestBody FlowAssistModel assistModel) throws WorkFlowException {
        String[] taskId = assistModel.getIds().split(",");
        flowTaskService.delete(taskId);
        return ActionResult.success(MsgCode.SU003.get());
    }

}
