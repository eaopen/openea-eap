package org.openea.eap.module.visualdev.extend.service;

import org.openea.eap.extj.engine.entity.FlowTaskEntity;
import org.openea.eap.extj.engine.model.flowtask.FlowTaskListModel;

import java.util.List;

public interface FlowTaskService {
    List<FlowTaskEntity> getWaitList();

    List<FlowTaskListModel> getTrialList();

    List<FlowTaskListModel> getCirculateList();

    List<FlowTaskListModel> getDashboardAllWaitList();
}
