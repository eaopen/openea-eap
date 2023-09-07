package org.openea.eap.extj.extend.service;

import org.openea.eap.extj.base.Page;
import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.extend.entity.ProvinceEntity;

import java.util.List;

public interface ProvinceService extends SuperService<ProvinceEntity> {


    /**
     * 验证名称
     *
     * @param fullName 名称
     * @param id       主键值
     * @return ignore
     */
    boolean isExistByFullName(String fullName, String id);

    /**
     * 验证编码
     *
     * @param enCode 编码
     * @param id     主键值
     * @return ignore
     */
    boolean isExistByEnCode(String enCode, String id);

    /**
     * 普通列表
     *
     * @param parentId 节点Id
     * @return ignore
     */
    List<ProvinceEntity> getList(String parentId);

    /**
     * 普通列表
     *
     * @param parentId 节点Id
     * @param page
     * @return ignore
     */
    List<ProvinceEntity> getList(String parentId, Page page);

    /**
     * 普通列表
     *
     * @return ignore
     */
    List<ProvinceEntity> getAllList();


    /**
     * 省市区单条数据集合(代码生成器)
     * @param ProIdList 省市区id集合
     * @return
     */
    List<ProvinceEntity> getProList(List<String> ProIdList);

    /**
     * 地域名列表（在线开发）
     *
     * @return ignore
     */
    List<ProvinceEntity> getProListBytype(String type);

    /**
     * 信息
     *
     * @param id 主键值
     * @return ignore
     */
    ProvinceEntity getInfo(String id);

    /**
     * 信息
     *
     * @param fullName
     * @param parentId
     * @return ignore
     */
    ProvinceEntity getInfo(String fullName,List<String> parentId);


    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(ProvinceEntity entity);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(ProvinceEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return ignore
     */
    boolean update(String id, ProvinceEntity entity);

    /**
     * 上移
     *
     * @param id 主键值
     * @return ignore
     */
    boolean first(String id);

    /**
     * 下移
     *
     * @param id 主键值
     * @return ignore
     */
    boolean next(String id);

    /**
     * 获取行政规划列表
     *
     * @param list 数组
     * @return ignore
     */
    List<ProvinceEntity> infoList(List<String> list);
}