package org.openea.eap.module.visualdev.extend.service.impl;

import org.openea.eap.extj.engine.entity.FlowTaskEntity;
import org.openea.eap.extj.engine.model.flowtask.FlowTaskListModel;
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
}
