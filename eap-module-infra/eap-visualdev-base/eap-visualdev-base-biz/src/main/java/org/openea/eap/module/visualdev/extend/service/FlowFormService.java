package org.openea.eap.module.visualdev.extend.service;

import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.module.visualdev.extend.entity.FlowFormEntity;
import org.openea.eap.module.visualdev.extend.model.flow.FlowTempInfoModel;
import org.openea.eap.module.visualdev.extend.model.form.FlowFormPage;

import java.util.List;

public interface FlowFormService extends SuperService<FlowFormEntity> {

    /**
     * 判断名称是否重复
     *
     * @param fullName 名称
     * @param id       主键
     * @return ignore
     */
    boolean isExistByFullName(String fullName, String id);

    /**
     * 判断code是否重复
     *
     * @param enCode 名称
     * @param id       主键
     * @return ignore
     */
    boolean isExistByEnCode(String enCode, String id);
    /**
     * 创建
     * @param
     * @return
     * @copyright 引迈信息技术有限公司
     * @date 2022/9/29
     */
    Boolean create(FlowFormEntity entity) throws WorkFlowException;

    /**
     * 修改
     * @param
     * @return
     * @copyright 引迈信息技术有限公司
     * @date 2022/9/29
     */
    Boolean update(FlowFormEntity entity) throws Exception;
    /**
     * 查询列表
     * @param
     * @return
     * @copyright 引迈信息技术有限公司
     * @date 2022/7/1
     */
    List<FlowFormEntity> getList(FlowFormPage flowFormPage);
    /**
     * 查询列表
     * @param
     * @return
     * @copyright 引迈信息技术有限公司
     * @date 2022/7/1
     */
    List<FlowFormEntity> getListForSelect(FlowFormPage flowFormPage);
    /**
     * 发布/回滚
     * @param isRelease 是否发布：1-发布 0-回滚
     * @return
     * @copyright 引迈信息技术有限公司
     * @date 2022/7/1
     */
    ActionResult release(String id, Integer isRelease) throws WorkFlowException ;
    /**
     * 复制表单
     * @param
     * @return
     * @copyright 引迈信息技术有限公司
     * @date 2022/7/1
     */
    boolean copyForm(String id);
    /**
     * 导入表单
     * @param
     * @return
     * @copyright 引迈信息技术有限公司
     * @date 2022/7/1
     */
    ActionResult ImportData(FlowFormEntity entity) throws WorkFlowException;

    /**
     * 获取表单流程引擎
     * @param flowId
     * @return
     */
    List<FlowFormEntity> getFlowIdList(String flowId);

    /**
     * 获取流程信息
     * @param id
     * @return
     */
    FlowTempInfoModel getFormById(String id) throws WorkFlowException;

    /**
     * 修改流程的引擎id
     * @param entity
     */
    void updateForm(FlowFormEntity entity);

}