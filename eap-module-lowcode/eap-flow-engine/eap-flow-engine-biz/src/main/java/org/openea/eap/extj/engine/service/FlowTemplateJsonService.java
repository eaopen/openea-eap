package org.openea.eap.extj.engine.service;

import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.engine.entity.FlowTemplateJsonEntity;
import org.openea.eap.extj.engine.model.flowengine.FlowPagination;
import org.openea.eap.extj.engine.model.flowtemplatejson.FlowTemplateJsonPage;
import org.openea.eap.extj.exception.WorkFlowException;

import java.util.List;

/**
 * 流程引擎
 *
 *
 */
public interface FlowTemplateJsonService extends SuperService<FlowTemplateJsonEntity> {

    /**
     * @return
     */
    List<FlowTemplateJsonEntity> getMonitorList();

    /**
     * 列表
     *
     * @return
     */
    List<FlowTemplateJsonEntity> getTemplateList(List<String> id);

    /**
     * 列表
     *
     * @return
     */
    List<FlowTemplateJsonEntity> getTemplateJsonList(List<String> id);

    /**
     * 分页列表
     *
     * @param page
     * @return
     */
    List<FlowTemplateJsonEntity> getListPage(FlowTemplateJsonPage page, boolean isPage);

    /**
     * 查询子流程
     *
     * @return
     */
    List<FlowTemplateJsonEntity> getChildListPage(FlowPagination page);

    /**
     * 获取主版本
     *
     * @param id
     * @return
     */
    List<FlowTemplateJsonEntity> getMainList(List<String> id);

    /**
     * 获取主版本
     *
     * @param id
     * @return
     */
    FlowTemplateJsonEntity getInfo(String id) throws WorkFlowException;


    /**
     * 获取主版本
     *
     * @param id
     * @return
     */
    FlowTemplateJsonEntity getJsonInfo(String id);

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    void create(FlowTemplateJsonEntity entity);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return
     */
    void update(String id, FlowTemplateJsonEntity entity);

    /**
     * 删除
     *
     * @param entity
     */
    void delete(FlowTemplateJsonEntity entity);

    /**
     * 删除表单id
     *
     * @param entity
     */
    void deleteFormFlowId(FlowTemplateJsonEntity entity);

    /**
     * 查询主版本流程
     *
     * @return
     */
    List<FlowTemplateJsonEntity> getListAll(List<String> id);

    /**
     * 设置主版本
     *
     * @param ids
     */
    void templateJsonMajor(String ids) throws WorkFlowException;

    /**
     * 获取消息发送配置id
     *
     * @param engine
     * @return
     */
    List<String> sendMsgConfigList(FlowTemplateJsonEntity engine);

    /**
     * 修改流程引擎名称
     *
     * @param groupId
     * @param fullName
     */
    void updateFullName(String groupId, String fullName);
}
