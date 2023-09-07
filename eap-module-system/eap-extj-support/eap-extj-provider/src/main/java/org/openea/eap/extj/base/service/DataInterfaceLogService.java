package org.openea.eap.extj.base.service;

import org.openea.eap.extj.base.Pagination;
import org.openea.eap.extj.base.entity.DataInterfaceLogEntity;
import org.openea.eap.extj.base.model.InterfaceOauth.PaginationIntrfaceLog;

import java.util.List;

public interface DataInterfaceLogService extends SuperService<DataInterfaceLogEntity> {

    /**
     * 添加日志
     *
     * @param dateInterfaceId 接口Id
     * @param invokWasteTime  执行时间
     */
    void create(String dateInterfaceId, Integer invokWasteTime);
    /**
     * 通过权限判断添加日志
     *
     * @param dateInterfaceId 接口Id
     * @param invokWasteTime  执行时间
     */
    void create(String dateInterfaceId, Integer invokWasteTime,String appId,String invokType);

    /**
     * 获取调用日志列表
     *
     * @param invokId    接口id
     * @param pagination 分页参数
     * @return ignore
     */
    List<DataInterfaceLogEntity> getList(String invokId, Pagination pagination);


    /**
     * 获取调用日志列表(多id)
     *
     * @param invokIds    接口ids
     * @param pagination 分页参数
     * @return ignore
     */
    List<DataInterfaceLogEntity> getListByIds(String appId,List<String> invokIds, PaginationIntrfaceLog pagination);

}