package org.openea.eap.extj.message.controller.admin.message;


//import cn.dev33.satoken.annotation.SaCheckPermission;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.base.controller.SuperController;
import org.openea.eap.extj.base.entity.DictionaryDataEntity;
import org.openea.eap.extj.base.entity.DictionaryTypeEntity;
import org.openea.eap.extj.base.service.DictionaryDataService;
import org.openea.eap.extj.base.service.DictionaryTypeService;
import org.openea.eap.extj.base.vo.PageListVO;
import org.openea.eap.extj.base.vo.PaginationVO;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.message.entity.MessageEntity;
import org.openea.eap.extj.message.entity.MessageReceiveEntity;
import org.openea.eap.extj.message.model.NoticePagination;
import org.openea.eap.extj.message.model.message.*;
import org.openea.eap.extj.message.service.MessageService;
import org.openea.eap.extj.message.service.UserDeviceService;
import org.openea.eap.extj.message.util.unipush.UinPush;
import org.openea.eap.extj.permission.entity.UserEntity;
import org.openea.eap.extj.permission.service.UserService;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.JsonUtilEx;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统公告
 *
 *
 */
@Tag(name = "系统公告", description = "Message")
@RestController
@RequestMapping("/api/message")
public class MessageController extends SuperController<MessageService, MessageEntity> {

    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;
    @Autowired
    private UinPush uinPush;
    @Autowired
    private UserDeviceService userDeviceService;
    @Autowired
    private DictionaryDataService dictionaryDataService;
    @Autowired
    private DictionaryTypeService dictionaryTypeService;

    /**
     * 列表（通知公告）
     *
     * @param pagination
     * @return
     */
    @Operation(summary = "获取系统公告列表（带分页）")
    //@SaCheckPermission("system.notice")
    @PostMapping("/Notice")
    public ActionResult<PageListVO<MessageNoticeVO>> noticeList(@RequestBody NoticePagination pagination) {
        messageService.updateEnabledMark();
        List<MessageEntity> list = messageService.getNoticeList(pagination);
        List<UserEntity> userList = userService.getUserName(list.stream().map(MessageEntity::getCreatorUser).collect(Collectors.toList()));
        PaginationVO paginationVO = JsonUtil.getJsonToBean(pagination, PaginationVO.class);
        DictionaryTypeEntity dictionaryTypeEntity = dictionaryTypeService.getInfoByEnCode("NoticeType");
        List<DictionaryDataEntity> noticeType = dictionaryDataService.getDicList(dictionaryTypeEntity.getId());
        list.forEach(t -> {
            DictionaryDataEntity dictionaryDataEntity = noticeType.stream().filter(notice -> notice.getEnCode().equals(t.getCategory())).findFirst().orElse(new DictionaryDataEntity());
            t.setCategory(dictionaryDataEntity.getFullName());
            UserEntity user = userList.stream().filter(ul -> ul.getId().equals(t.getCreatorUser())).findFirst().orElse(null);
            t.setCreatorUser(user != null ? user.getRealName() + "/" + user.getAccount() : "");
            if (t.getEnabledMark() != null && t.getEnabledMark() != 0) {
                UserEntity entity = userService.getInfo(t.getLastModifyUserId());
                t.setLastModifyUserId(entity != null ? entity.getRealName() + "/" + entity.getAccount() : "");
            }
        });
        List<MessageNoticeVO> voList = JsonUtil.getJsonToList(list, MessageNoticeVO.class);
        voList.forEach(t -> {
            t.setReleaseTime(t.getLastModifyTime());
            t.setReleaseUser(t.getLastModifyUserId());
        });
        return ActionResult.page(voList, paginationVO);
    }

    /**
     * 添加系统公告
     *
     * @param noticeCrForm 实体对象
     * @return
     */
    @Operation(summary = "添加系统公告")
    @Parameters({
            @Parameter(name = "noticeCrForm", description = "新建系统公告模型", required = true)
    })
    //@SaCheckPermission("system.notice")
    @PostMapping
    public ActionResult create(@RequestBody @Valid NoticeCrForm noticeCrForm) {
        MessageEntity entity = JsonUtil.getJsonToBean(noticeCrForm, MessageEntity.class);
        messageService.create(entity);
        return ActionResult.success(MsgCode.SU001.get());
    }

    /**
     * 修改系统公告
     *
     * @param id            主键值
     * @param messageUpForm 实体对象
     * @return
     */
    @Operation(summary = "修改系统公告")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "messageUpForm", description = "修改系统公告模型", required = true)
    })
    //@SaCheckPermission("system.notice")
    @PutMapping("/{id}")
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid NoticeUpForm messageUpForm) {
        MessageEntity entity = JsonUtil.getJsonToBean(messageUpForm, MessageEntity.class);
        boolean flag = messageService.update(id, entity);
        if (flag == false) {
            return ActionResult.fail(MsgCode.FA002.get());
        }
        return ActionResult.success(MsgCode.SU004.get());
    }

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "获取/查看系统公告信息")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    //@SaCheckPermission("system.notice")
    @GetMapping("/{id}")
    public ActionResult<NoticeInfoVO> info(@PathVariable("id") String id) throws DataException {
        MessageEntity entity = messageService.getinfo(id);
        NoticeInfoVO vo = null;
        if (entity != null) {
            UserEntity info = userService.getInfo(entity.getCreatorUser());
            entity.setCreatorUser(info != null ? info.getRealName() + "/" + info.getAccount() : "");
            vo = JsonUtilEx.getJsonToBeanEx(entity, NoticeInfoVO.class);
            vo.setReleaseUser(entity.getCreatorUser());
            vo.setReleaseTime(entity.getLastModifyTime() != null ? entity.getLastModifyTime().getTime() : null);
            UserEntity userEntity = userService.getInfo(entity.getLastModifyUserId());
            if (userEntity != null && StringUtil.isNotEmpty(userEntity.getId())) {
                String realName = userEntity.getRealName();
                String account = userEntity.getAccount();
                if (StringUtil.isNotEmpty(realName)) {
                    vo.setReleaseUser(realName + "/" + account);
                }
            }
        }
        return ActionResult.success(vo);
    }

    /**
     * 删除
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "删除系统公告")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    //@SaCheckPermission("system.notice")
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") String id) {
        MessageEntity entity = messageService.getinfo(id);
        if (entity != null) {
            messageService.delete(entity);
            return ActionResult.success(MsgCode.SU003.get());
        }
        return ActionResult.fail(MsgCode.FA003.get());
    }


    /**
     * 发布公告
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "发布系统公告")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    //@SaCheckPermission("system.notice")
    @PutMapping("/{id}/Actions/Release")
    public ActionResult release(@PathVariable("id") String id) {
        MessageEntity entity = messageService.getinfo(id);
        if (entity != null) {
            List<String> userIds = null;
            if (StringUtil.isNotEmpty(entity.getToUserIds())) {
                userIds = Arrays.asList(entity.getToUserIds().split(","));
            } else {
                userIds = userService.getListId();
            }
            List<String> userIdList = userService.getUserIdList(userIds);
            messageService.sentNotice(userIdList, entity);
            /*if(userIdList != null && userIdList.size()>0) {
                for (String userId : userIdList) {
                    List<String> cidList = userDeviceService.getCidList(userId);
                    if(cidList != null && cidList.size()>0){
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("type","1");
                        jsonObject.put("id",entity.getId());
                        jsonObject.put("title",entity.getTitle());
                        String text = JSONObject.toJSONString(jsonObject);
                        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
                        text = Base64.getEncoder().encodeToString(bytes);
                        uinPush.sendUniPush(cidList, entity.getTitle(), "你有一条公告消息", "1", text);
                    }
                }
            }*/
            return ActionResult.success("发布成功");
        }
        return ActionResult.fail("发布失败");
    }
//=======================================站内消息、消息中心=================================================


    /**
     * 获取消息中心列表
     *
     * @param pagination
     * @return
     */
    @Operation(summary = "列表（通知公告/系统消息/私信消息）")
    @GetMapping
    public ActionResult<PageListVO<MessageInfoVO>> messageList(PaginationMessage pagination) {
        List<MessageEntity> list = messageService.getMessageList1(pagination, pagination.getType(), pagination.getIsRead());
        List<UserEntity> userList = userService.getUserName(list.stream().map(t -> t.getCreatorUser()).collect(Collectors.toList()));
        List<MessageInfoVO> listVO = JsonUtil.getJsonToList(list, MessageInfoVO.class);
        listVO.forEach(t -> {
            UserEntity user = userList.stream().filter(ul -> ul.getId().equals(t.getCreatorUser())).findFirst().orElse(null);
            if (user != null) {
                t.setCreatorUser(user.getRealName() + "/" + user.getAccount());
                t.setReleaseTime(t.getLastModifyTime());
                UserEntity entity = userService.getInfo(t.getLastModifyUserId());
                t.setReleaseUser(entity != null ? entity.getRealName() + "/" + entity.getAccount() : "");
            }
        });
        PaginationVO paginationVO = JsonUtil.getJsonToBean(pagination, PaginationVO.class);
        return ActionResult.page(listVO, paginationVO);
    }


    /**
     * 读取消息
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "读取消息")
    @Parameters({
            @Parameter(name = "id", description = "主键值", required = true)
    })
    @GetMapping("/ReadInfo/{id}")
    public ActionResult<NoticeInfoVO> readInfo(@PathVariable("id") String id) throws DataException {
        MessageEntity entity = messageService.getinfo(id);
        if (entity != null) {
            MessageReceiveEntity receive = messageService.messageRead(id);
            UserEntity user = userService.getInfo(entity.getCreatorUser());
            entity.setCreatorUser(user != null ? user.getRealName() + "/" + user.getAccount() : "");
            entity.setBodyText(entity.getBodyText());
            if (entity.getType() == 2) {
                entity.setBodyText(receive.getBodyText());
            }
        }
        NoticeInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, NoticeInfoVO.class);
        vo.setReleaseTime(entity.getLastModifyTime() != null ? entity.getLastModifyTime().getTime() : null);
        UserEntity info = userService.getInfo(entity.getLastModifyUserId());
        vo.setReleaseUser(info != null ? info.getRealName() + "/" + info.getAccount() : "");
        return ActionResult.success(vo);
    }


    /**
     * 全部已读
     *
     * @param pagination 分页模型
     * @return
     */
    @Operation(summary = "全部已读")
    @Parameters({
            @Parameter(name = "pagination", description = "分页模型", required = true)
    })
    @PostMapping("/Actions/ReadAll")
    public ActionResult allRead(@RequestBody PaginationMessage pagination) {
        List<MessageEntity> list = messageService.getMessageList3(pagination, pagination.getType(),null,pagination.getIsRead());
        if(list != null && list.size()>0) {
            List<MessageEntity> unReadList = list.stream().filter(t->t.getIsRead()!=1).collect(Collectors.toList());
            if(unReadList != null && unReadList.size()>0) {
                List<String> idList = list.stream().map(t -> t.getId()).collect(Collectors.toList());
                messageService.messageRead(idList);
                return ActionResult.success("操作成功");
            }else {
                return ActionResult.fail("暂无未读消息");
            }
        }else {
            return ActionResult.fail("暂无未读消息");
        }
    }

    /**
     * app端获取未读数据
     *
     * @return
     */
    @Operation(summary = "app端获取未读数据")
    @GetMapping("/getUnReadMsgNum")
    public ActionResult getUnReadMsgNum() {
        Map<String, String> map = new HashMap<>();
        UserInfo userInfo = UserProvider.getUser();
        Integer unReadMsg = messageService.getUnreadMessageCount(userInfo.getUserId());
        Integer unReadNotice = messageService.getUnreadNoticeCount(userInfo.getUserId());
        Integer unReadSchedule = messageService.getUnreadCount(userInfo.getUserId(),4);
        Integer unReadSystemMsg = messageService.getUnreadSystemMessageCount(userInfo.getUserId());
        Integer unReadNum = unReadMsg+unReadNotice+unReadSchedule+unReadSystemMsg;
        map.put("unReadMsg",unReadMsg.toString());
        map.put("unReadNotice",unReadNotice.toString());
        map.put("unreadSchedule",unReadSchedule.toString());
        map.put("unReadSystemMsg",unReadSystemMsg.toString());
        map.put("unReadNum",unReadNum.toString());
        return ActionResult.success(map);
    }

    /**
     * 删除记录
     *
     * @param recordForm 已读模型
     * @return
     */
    @Operation(summary = "删除消息")
    @Parameters({
            @Parameter(name = "recordForm", description = "已读模型", required = true)
    })
    @DeleteMapping("/Record")
    public ActionResult deleteRecord(@RequestBody MessageRecordForm recordForm) {
        String[] id = recordForm.getIds().split(",");
        List<String> list = Arrays.asList(id);
        messageService.deleteRecord(list);
        return ActionResult.success(MsgCode.SU003.get());
    }
}
