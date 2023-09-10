package org.openea.eap.extj.controller.admin.engine;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.base.controller.SuperController;
import org.openea.eap.extj.base.vo.PageListVO;
import org.openea.eap.extj.base.vo.PaginationVO;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.permission.entity.UserEntity;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.UploaderUtil;
import org.openea.eap.extj.util.UserProvider;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import org.openea.eap.extj.engine.entity.FlowCommentEntity;
import org.openea.eap.extj.engine.model.flowcomment.FlowCommentForm;
import org.openea.eap.extj.engine.model.flowcomment.FlowCommentInfoVO;
import org.openea.eap.extj.engine.model.flowcomment.FlowCommentListVO;
import org.openea.eap.extj.engine.model.flowcomment.FlowCommentPagination;
import org.openea.eap.extj.engine.service.FlowCommentService;
import org.openea.eap.extj.util.ServiceAllUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 流程评论
 *
 * 
 */
@Tag(name = "流程评论", description = "Comment")
@RestController
@RequestMapping("/api/workflow/Engine/FlowComment")
public class FlowCommentController extends SuperController<FlowCommentService, FlowCommentEntity> {


    @Autowired
    private ServiceAllUtil serviceUtil;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private FlowCommentService flowCommentService;

    /**
     * 获取流程评论列表
     *
     * @param pagination 分页模型
     * @return
     */
    @Operation(summary = "获取流程评论列表")
    @GetMapping
    @SaCheckPermission(value = {"workFlow.flowTodo", "workFlow.flowDone", "workFlow.flowCirculate", "workFlow.flowMonitor"}, mode = SaMode.OR)
    public ActionResult<PageListVO<FlowCommentListVO>> list(FlowCommentPagination pagination) {
        List<FlowCommentEntity> list = flowCommentService.getlist(pagination);
        List<FlowCommentListVO> listVO = JsonUtil.getJsonToList(list, FlowCommentListVO.class);
        List<String> userId = list.stream().map(t -> t.getCreatorUserId()).collect(Collectors.toList());
        UserInfo userInfo = userProvider.get();
        List<UserEntity> userName = serviceUtil.getUserName(userId);
        for (FlowCommentListVO commentModel : listVO) {
            UserEntity userEntity = userName.stream().filter(t -> t.getId().equals(commentModel.getCreatorUserId())).findFirst().orElse(null);
            commentModel.setIsDel(commentModel.getCreatorUserId().equals(userInfo.getUserId()));
            commentModel.setCreatorUser(userEntity != null ? userEntity.getRealName() + "/" + userEntity.getAccount() : "");
            commentModel.setCreatorUserHeadIcon(userEntity != null ? UploaderUtil.uploaderImg(userEntity.getHeadIcon()) : commentModel.getCreatorUserHeadIcon());
        }
        PaginationVO vo = JsonUtil.getJsonToBean(pagination, PaginationVO.class);
        return ActionResult.page(listVO, vo);
    }

    /**
     * 获取流程评论信息
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "获取流程评论信息")
    @GetMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    @SaCheckPermission(value = {"workFlow.flowTodo", "workFlow.flowDone", "workFlow.flowCirculate", "workFlow.flowMonitor"}, mode = SaMode.OR)
    public ActionResult<FlowCommentInfoVO> info(@PathVariable("id") String id) {
        FlowCommentEntity entity = flowCommentService.getInfo(id);
        FlowCommentInfoVO vo = JsonUtil.getJsonToBean(entity, FlowCommentInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建流程评论
     *
     * @param commentForm 流程评论模型
     * @return
     */
    @Operation(summary = "新建流程评论")
    @PostMapping
    @Parameters({
            @Parameter(name = "commentForm", description = "流程评论模型",required = true),
    })
    @SaCheckPermission(value = {"workFlow.flowTodo", "workFlow.flowDone", "workFlow.flowCirculate", "workFlow.flowMonitor"}, mode = SaMode.OR)
    public ActionResult create(@RequestBody @Valid FlowCommentForm commentForm) throws DataException {
        FlowCommentEntity entity = JsonUtil.getJsonToBean(commentForm, FlowCommentEntity.class);
        flowCommentService.create(entity);
        return ActionResult.success(MsgCode.SU002.get());
    }

    /**
     * 更新流程评论
     *
     * @param id          主键
     * @param commentForm 流程评论模型
     * @return
     */
    @Operation(summary = "更新流程评论")
    @PutMapping("/{id}")
    @Parameters({
            @Parameter(name = "commentForm", description = "流程评论模型",required = true),
            @Parameter(name = "id", description = "主键", required = true),
    })
    @SaCheckPermission(value = {"workFlow.flowTodo", "workFlow.flowDone", "workFlow.flowCirculate", "workFlow.flowMonitor"}, mode = SaMode.OR)
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid FlowCommentForm commentForm) throws DataException {
        FlowCommentEntity info = flowCommentService.getInfo(id);
        if (info != null) {
            FlowCommentEntity entity = JsonUtil.getJsonToBean(commentForm, FlowCommentEntity.class);
            flowCommentService.update(id, entity);
            return ActionResult.success(MsgCode.SU004.get());
        }
        return ActionResult.fail(MsgCode.FA002.get());
    }

    /**
     * 删除流程评论
     *
     * @param id 主键
     * @return
     */
    @Operation(summary = "删除流程评论")
    @DeleteMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    @SaCheckPermission(value = {"workFlow.flowTodo", "workFlow.flowDone", "workFlow.flowCirculate", "workFlow.flowMonitor"}, mode = SaMode.OR)
    public ActionResult delete(@PathVariable("id") String id) {
        FlowCommentEntity entity = flowCommentService.getInfo(id);
        if (entity.getCreatorUserId().equals(userProvider.get().getUserId())) {
            flowCommentService.delete(entity);
            return ActionResult.success(MsgCode.SU003.get());
        }
        return ActionResult.success(MsgCode.FA003.get());
    }

}
