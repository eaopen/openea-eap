package org.openea.eapboot.modules.activiti.serviceimpl.mybatis;

import org.openea.eapboot.common.exception.EapbootException;
import org.openea.eapboot.modules.activiti.dao.mapper.ActMapper;
import org.openea.eapboot.modules.activiti.service.mybatis.IActService;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 */
@Service
public class IActServiceImpl implements IActService {

    @Autowired
    private ActMapper actMapper;

    @Override
    public Integer deleteBusiness(String table, String id) {

        if(StrUtil.isBlank(table)||StrUtil.isBlank(id)){
            throw new EapbootException("关联业务表名或id为空");
        }
        return actMapper.deleteBusiness(table, id);
    }
}
