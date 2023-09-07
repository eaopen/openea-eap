package org.openea.eap.extj.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.openea.eap.extj.base.Pagination;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.util.*;
import org.openea.eap.extj.base.entity.DataInterfaceLogEntity;
import org.openea.eap.extj.base.mapper.DataInterfaceLogMapper;
import org.openea.eap.extj.base.model.InterfaceOauth.PaginationIntrfaceLog;
import org.openea.eap.extj.base.service.DataInterfaceLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DataInterfaceLogServiceImpl extends SuperServiceImpl<DataInterfaceLogMapper, DataInterfaceLogEntity> implements DataInterfaceLogService {
    @Autowired
    private EapUserProvider userProvider;

    @Override
    public void create(String dateInterfaceId, Integer invokWasteTime) {
        DataInterfaceLogEntity entity = new DataInterfaceLogEntity();
        entity.setId(RandomUtil.uuId());
        entity.setInvokTime(DateUtil.getNowDate());
        entity.setUserId(userProvider.get().getUserId());
        entity.setInvokId(dateInterfaceId);
        entity.setInvokIp(IpUtil.getIpAddr());
        entity.setInvokType("GET");
        entity.setInvokDevice(ServletUtil.getUserAgent());
        entity.setInvokWasteTime(invokWasteTime);
        this.save(entity);
    }
    @Override
    public void create(String dateInterfaceId, Integer invokWasteTime,String appId,String invokType) {
        DataInterfaceLogEntity entity = new DataInterfaceLogEntity();
        entity.setId(RandomUtil.uuId());
        entity.setInvokTime(DateUtil.getNowDate());
        entity.setUserId(userProvider.get().getUserId());
        entity.setInvokId(dateInterfaceId);
        entity.setInvokIp(IpUtil.getIpAddr());
        entity.setInvokType(invokType);
        entity.setInvokDevice(ServletUtil.getUserAgent());
        entity.setInvokWasteTime(invokWasteTime);
        entity.setOauthAppId(appId);
        this.save(entity);
    }

    @Override
    public List<DataInterfaceLogEntity> getList(String invokId, Pagination pagination) {
        QueryWrapper<DataInterfaceLogEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DataInterfaceLogEntity::getInvokId, invokId).orderByDesc(DataInterfaceLogEntity::getInvokTime);
        if (StringUtil.isNotEmpty(pagination.getKeyword())){
            queryWrapper.lambda().and(
                    t->t.like(DataInterfaceLogEntity::getUserId, pagination.getKeyword())
                            .or().like(DataInterfaceLogEntity::getInvokIp, pagination.getKeyword())
                            .or().like(DataInterfaceLogEntity::getInvokDevice, pagination.getKeyword())
                            .or().like(DataInterfaceLogEntity::getInvokType, pagination.getKeyword())
            );
        }
        // 排序
        queryWrapper.lambda().orderByDesc(DataInterfaceLogEntity::getInvokTime);
        Page<DataInterfaceLogEntity> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<DataInterfaceLogEntity> iPage = this.page(page, queryWrapper);
        return pagination.setData(iPage.getRecords(), page.getTotal());
    }

    @Override
    public List<DataInterfaceLogEntity> getListByIds(String appId,List<String> invokIds, PaginationIntrfaceLog pagination) {
        QueryWrapper<DataInterfaceLogEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DataInterfaceLogEntity::getOauthAppId,appId);
        queryWrapper.lambda().in(DataInterfaceLogEntity::getInvokId, invokIds).orderByDesc(DataInterfaceLogEntity::getInvokTime);
        if (StringUtil.isNotEmpty(pagination.getKeyword())){
            queryWrapper.lambda().and(
                    t->t.like(DataInterfaceLogEntity::getUserId, pagination.getKeyword())
                            .or().like(DataInterfaceLogEntity::getInvokIp, pagination.getKeyword())
                            .or().like(DataInterfaceLogEntity::getInvokDevice, pagination.getKeyword())
                            .or().like(DataInterfaceLogEntity::getInvokType, pagination.getKeyword())
            );
        }
        //日期范围（近7天、近1月、近3月、自定义）
        String startTime = pagination.getStartTime() != null ? pagination.getStartTime() : null;
        String endTime = pagination.getEndTime() != null ? pagination.getEndTime() : null;
        if (!StringUtil.isEmpty(startTime) && !StringUtil.isEmpty(endTime)) {
            Date startTimes = DateUtil.stringToDate(DateUtil.daFormatYmd(Long.parseLong(startTime)) + " 00:00:00");
            Date endTimes = DateUtil.stringToDate(DateUtil.daFormatYmd(Long.parseLong(endTime)) + " 23:59:59");
            queryWrapper.lambda().ge(DataInterfaceLogEntity::getInvokTime, startTimes).le(DataInterfaceLogEntity::getInvokTime, endTimes);
        }
        // 排序
        queryWrapper.lambda().orderByDesc(DataInterfaceLogEntity::getInvokTime);
        Page<DataInterfaceLogEntity> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<DataInterfaceLogEntity> iPage = this.page(page, queryWrapper);
        return pagination.setData(iPage.getRecords(), page.getTotal());
    }

}