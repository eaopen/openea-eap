package org.openea.eap.module.visualdev.extend.service.impl;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.openea.eap.extj.engine.entity.FlowTaskEntity;
import org.openea.eap.extj.engine.model.flowtask.FlowTaskListModel;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.module.visualdev.extend.model.flowtask.PaginationFlowTask;
import org.openea.eap.module.visualdev.extend.service.FlowTaskService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * todo eap
 */
@Service
public class EapFlowTaskService implements FlowTaskService {
    @Override
    public List<FlowTaskEntity> getWaitList() {
        return null;
    }

    @Override
    public List<FlowTaskEntity> getMonitorList(PaginationFlowTask paginationFlowTask) {
        return null;
    }

    @Override
    public List<FlowTaskEntity> getLaunchList(PaginationFlowTask paginationFlowTask) {
        return null;
    }

    @Override
    public List<FlowTaskListModel> getWaitList(PaginationFlowTask paginationFlowTask) {
        return null;
    }

    @Override
    public List<FlowTaskListModel> getWaitListAll(PaginationFlowTask paginationFlowTask) {
        return null;
    }

    @Override
    public List<FlowTaskListModel> getTrialList() {
        return null;
    }

    @Override
    public List<FlowTaskListModel> getCirculateList() {
        return null;
    }

    @Override
    public List<FlowTaskListModel> getDashboardAllWaitList() {
        return null;
    }

    @Override
    public List<FlowTaskListModel> getTrialList(PaginationFlowTask paginationFlowTask) {
        return null;
    }

    @Override
    public List<FlowTaskListModel> getCirculateList(PaginationFlowTask paginationFlowTask) {
        return null;
    }

    @Override
    public FlowTaskEntity getInfo(String id) throws WorkFlowException {
        return null;
    }

    @Override
    public void update(FlowTaskEntity entity) {

    }

    @Override
    public void create(FlowTaskEntity entity) {

    }

    @Override
    public void createOrUpdate(FlowTaskEntity entity) {

    }

    @Override
    public FlowTaskEntity getInfoSubmit(String id, SFunction<FlowTaskEntity, ?>... columns) {
        return null;
    }

    @Override
    public List<FlowTaskEntity> getInfosSubmit(String[] ids, SFunction<FlowTaskEntity, ?>... columns) {
        return null;
    }

    @Override
    public void delete(FlowTaskEntity entity) throws WorkFlowException {

    }

    @Override
    public void deleteChildAll(List<String> id) {

    }

    @Override
    public void delete(String[] ids) throws WorkFlowException {

    }

    @Override
    public List<FlowTaskEntity> getOrderStaList(List<String> id) {
        return null;
    }

    @Override
    public List<FlowTaskEntity> getChildList(String id, SFunction<FlowTaskEntity, ?>... columns) {
        return null;
    }

    @Override
    public List<FlowTaskEntity> getTemplateIdList(String tempId) {
        return null;
    }

    @Override
    public List<FlowTaskEntity> getFlowList(String flowId) {
        return null;
    }

    @Override
    public void deleTaskAll(List<String> idList, List<String> idAll) {

    }

    @Override
    public List<String> getChildAllList(String id) {
        return null;
    }

    @Override
    public String getVisualFormId(String id) {
        return null;
    }

    @Override
    public void getChildList(String id, boolean suspend, List<String> list) {

    }
}
