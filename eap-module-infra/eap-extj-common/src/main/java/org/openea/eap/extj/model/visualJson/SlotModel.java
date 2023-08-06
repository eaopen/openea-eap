package org.openea.eap.extj.model.visualJson;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class SlotModel {
    private String prepend;
    private String append;
    @JSONField(
            name = "default"
    )
    private String defaultName;
    private String options;
    private String appOptions;

}