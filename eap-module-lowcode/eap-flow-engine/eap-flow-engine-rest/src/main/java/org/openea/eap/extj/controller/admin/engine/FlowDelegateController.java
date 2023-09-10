package org.openea.eap.extj.controller.admin.engine;

import cn.dev33.satoken.annotation.SaCheckPermission;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.controller.SuperController;
import org.openea.eap.extj.base.vo.ListVO;
import org.openea.eap.extj.base.vo.PageListVO;
import org.openea.eap.extj.base.vo.PaginationVO;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.engine.model.flowdelegate.*;
import org.openea.eap.extj.engine.model.flowdelegate.*;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.StringUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import org.openea.eap.extj.engine.entity.FlowDelegateEntity;
import org.openea.eap.extj.engine.model.flowcandidate.FlowCandidateUserModel;
import org.openea.eap.extj.engine.model.flowdelegate.*;
import org.openea.eap.extj.engine.model.flowengine.FlowPagination;
import org.openea.eap.extj.engine.model.flowtemplate.FlowPageListVO;
import org.openea.eap.extj.engine.service.FlowDelegateService;
import org.openea.eap.extj.util.JsonUtilEx;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 流程委托
 *
 * 
 */
@Tag(name = "流程委托", description = "FlowDelegate")
@RestController
@RequestMapping("/api/workflow/Engine/FlowDelegate")
public class FlowDelegateController extends SuperController<FlowDelegateService, FlowDelegateEntity> {

    @Autowired
    private FlowDelegateService flowDelegateService;

    /**
     * 获取流程委托列表
     *
     * @param pagination 分页模型
     * @return
     */
    @Operation(summary = "获取流程委托列表")
    @GetMapping
    @SaCheckPermission("workFlow.entrust")
    public ActionResult<PageListVO<FlowDelegatListVO>> list(FlowDelegatePagination pagination) {
        List<FlowDelegateEntity> list = flowDelegateService.getList(pagination);
        PaginationVO paginationVO = JsonUtil.getJsonToBean(pagination, PaginationVO.class);
        List<FlowDelegatListVO> listVO = JsonUtil.getJsonToList(list, FlowDelegatListVO.class);
        return ActionResult.page(listVO, paginationVO);
    }

    /**
     * 获取流程委托信息
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "获取流程委托信息")
    @GetMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    @SaCheckPermission("workFlow.entrust")
    public ActionResult<FlowDelegateInfoVO> info(@PathVariable("id") String id) throws DataException {
        FlowDelegateEntity entity = flowDelegateService.getInfo(id);
        FlowDelegateInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, FlowDelegateInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建流程委托
     *
     * @param flowDelegateCrForm 委托模型
     * @return
     */
    @Operation(summary = "新建流程委托")
    @PostMapping
    @Parameters({
            @Parameter(name = "flowDelegateCrForm", description = "委托模型",required = true),
    })
    @SaCheckPermission("workFlow.entrust")
    public ActionResult create(@RequestBody @Valid FlowDelegateCrForm flowDelegateCrForm) {
        FlowDelegateEntity entity = JsonUtil.getJsonToBean(flowDelegateCrForm, FlowDelegateEntity.class);
        if (entity.getUserId().equals(entity.getToUserId())) {
            return ActionResult.fail("委托人和被委托人相同，委托失败！");
        }
        if (alreadyDelegate(flowDelegateCrForm, null)) {
            return ActionResult.fail("操作失败，同一时间内有相同流程的委托！");
        }
        FlowDelegateCrForm flowReverse = new FlowDelegateCrForm();
        BeanUtils.copyProperties(flowDelegateCrForm, flowReverse);
        flowReverse.setUserId(flowDelegateCrForm.getToUserId());
        flowReverse.setToUserId(flowDelegateCrForm.getUserId());
        if (alreadyDelegate(flowReverse, null)) {
            return ActionResult.fail("操作失败，同一时间内有相同流程，不能相互委托！");
        }
        flowDelegateService.create(entity);
        return ActionResult.success(MsgCode.SU001.get());
    }

    /**
     * 判断是否已有委托
     *
     * @param
     * @return
     */
    private boolean alreadyDelegate(FlowDelegateCrForm flowDelegateCrForm, String id) {
        List<FlowDelegateEntity> flowDelegateEntities = flowDelegateService.selectSameParamAboutDelaget(flowDelegateCrForm);
        for (FlowDelegateEntity delegate : flowDelegateEntities) {
            if (delegate.getId().equals(id)) {
                continue;
            }
            //时间交叉
            if ((flowDelegateCrForm.getStartTime() <= delegate.getStartTime().getTime() && flowDelegateCrForm.getEndTime() >= delegate.getStartTime().getTime()) ||
                    (flowDelegateCrForm.getStartTime() >= delegate.getStartTime().getTime() && flowDelegateCrForm.getStartTime() <= delegate.getEndTime().getTime())) {
                if (StringUtil.isEmpty(flowDelegateCrForm.getFlowId())) {
                    return true;
                } else {
                    if (StringUtil.isEmpty(delegate.getFlowId())) {
                        return true;
                    } else {
                        List<String> split = Arrays.asList(delegate.getFlowId().split(","));
                        List<String> split1 = Arrays.asList(flowDelegateCrForm.getFlowId().split(","));
                        for (String srt : split) {
                            if (split1.contains(srt)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 更新流程委托
     *
     * @param id                 主键值
     * @param flowDelegateUpForm 委托模型
     * @return
     */
    @Operation(summary = "更新流程委托")
    @PutMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "flowDelegateUpForm", description = "委托模型",required = true),
    })
    @SaCheckPermission("workFlow.entrust")
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid FlowDelegateUpForm flowDelegateUpForm) {
        FlowDelegateEntity entity = JsonUtil.getJsonToBean(flowDelegateUpForm, FlowDelegateEntity.class);
        if (entity.getUserId().equals(entity.getToUserId())) {
            return ActionResult.fail("委托人和被委托人相同，委托失败");
        }

        if (alreadyDelegate(flowDelegateUpForm, id)) {
            return ActionResult.fail("操作失败，同一时间内有相同流程的委托！");
        }
        FlowDelegateCrForm flowReverse = new FlowDelegateCrForm();
        BeanUtils.copyProperties(flowDelegateUpForm, flowReverse);
        flowReverse.setUserId(flowDelegateUpForm.getToUserId());
        flowReverse.setToUserId(flowDelegateUpForm.getUserId());
        if (alreadyDelegate(flowReverse, id)) {
            return ActionResult.fail("操作失败，同一时间内有相同流程，不能相互委托！");
        }
        boolean flag = flowDelegateService.update(id, entity);
        if (flag == false) {
            return ActionResult.success(MsgCode.FA002.get());
        }
        return ActionResult.success(MsgCode.SU004.get());
    }

    /**
     * 停止流程引擎
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "结束委托")
    @PutMapping("/Stop/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    @SaCheckPermission("workFlow.entrust")
    public ActionResult stop(@PathVariable("id") String id) {
        FlowDelegateEntity entity = flowDelegateService.getInfo(id);
        if (entity != null) {
            Date date = new Date();
            entity.setStartTime(date);
            entity.setEndTime(date);
            flowDelegateService.updateStop(id, entity);
            return ActionResult.success(MsgCode.SU008.get());
        }
        return ActionResult.fail(MsgCode.FA002.get());
    }

    /**
     * 删除流程委托
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "删除流程委托")
    @DeleteMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键",required = true),
    })
    @SaCheckPermission("workFlow.entrust")
    public ActionResult delete(@PathVariable("id") String id) {
        FlowDelegateEntity entity = flowDelegateService.getInfo(id);
        if (entity != null) {
            flowDelegateService.delete(entity);
            return ActionResult.success(MsgCode.SU003.get());
        }
        return ActionResult.fail(MsgCode.FA003.get());
    }


    /**
     * 被委托审核
     * 根据流程id和委托人id=查询被委托人
     *
     * @param flowId   流程主键
     * @param userId   用户主键
     * @param touserId 用户主键
     * @return
     */
    @Operation(summary = "被委托审核")
    @GetMapping("/getuser")
    @Parameters({
            @Parameter(name = "flowId", description = "流程主键", required = true),
            @Parameter(name = "userId", description = "用户主键"),
            @Parameter(name = "touserId", description = "用户主键"),
    })
    @SaCheckPermission("workFlow.entrust")
    public ActionResult getuser(@RequestParam("flowId") String flowId, @RequestParam("userId") String userId, @RequestParam("touserId") String touserId) {
        flowDelegateService.getUser(userId, flowId, touserId);
        return ActionResult.success();
    }

    /**
     * 被委托发起
     * 根据被委托人查询可发起的流程列表
     *
     * @param pagination 分页模型
     * @return
     */
    @Operation(summary = "被委托发起")
    @GetMapping("/getflow")
    @SaCheckPermission("workFlow.entrust")
    public ActionResult<PageListVO<FlowPageListVO>> getflow(FlowPagination pagination) {
        List<FlowPageListVO> getflow = flowDelegateService.getflow(pagination);
        PaginationVO paginationVO = JsonUtil.getJsonToBean(pagination, PaginationVO.class);
        List<FlowPageListVO> listVO = JsonUtil.getJsonToList(getflow, FlowPageListVO.class);
        return ActionResult.page(listVO, paginationVO);
    }


    /**
     * 获取委托人
     * 根据被委托人查询可发起的流程列表
     *
     * @Param flowId 流程主键
     */
    @Operation(summary = "获取委托人")
    @GetMapping("/userList")
    @Parameters({
            @Parameter(name = "flowId", description = "流程主键",required = true),
    })
    @SaCheckPermission("workFlow.entrust")
    public ActionResult<ListVO<FlowCandidateUserModel>> getUserListByFlowId(@RequestParam("flowId") String flowId) throws WorkFlowException {
        ListVO<FlowCandidateUserModel> userListByFlowId = flowDelegateService.getUserListByFlowId(flowId);
        return ActionResult.success(userListByFlowId);
    }
}
