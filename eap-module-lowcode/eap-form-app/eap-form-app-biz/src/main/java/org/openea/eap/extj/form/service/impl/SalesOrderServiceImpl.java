package org.openea.eap.extj.form.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.extj.form.mapper.SalesOrderMapper;
import org.openea.eap.extj.form.service.SalesOrderEntryService;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.form.entity.SalesOrderEntity;
import org.openea.eap.extj.form.entity.SalesOrderEntryEntity;
import org.openea.eap.extj.form.model.salesorder.SalesOrderEntryEntityInfoModel;
import org.openea.eap.extj.form.model.salesorder.SalesOrderForm;
import org.openea.eap.extj.form.service.SalesOrderService;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.RandomUtil;
import org.openea.eap.extj.util.ServiceAllUtil;
import org.openea.eap.extj.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 销售订单
 *
 * 
 */
@Service
public class SalesOrderServiceImpl extends SuperServiceImpl<SalesOrderMapper, SalesOrderEntity> implements SalesOrderService {

    @Autowired
    private ServiceAllUtil serviceAllUtil;
    @Autowired
    private SalesOrderEntryService salesOrderEntryService;

    @Override
    public List<SalesOrderEntryEntity> getSalesEntryList(String id) {
        QueryWrapper<SalesOrderEntryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SalesOrderEntryEntity::getSalesOrderId, id).orderByDesc(SalesOrderEntryEntity::getSortCode);
        return salesOrderEntryService.list(queryWrapper);
    }

    @Override
    public SalesOrderEntity getInfo(String id) {
        QueryWrapper<SalesOrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SalesOrderEntity::getId, id);
        return getOne(queryWrapper);
    }

    @Override
    @DSTransactional
    public void save(String id, SalesOrderEntity entity, List<SalesOrderEntryEntity> salesOrderEntryEntityList, SalesOrderForm form) {
        //表单信息
        if (StringUtil.isEmpty(entity.getId())) {
            entity.setId(id);
            for (int i = 0; i < salesOrderEntryEntityList.size(); i++) {
                salesOrderEntryEntityList.get(i).setId(RandomUtil.uuId());
                salesOrderEntryEntityList.get(i).setSalesOrderId(entity.getId());
                salesOrderEntryEntityList.get(i).setSortCode(Long.parseLong(i + ""));
                salesOrderEntryService.save(salesOrderEntryEntityList.get(i));
            }
            //创建
            save(entity);
            serviceAllUtil.useBillNumber("WF_SalesOrderNo");
        } else {
            entity.setId(id);
            QueryWrapper<SalesOrderEntryEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(SalesOrderEntryEntity::getSalesOrderId, entity.getId());
            salesOrderEntryService.remove(queryWrapper);
            for (int i = 0; i < salesOrderEntryEntityList.size(); i++) {
                salesOrderEntryEntityList.get(i).setId(RandomUtil.uuId());
                salesOrderEntryEntityList.get(i).setSalesOrderId(entity.getId());
                salesOrderEntryEntityList.get(i).setSortCode(Long.parseLong(i + ""));
                salesOrderEntryService.save(salesOrderEntryEntityList.get(i));
            }
            //编辑
            updateById(entity);
        }
    }

    @Override
    @DSTransactional
    public void submit(String id, SalesOrderEntity entity, List<SalesOrderEntryEntity> salesOrderEntryEntityList, SalesOrderForm form) {
        //表单信息
        if (StringUtil.isEmpty(entity.getId())) {
            entity.setId(id);
            for (int i = 0; i < salesOrderEntryEntityList.size(); i++) {
                salesOrderEntryEntityList.get(i).setId(RandomUtil.uuId());
                salesOrderEntryEntityList.get(i).setSalesOrderId(entity.getId());
                salesOrderEntryEntityList.get(i).setSortCode(Long.parseLong(i + ""));
                salesOrderEntryService.save(salesOrderEntryEntityList.get(i));
            }
            //创建
            save(entity);
            serviceAllUtil.useBillNumber("WF_SalesOrderNo");
        } else {
            entity.setId(id);
            QueryWrapper<SalesOrderEntryEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(SalesOrderEntryEntity::getSalesOrderId, entity.getId());
            salesOrderEntryService.remove(queryWrapper);
            for (int i = 0; i < salesOrderEntryEntityList.size(); i++) {
                salesOrderEntryEntityList.get(i).setId(RandomUtil.uuId());
                salesOrderEntryEntityList.get(i).setSalesOrderId(entity.getId());
                salesOrderEntryEntityList.get(i).setSortCode(Long.parseLong(i + ""));
                salesOrderEntryService.save(salesOrderEntryEntityList.get(i));
            }
            //编辑
            updateById(entity);
        }
    }

    @Override
    public void data(String id, String data) {
        SalesOrderForm salesOrderForm = JsonUtil.getJsonToBean(data, SalesOrderForm.class);
        SalesOrderEntity entity = JsonUtil.getJsonToBean(salesOrderForm, SalesOrderEntity.class);
        List<SalesOrderEntryEntityInfoModel> entryList = salesOrderForm.getEntryList() != null ? salesOrderForm.getEntryList() : new ArrayList<>();
        List<SalesOrderEntryEntity> salesOrderEntryEntityList = JsonUtil.getJsonToList(entryList, SalesOrderEntryEntity.class);
        entity.setId(id);
        QueryWrapper<SalesOrderEntryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SalesOrderEntryEntity::getSalesOrderId, entity.getId());
        salesOrderEntryService.remove(queryWrapper);
        for (int i = 0; i < salesOrderEntryEntityList.size(); i++) {
            salesOrderEntryEntityList.get(i).setId(RandomUtil.uuId());
            salesOrderEntryEntityList.get(i).setSalesOrderId(entity.getId());
            salesOrderEntryEntityList.get(i).setSortCode(Long.parseLong(i + ""));
            salesOrderEntryService.save(salesOrderEntryEntityList.get(i));
        }
        //编辑
        saveOrUpdate(entity);
    }

}
