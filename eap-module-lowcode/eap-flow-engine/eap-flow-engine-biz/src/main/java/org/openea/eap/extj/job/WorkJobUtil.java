package org.openea.eap.extj.job;

import com.alibaba.fastjson.JSONObject;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.engine.entity.FlowTaskOperatorEntity;
import org.openea.eap.extj.engine.model.flowmessage.FlowMsgModel;
import org.openea.eap.extj.engine.model.flowtask.WorkJobModel;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.RedisUtil;
import org.openea.eap.extj.engine.util.FlowMsgUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流程定时器工具类
 *
 *
 */
@Component
public class WorkJobUtil {
    /**
     * 缓存key
     */
    public static final String REDIS_KEY = "idgenerator_WorkJobNew";

    private static FlowMsgUtil flowMsgUtil;

    @Autowired
    public void setFlowMsgUtil(FlowMsgUtil flowMsgUtil) {
        WorkJobUtil.flowMsgUtil = flowMsgUtil;
    }

    /**
     * 将数据放入缓存
     *
     * @param
     * @return
     */
    public static void insertRedis(WorkJobModel workJobModel, RedisUtil redisUtil) {
        JSONObject json = new JSONObject();
        json.put("taskId", workJobModel.getTaskId());
        json.put("flowMsgModel", JsonUtil.getObjectToString(workJobModel.getFlowMsgModel()));
        json.put("userInfo", JsonUtil.getObjectToString(workJobModel.getUserInfo()));

        List<FlowTaskOperatorEntity> operatorList = workJobModel.getFlowMsgModel().getOperatorList();
        if(operatorList.isEmpty()){
            return;
        }
        long time = System.currentTimeMillis();
        boolean isNext = operatorList.stream().filter(t -> t.getCreatorTime().getTime() > time).count() > 0;
        if(isNext) {
            Map<String, List<FlowTaskOperatorEntity>> operatorMap = operatorList.stream().collect(Collectors.groupingBy(FlowTaskOperatorEntity::getTaskNodeId));
            for (String key : operatorMap.keySet()) {
                redisUtil.insertHash(REDIS_KEY, workJobModel.getTaskId()+key, String.valueOf(operatorMap.get(key).get(0).getCreatorTime().getTime()));
                redisUtil.insertHash(REDIS_KEY+"_json", workJobModel.getTaskId()+key, json.toJSONString());
            }

        }else {
            flowMsgUtil.message(workJobModel.getFlowMsgModel());
        }
    }

    /**
     * 定时器取用数据调用创建方法
     *
     * @param
     * @return
     */
    public static List<WorkJobModel> getListRedis(RedisUtil redisUtil) {
        List<WorkJobModel> list = new ArrayList<>();
        if (redisUtil.exists(REDIS_KEY)) {
            Map<String, Object> map = redisUtil.getMap(REDIS_KEY);
            for (Object object : map.keySet()) {
                if (map.get(object) instanceof String) {
                    Map<String, Object> jsonMap = JsonUtil.stringToMap(String.valueOf(map.get(object)));
                    String taskId = jsonMap.get("taskId").toString();
                    FlowMsgModel flowMsgModel = JsonUtil.getJsonToBean(jsonMap.get("flowMsgModel").toString(), FlowMsgModel.class);
                    UserInfo userInfo = JsonUtil.getJsonToBean(jsonMap.get("userInfo").toString(), UserInfo.class);
                    list.add(new WorkJobModel(taskId, flowMsgModel, userInfo));
                }
            }
        }
        return list;
    }

    /**
     * 定时器取用数据调用创建方法
     *
     * @param
     * @return
     */
    public static Map<String,Object> getListRedisTime(RedisUtil redisUtil) {
        Map<String, Object> map=null;
        if (redisUtil.exists(REDIS_KEY)) {
           map = redisUtil.getMap(REDIS_KEY);
        }
        return map;
    }
    /**
     * 获取缓存信息
     *
     * @param
     * @return
     */
    public static WorkJobModel getListRedisJson(RedisUtil redisUtil, String key) {
        String hashValues = redisUtil.getHashValues(REDIS_KEY + "_json", key);
        Map<String, Object> jsonMap = JsonUtil.stringToMap(String.valueOf(hashValues));
        String taskId = jsonMap.get("taskId").toString();
        FlowMsgModel flowMsgModel = JsonUtil.getJsonToBean(jsonMap.get("flowMsgModel").toString(), FlowMsgModel.class);
        UserInfo userInfo = JsonUtil.getJsonToBean(jsonMap.get("userInfo").toString(), UserInfo.class);
        return new WorkJobModel(taskId, flowMsgModel, userInfo);
    }

}
