package org.openea.eap.extj.permission.model.user.mod;

import lombok.Data;
import org.openea.eap.extj.permission.entity.UserEntity;

/**
 * 
 */
@Data
public class UserUpModel {

    private Integer num;

    private UserEntity entity;

    public UserUpModel() {
    }

    public UserUpModel(Integer num, UserEntity entity) {
        this.num = num;
        this.entity = entity;
    }
}
