package org.openea.eap.extj.permission.model.user.mod;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 */
@Data
public class UserIdModel {
    /**
     * 用户id集合
     */
    @Schema(description = "用户id集合")
    private Object userId;

    public List<String> getUserId() {
        List<String> ids = new ArrayList<>(16);
        if (userId != null) {
            if (userId instanceof List) {
                List list = (List) userId;
                Object object = list.size() > 0 ? list.get(0) : null;
                if (Objects.nonNull(object) && object instanceof String) {
                    ids.addAll(list);
                }
            } else {
                String userIds = (String) userId;
                ids.add(userIds);
            }
        }
        return ids;
    }
}
