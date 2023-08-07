package org.openea.eap.extj.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.extj.permission.model.authorize.AuthorizeConditionModel;
import org.openea.eap.extj.permission.service.AuthorizeService;
import org.springframework.stereotype.Service;

@Service
public class EapAuthorizeService implements AuthorizeService {
    @Override
    public <T> QueryWrapper<T> getCondition(AuthorizeConditionModel conditionModel) {
        // TODO
        return null;
    }
}
