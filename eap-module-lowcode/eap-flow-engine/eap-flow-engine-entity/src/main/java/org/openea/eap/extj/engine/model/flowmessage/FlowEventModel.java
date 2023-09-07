package org.openea.eap.extj.engine.model.flowmessage;

import org.openea.eap.extj.engine.entity.FlowTaskOperatorRecordEntity;
import org.openea.eap.extj.engine.model.flowengine.shuntjson.childnode.TemplateJsonModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowEventModel {

    //数据
    private String dataJson;
    //表单数据
    private Map<String, Object> data;
    //系统匹配
    private TemplateJsonModel templateJson;
    //操作对象
    private FlowTaskOperatorRecordEntity record;

}
