package org.openea.eap.extj.engine.model.flowtime;

import org.openea.eap.extj.engine.model.flowengine.shuntjson.nodejson.ChildNodeList;
import lombok.Data;

import java.util.Date;

/**
 * 
 */
@Data
public class FlowTimeModel {
    /**是否开启限时设置*/
    private Boolean on = false;
    /**开始时间*/
    private Date date = new Date();
    /**通知*/
    private ChildNodeList childNode= new ChildNodeList();
    /**事件*/
    private ChildNodeList childNodeEvnet= new ChildNodeList();
}
