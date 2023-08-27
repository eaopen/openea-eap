package org.openea.eap.module.obpm.service.obpm;


import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.openea.eap.framework.common.enums.CommonStatusEnum;
import org.openea.eap.module.system.dal.dataobject.user.AdminUserDO;
import org.openea.eap.module.system.service.user.AdminUserService;
import org.openea.eap.module.system.service.user.AdminUserServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service("obpmUserService")
@ConditionalOnProperty(prefix = "eap", name = "enableOpenBpm", havingValue = "true")
@Slf4j
public class ObpmUserServiceImpl extends AdminUserServiceImpl implements AdminUserService {

    @Override
    public AdminUserDO getUser(Long id) {
        AdminUserDO user = super.getUser(id);
        // more info from obpm
        return user;
    }

    public AdminUserDO createAdminUser(JSONObject jsonUser) {
        AdminUserDO user = new AdminUserDO();
        // 保存原密码 “{sha2}+obpm加密后密码”
        String username = jsonUser.getStr("account");
        if(ObjectUtils.isEmpty(username)){
            username = jsonUser.getStr("username");
        }
        user.setUsername(username);
        user.setPassword("{sha2}"+jsonUser.getStr("password"));
        user.setMobile(jsonUser.getStr("mobile"));
        user.setEmail(jsonUser.getStr("email"));
        String nickname = jsonUser.getStr("nickname");
        if(ObjectUtils.isEmpty(nickname)){
            nickname = jsonUser.getStr("fullname");
        }
        if(ObjectUtils.isEmpty(nickname)){
            nickname = username;
        }
        user.setNickname(nickname);
        user.setStatus(CommonStatusEnum.ENABLE.getStatus());
        user.setCreateTime(LocalDateTimeUtil.now());
        user.setCreator("obpm-sync");
        userMapper.insert(user);
        user = userMapper.selectByUsername(user.getUsername());
        return user;
    }


}
