package org.openea.eap.extj.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * token
 *
 *
 */
@Data
public class HeadersModel {
    @JSONField(name = "Token")
    private String token;
    @JSONField(name = "ModuleId")
    private String moduleId;
}
