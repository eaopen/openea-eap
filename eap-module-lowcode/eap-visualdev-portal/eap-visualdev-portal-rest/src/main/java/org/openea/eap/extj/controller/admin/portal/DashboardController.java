package org.openea.eap.extj.controller.admin.portal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.entity.DictionaryDataEntity;
import org.openea.eap.extj.base.entity.DictionaryTypeEntity;
import org.openea.eap.extj.base.service.DictionaryDataService;
import org.openea.eap.extj.base.service.DictionaryTypeService;
import org.openea.eap.extj.base.vo.ListVO;
import org.openea.eap.extj.engine.entity.FlowTaskEntity;
import org.openea.eap.extj.engine.model.flowtask.FlowTaskListModel;
import org.openea.eap.extj.engine.service.FlowDelegateService;
import org.openea.eap.extj.engine.service.FlowTaskService;
import org.openea.eap.extj.extend.service.EmailReceiveService;
import org.openea.eap.extj.message.entity.MessageEntity;
import org.openea.eap.extj.message.model.NoticeModel;
import org.openea.eap.extj.message.service.MessageService;
import org.openea.eap.extj.permission.entity.UserEntity;
import org.openea.eap.extj.permission.service.UserService;
import org.openea.eap.extj.portal.model.*;
import org.openea.eap.extj.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 主页控制器
 *
 */
@Tag(name = "主页控制器", description = "Home")
@RestController
@RequestMapping("api/visualdev/Dashboard")
public class DashboardController {
    @Autowired
    private FlowTaskService flowTaskService;

    @Autowired
    private FlowDelegateService flowDelegateService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private EmailReceiveService emailReceiveService;

    @Autowired
    private UserService userService;

    @Autowired
    private DictionaryDataService dictionaryDataService;

    @Autowired
    private DictionaryTypeService dictionaryTypeService;

    /**
     * 获取我的待办
     *
     * @return
     */
    @Operation(summary = "获取我的待办")
    @PostMapping("/FlowTodoCount")
    public ActionResult getFlowTodoCount(@RequestBody FlowTodo flowTodo) {
        FlowTodoCountVO vo = new FlowTodoCountVO();
        List<FlowTaskEntity> toBeReviewedList = flowTaskService.getWaitList();
        if (flowTodo.getToBeReviewedType().size() > 0) {
            toBeReviewedList = toBeReviewedList.stream().filter(t -> flowTodo.getToBeReviewedType().contains(t.getFlowCategory())).collect(Collectors.toList());
        }
        vo.setToBeReviewed(toBeReviewedList.size());
        vo.setEntrust(flowDelegateService.getList().size());
        List<FlowTaskListModel> flowDoneList = flowTaskService.getTrialList();
        if (flowTodo.getFlowDoneType().size() > 0) {
            flowDoneList = flowDoneList.stream().filter(t -> flowTodo.getFlowDoneType().contains(t.getFlowCategory())).collect(Collectors.toList());
        }
        vo.setFlowDone(flowDoneList.size());
        List<FlowTaskListModel> flowCirculate = flowTaskService.getCirculateList();
        if (flowTodo.getFlowCirculateType().size() > 0) {
            flowCirculate = flowCirculate.stream().filter(t -> flowTodo.getFlowCirculateType().contains(t.getFlowCategory())).collect(Collectors.toList());
        }
        vo.setFlowCirculate(flowCirculate.size());
        return ActionResult.success(vo);
    }

    /**
     * 获取通知公告
     *
     * @return
     */
    @Operation(summary = "获取通知公告")
    @PostMapping("/Notice")
    public ActionResult getNotice(@RequestBody NoticeModel noticeModel) {
        List<MessageEntity> list = messageService.getDashboardNoticeList(noticeModel.getTypeList());
        List<UserEntity> userList = userService.getUserName(list.stream().map(MessageEntity::getCreatorUser).collect(Collectors.toList()));
        DictionaryTypeEntity dictionaryTypeEntity = dictionaryTypeService.getInfoByEnCode("NoticeType");
        List<DictionaryDataEntity> noticeType = dictionaryDataService.getDicList(dictionaryTypeEntity.getId());
        list.forEach(t -> {
            // 处理是否过期
            if (t.getExpirationTime() != null) {
                // 已发布的情况下
                if (t.getEnabledMark() == 1) {
                    if (t.getExpirationTime().getTime() < System.currentTimeMillis()) {
                        t.setEnabledMark(2);
                    }
                }
            }
            DictionaryDataEntity dictionaryDataEntity = noticeType.stream().filter(notice -> notice.getEnCode().equals(t.getCategory())).findFirst().orElse(new DictionaryDataEntity());
            t.setCategory(dictionaryDataEntity.getFullName());
            UserEntity user = userList.stream().filter(ul -> ul.getId().equals(t.getCreatorUser())).findFirst().orElse(null);
            t.setCreatorUser(user != null ? user.getRealName() + "/" + user.getAccount() : "");
            if (t.getEnabledMark() != null && t.getEnabledMark() != 0) {
                UserEntity entity = userService.getInfo(t.getLastModifyUserId());
                t.setLastModifyUserId(entity != null ? entity.getRealName() + "/" + entity.getAccount() : "");
            }
        });
        List<NoticeVO> data= JsonUtil.getJsonToList(list, NoticeVO.class);
        data.forEach(t -> {
            t.setReleaseTime(t.getLastModifyTime());
            t.setReleaseUser(t.getLastModifyUserId());
        });
        ListVO<NoticeVO> voList = new ListVO<>();
        voList.setList(data);
        return ActionResult.success(voList);
    }

    /**
     * 获取未读邮件
     *
     * @return
     */
    @Operation(summary = "获取未读邮件")
    @GetMapping("/Email")
    public ActionResult getEmail() {
        List<EmailVO> list = JsonUtil.getJsonToList(emailReceiveService.getDashboardReceiveList(), EmailVO.class);
        ListVO<EmailVO> voList = new ListVO<>();
        voList.setList(list);
        return ActionResult.success(voList);
    }

    /**
     * 获取待办事项
     *
     * @return
     */
    @Operation(summary = "获取待办事项")
    @GetMapping("/FlowTodo")
    public ActionResult getFlowTodo() {
        List<FlowTaskListModel> taskList = flowTaskService.getDashboardAllWaitList();
        List<FlowTodoVO> list = new LinkedList<>();
        for (FlowTaskListModel taskEntity : taskList) {
            FlowTodoVO vo = JsonUtil.getJsonToBean(taskEntity, FlowTodoVO.class);
            vo.setTaskNodeId(taskEntity.getThisStepId());
            vo.setTaskOperatorId(taskEntity.getId());
            vo.setType(2);
            list.add(vo);
        }
        ListVO voList = new ListVO<>();
        voList.setList(list);
        return ActionResult.success(voList);
    }

    /**
     * 获取我的待办事项
     *
     * @return
     */
    @Operation(summary = "获取我的待办事项")
    @GetMapping("/MyFlowTodo")
    public ActionResult getMyFlowTodo() {
        List<MyFlowTodoVO> list = JsonUtil.getJsonToList(flowTaskService.getWaitList(), MyFlowTodoVO.class);
        ListVO<MyFlowTodoVO> voList = new ListVO<>();
        voList.setList(list);
        return ActionResult.success(voList);
    }
}
