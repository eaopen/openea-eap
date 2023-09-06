package org.openea.eap.extj.model.home;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 *
 *
 */
@Data
public class EmailVO {
    private String id;
    @JSONField(name="subject")
    private String fullName;
    private Long creatorTime;
}
