package org.openea.eap.extj.model;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 */
@Slf4j
public class OnlineUserProvider {

    /**
     * 在线用户
     */
    private static List<OnlineUserModel> onlineUserList = new ArrayList<>();

    public static List<OnlineUserModel> getOnlineUserList() {
        return OnlineUserProvider.onlineUserList;
    }

    public static void addModel(OnlineUserModel model){
        OnlineUserProvider.onlineUserList.add(model);
    }

    public static void removeModel(OnlineUserModel onlineUserModel){
        onlineUserList.remove(onlineUserModel);
    }

    /**
     * 发送用户退出消息
     * @param session
     */
    public static void logoutWS(OnlineUserModel onlineUserModel, Session session) {
        JSONObject obj = new JSONObject();
        obj.put("method", "logout");
        obj.put("token", onlineUserModel.getToken());
        if(onlineUserModel != null) {
            sendMessage(onlineUserModel, obj);
        }else{
            sendMessage(session, obj);
        }
    }


    /**
     * 发送关闭WebSocket消息, 前端不在重连
     * @param session
     */
    public static void closeFrontWs(OnlineUserModel onlineUserModel, Session session) {
        JSONObject obj = new JSONObject();
        obj.put("method", "closeSocket");
        if(onlineUserModel != null) {
            sendMessage(onlineUserModel, obj);
        }else{
            sendMessage(session, obj);
        }
    }


    public static void sendMessage(OnlineUserModel onlineUserModel, Object message){
        Session session = onlineUserModel.getWebSocket();
        synchronized (session) {

            try {
                if (session.isOpen()) {
                    session.getAsyncRemote().sendText(JSONObject.toJSONString(message));
                }else{
                    log.debug("WS未打开: {}, {}, {}, {}, {}", onlineUserModel.getTenantId(), session.getId(), onlineUserModel.getUserId(), onlineUserModel.getToken(), message);
                    try{
                        session.close();
                    }catch (Exception ee){
                    }
                    finally {
                        OnlineUserProvider.removeModel(onlineUserModel);
                    }
                }
            }catch (Exception e){
                log.debug(String.format("WS消息发送失败: %s, %s, %s, %s, %s", onlineUserModel.getTenantId(), session.getId(), onlineUserModel.getUserId(), onlineUserModel.getToken(), message), e.getMessage());
            }
        }
    }

    public static void sendMessage(Session session, Object message){
        OnlineUserModel onlineUserModel = OnlineUserProvider.getOnlineUserList().stream().filter(t -> t.getConnectionId().equals(session.getId())).findFirst().orElse(null);
        if(onlineUserModel == null){
            onlineUserModel = new OnlineUserModel();
            onlineUserModel.setWebSocket(session);
        }
        synchronized (session) {
            sendMessage(onlineUserModel, message);
        }
    }
}
