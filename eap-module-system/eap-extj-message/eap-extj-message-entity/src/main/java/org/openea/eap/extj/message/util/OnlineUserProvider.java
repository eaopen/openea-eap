package org.openea.eap.extj.message.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.openea.eap.extj.consts.AuthConsts.TOKEN_PREFIX;


@Slf4j
public class OnlineUserProvider {

    /**
     * 在线用户
     */
    private static List<OnlineUserModel> onlineUserList = new ArrayList<>();

    public static List<OnlineUserModel> getOnlineUserList() {
        return OnlineUserProvider.onlineUserList;
    }


    public static void addModel(OnlineUserModel model) {
        OnlineUserProvider.onlineUserList.add(model);
    }

    public static void removeModel(OnlineUserModel onlineUserModel) {
        onlineUserList.remove(onlineUserModel);
    }


    // =================== Websocket相关操作 ===================

    /**
     * 根据Token精准推送Websocket 登出消息
     *
     * @param token
     */
    public static void removeWebSocketByToken(String... token) {
        List<String> tokens = Arrays.stream(token).map(t -> t.contains(TOKEN_PREFIX) ? t : TOKEN_PREFIX + " " + t).collect(Collectors.toList());
        //清除websocket登录状态
        List<OnlineUserModel> users = OnlineUserProvider.getOnlineUserList().stream().filter(t -> tokens.contains(t.getToken())).collect(Collectors.toList());
        if (!ObjectUtils.isEmpty(users)) {
            for (OnlineUserModel user : users) {
                OnlineUserProvider.logoutWS(user, null);
                //先移除对象， 并推送下线信息， 避免网络原因导致就用户未断开 新用户连不上WebSocket
                OnlineUserProvider.removeModel(user);
                //通知所有在线，有用户离线
                for (OnlineUserModel item : OnlineUserProvider.getOnlineUserList().stream().filter(t -> !Objects.equals(user.getUserId(), t.getUserId()) && !Objects.equals(user.getTenantId(), t.getTenantId())).collect(Collectors.toList())) {
                    if (!item.getUserId().equals(user.getUserId())) {
                        JSONObject obj = new JSONObject();
                        obj.put("method", "Offline");
                        //推送给前端
                        OnlineUserProvider.sendMessage(item, obj);

                    }
                }
            }
        }
    }

    /**
     * 根据用户ID 推送全部Websocket 登出消息
     *
     * @param userId
     */
    public static void removeWebSocketByUser(String userId) {
        // TODO eap待处理
//        List<String> tokens = StpUtil.getTokenValueListByLoginId(UserProvider.splicingLoginId(userId));
//        removeWebSocketByToken(tokens.toArray(new String[tokens.size()]));
    }

    /**
     * 发送用户退出消息
     *
     * @param session
     */
    public static void logoutWS(OnlineUserModel onlineUserModel, Session session) {
        JSONObject obj = new JSONObject();
        obj.put("method", "logout");
        obj.put("token", onlineUserModel.getToken());
        if (onlineUserModel != null) {
            sendMessage(onlineUserModel, obj);
        } else {
            sendMessage(session, obj);
        }
    }


    /**
     * 发送关闭WebSocket消息, 前端不在重连
     *
     * @param session
     */
    public static void closeFrontWs(OnlineUserModel onlineUserModel, Session session) {
        JSONObject obj = new JSONObject();
        obj.put("method", "closeSocket");
        if (onlineUserModel != null) {
            sendMessage(onlineUserModel, obj);
        } else {
            sendMessage(session, obj);
        }
    }


    public static void sendMessage(OnlineUserModel onlineUserModel, Object message) {
        Session session = onlineUserModel.getWebSocket();
        synchronized (session) {

            try {
                if (session.isOpen()) {
                    session.getAsyncRemote().sendText(JSONObject.toJSONString(message));
                } else {
                    log.debug("WS未打开: {}, {}, {}, {}, {}", onlineUserModel.getTenantId(), session.getId(), onlineUserModel.getUserId(), onlineUserModel.getToken(), message);
                    try {
                        session.close();
                    } catch (Exception ee) {
                    } finally {
                        OnlineUserProvider.removeModel(onlineUserModel);
                    }
                }
            } catch (Exception e) {
                log.debug(String.format("WS消息发送失败: %s, %s, %s, %s, %s", onlineUserModel.getTenantId(), session.getId(), onlineUserModel.getUserId(), onlineUserModel.getToken(), message), e);
            }
        }
    }

    public static void sendMessage(Session session, Object message) {
        OnlineUserModel onlineUserModel = OnlineUserProvider.getOnlineUserList().stream().filter(t -> t.getConnectionId().equals(session.getId())).findFirst().orElse(null);
        if (onlineUserModel == null) {
            onlineUserModel = new OnlineUserModel();
            onlineUserModel.setWebSocket(session);
        }
        synchronized (session) {
            sendMessage(onlineUserModel, message);
        }
    }

}
