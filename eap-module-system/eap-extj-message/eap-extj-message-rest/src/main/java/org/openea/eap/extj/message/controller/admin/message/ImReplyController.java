package org.openea.eap.extj.message.controller.admin.message;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import org.openea.eap.extj.base.controller.SuperController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.vo.ListVO;
import org.openea.eap.extj.message.entity.ImReplyEntity;
import org.openea.eap.extj.message.model.ImReplyListModel;
import org.openea.eap.extj.message.model.ImReplyListVo;
import org.openea.eap.extj.message.service.ImContentService;
import org.openea.eap.extj.message.service.ImReplyService;
import org.openea.eap.extj.permission.entity.UserEntity;
import org.openea.eap.extj.permission.service.UserService;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.UploaderUtil;
import org.openea.eap.extj.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息会话接口
 *
 *
 */
@Tag(name = "消息会话接口", description = "imreply")
@RestController
@RequestMapping("/api/message/imreply")
public class ImReplyController extends SuperController<ImReplyService, ImReplyEntity> {
    @Autowired
    private ImReplyService imReplyService;
    @Autowired
    private ImContentService imContentService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserProvider userProvider;

    /**
     * 获取消息会话列表
     *
     * @return
     */
    @Operation(summary = "获取消息会话列表")
    @GetMapping
    public ActionResult<ListVO<ImReplyListVo>> getList() {
        List<ImReplyListModel> imReplyList = imReplyService.getImReplyList();
        //过滤 发送者删除标记
        imReplyList = imReplyList.stream().filter(t ->{
            if(t.getImreplySendDeleteMark() != null){
                return !t.getImreplySendDeleteMark().equals(userProvider.get().getUserId());
            }
            return true;
        }).collect(Collectors.toList());
        List<ImReplyListModel> imReplyLists = new ArrayList<>(imReplyList);
        for (ImReplyListModel vo : imReplyList) {
            UserEntity entity = userService.getInfo(vo.getId());
            if (entity == null || entity.getEnabledMark() == 0) {
                imReplyLists.remove(vo);
                continue;
            }
            //拼接账号和名称
            vo.setRealName(entity.getRealName());
            vo.setAccount(entity.getAccount());
            //头像路径拼接
            vo.setHeadIcon(UploaderUtil.uploaderImg(vo.getHeadIcon()));
            //获取未读消息
            vo.setUnreadMessage(imContentService.getUnreadCount(vo.getId(), userProvider.get().getUserId()));
            if(vo.getSendDeleteMark()!=null && vo.getSendDeleteMark().equals(userProvider.get().getUserId()) || vo.getDeleteMark()==1){
                vo.setLatestMessage("");
                vo.setMessageType("");
            }
        }
        //排序
        imReplyLists = imReplyLists.stream().sorted(Comparator.comparing(ImReplyListModel::getLatestDate).reversed()).collect(Collectors.toList());
        List<ImReplyListVo> imReplyListVoList = JsonUtil.getJsonToList(imReplyLists, ImReplyListVo.class);
        ListVO listVO = new ListVO();
        listVO.setList(imReplyListVoList);
        return ActionResult.success(listVO);
    }

    /**
     * 删除聊天记录
     *
     * @param id 主键
     * @return ignore
     */
    @Operation(summary = "删除聊天记录")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @DeleteMapping("/deleteChatRecord/{id}")
    public ActionResult deleteChatRecord(@PathVariable("id") String id){
        imContentService.deleteChatRecord(userProvider.get().getUserId(),id);
        return ActionResult.success("");
    }

    /**
     * 移除会话列表
     *
     * @param id 主键
     * @return ignore
     */
    @Operation(summary = "移除会话列表")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @DeleteMapping("/relocation/{id}")
    public ActionResult relocation(@PathVariable("id") String id){
        imReplyService.relocation(userProvider.get().getUserId(),id);
        return ActionResult.success("");
    }


}
