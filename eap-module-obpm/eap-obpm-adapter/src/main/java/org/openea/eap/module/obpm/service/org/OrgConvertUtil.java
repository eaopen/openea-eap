package org.openea.eap.module.obpm.service.org;

import cn.hutool.core.bean.BeanUtil;
import org.openbpm.org.api.model.IUser;
import org.openbpm.org.api.model.dto.UserDTO;
import org.openea.eap.module.system.dal.dataobject.user.AdminUserDO;

import java.util.ArrayList;
import java.util.List;

public class OrgConvertUtil {
    public static Long convertUserId(String userId) {
        return Long.valueOf(userId);
    }

    public static IUser convertUser(AdminUserDO adminUserDO) {
        UserDTO userDTO = new UserDTO();
        BeanUtil.copyProperties(adminUserDO, userDTO);
        // TODO 属性匹配
        return userDTO;
    }

    public static List<? extends IUser> convertUsers(List<AdminUserDO> listUser) {
        List<IUser> listUserDto = new ArrayList<>(listUser.size());
        for(AdminUserDO adminUser: listUser){
            IUser user = convertUser(adminUser);
            listUserDto.add(user);
        }
        return listUserDto;
    }
}
