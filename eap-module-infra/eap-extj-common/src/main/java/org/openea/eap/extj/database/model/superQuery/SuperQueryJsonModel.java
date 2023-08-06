package org.openea.eap.extj.database.model.superQuery;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class SuperQueryJsonModel {

    @JSONField(
            name = "matchLogic"
    )
    private String matchLogic;
    @JSONField(
            name = "conditionJson"
    )
    private String conditionJson;
}
