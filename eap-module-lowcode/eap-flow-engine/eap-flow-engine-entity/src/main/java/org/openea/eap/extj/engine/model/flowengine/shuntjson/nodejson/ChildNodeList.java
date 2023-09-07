package org.openea.eap.extj.engine.model.flowengine.shuntjson.nodejson;

import org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.Properties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析引擎
 *
 *
 */
@Data
public class ChildNodeList {
    /**节点属性**/
    private Properties properties = new Properties();
    /**自定义属性**/
    private Custom custom = new Custom();
    /**流程节点id**/
    private String taskNodeId;
    /**流程任务id**/
    private String taskId;
    /**下一级定时器属性**/
    private DateProperties timer = new DateProperties();
    /**分流合流**/
    private String conditionType;
    /**定时器所有**/
    private List<DateProperties> timerAll = new ArrayList<>();
    /**当前节点总人数**/
    private Double total=0.0;
}
