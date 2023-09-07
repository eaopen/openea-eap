package org.openea.eap.extj.permission.model.user.mod;

import lombok.Data;
import org.openea.eap.extj.base.Pagination;

@Data
public class UserIdModelByPage extends UserIdModel {

    private Pagination pagination;
    
}
