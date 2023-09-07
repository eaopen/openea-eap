package org.openea.eap.extj.form.service;

import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.form.entity.SalesOrderEntity;
import org.openea.eap.extj.form.entity.SalesOrderEntryEntity;
import org.openea.eap.extj.form.model.salesorder.SalesOrderForm;

import java.util.List;

/**
 * 销售订单
 *
 *
 */
public interface SalesOrderService extends SuperService<SalesOrderEntity> {

    /**
     * 列表
     *
     * @param id 主键值
     * @return
     */
    List<SalesOrderEntryEntity> getSalesEntryList(String id);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    SalesOrderEntity getInfo(String id);

    /**
     * 保存
     *
     * @param id                        主键值
     * @param entity                    实体对象
     * @param salesOrderEntryEntityList 子表
     * @throws WorkFlowException 异常
     */
    void save(String id, SalesOrderEntity entity, List<SalesOrderEntryEntity> salesOrderEntryEntityList, SalesOrderForm form);

    /**
     * 提交
     *
     * @param id                        主键值
     * @param entity                    实体对象
     * @param salesOrderEntryEntityList 子表
     * @throws WorkFlowException 异常
     */
    void submit(String id, SalesOrderEntity entity, List<SalesOrderEntryEntity> salesOrderEntryEntityList, SalesOrderForm form);

    /**
     * 更改数据
     *
     * @param id   主键值
     * @param data 实体对象
     */
    void data(String id, String data);
}
