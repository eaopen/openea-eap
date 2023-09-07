package org.openea.eap.extj.engine.util;

import org.openea.eap.extj.engine.model.flowmessage.FlowParameterModel;
import org.openea.eap.extj.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 事件数据添加
 *
 *
 */
public class FlowContextHolder {

    private static final ThreadLocal<List<FlowParameterModel>> CONTEXT_DB_NAME_HOLDER = new ThreadLocal<>();

    private static final ThreadLocal<Map<String, Map<String, Object>>> CONTEXT_DATA = new ThreadLocal<>();

    private static final ThreadLocal<Map<String, Map<String, Object>>> CHILD_DATA = new ThreadLocal<>();

    /**
     * 添加当前事件对象
     */
    static void addEvent(String interId, Map<String, String> parameterMap) {
        FlowParameterModel model = new FlowParameterModel();
        model.setInterId(interId);
        model.setParameterMap(parameterMap);
        List<FlowParameterModel> list = CONTEXT_DB_NAME_HOLDER.get() != null ? CONTEXT_DB_NAME_HOLDER.get() : new ArrayList<>();
        list.add(model);
        CONTEXT_DB_NAME_HOLDER.set(list);
    }

    /**
     * 获取当前事件对象
     */
    public static List<FlowParameterModel> getAllEvent() {
        return CONTEXT_DB_NAME_HOLDER.get() != null ? CONTEXT_DB_NAME_HOLDER.get() : new ArrayList<>();
    }

    /**
     * 添加数据
     */
    public static void addData(String formId, Map<String, Object> parameterMap) {
        if (StringUtil.isNotEmpty(formId)) {
            Map<String, Map<String, Object>> map = CONTEXT_DATA.get() != null ? CONTEXT_DATA.get() : new HashMap<>();
            map.put(formId, parameterMap);
            CONTEXT_DATA.set(map);
        }
    }

    /**
     * 获取数据
     */
    static Map<String, Map<String, Object>> getAllData() {
        Map<String, Map<String, Object>> data = CONTEXT_DATA.get() != null ? CONTEXT_DATA.get() : new HashMap<>();
        return data;
    }

    /**
     * 清除数据
     */
    public static void clearAll() {
        CONTEXT_DB_NAME_HOLDER.remove();
        CONTEXT_DATA.remove();
        CHILD_DATA.remove();
    }


    /**
     * 添加数据
     */
    public static void addChildData(String taskId, String formId, Map<String, Object> parameterMap) {
        if (StringUtil.isNotEmpty(taskId) && StringUtil.isNotEmpty(formId)) {
            Map<String, Map<String, Object>> map = CHILD_DATA.get() != null ? CHILD_DATA.get() : new HashMap<>();
            map.put(taskId + "_jnpf_" + formId, parameterMap);
            CHILD_DATA.set(map);
        }
    }

    /**
     * 获取数据
     */
    public static Map<String, Map<String, Object>> getChildAllData() {
        Map<String, Map<String, Object>> data = CHILD_DATA.get() != null ? CHILD_DATA.get() : new HashMap<>();
        return data;
    }


}
