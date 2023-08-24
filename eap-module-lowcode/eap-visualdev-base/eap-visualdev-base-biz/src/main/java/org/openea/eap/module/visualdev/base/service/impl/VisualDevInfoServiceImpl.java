package org.openea.eap.module.visualdev.base.service.impl;

import org.openea.eap.module.visualdev.base.entity.VisualdevEntity;
import org.openea.eap.module.visualdev.base.entity.VisualdevModelDataInfoVO;
import org.openea.eap.module.visualdev.base.service.VisualDevInfoService;
import org.springframework.stereotype.Service;

@Service
public class VisualDevInfoServiceImpl implements VisualDevInfoService {
    @Override
    public VisualdevModelDataInfoVO getEditDataInfo(String id, VisualdevEntity visualdevEntity) {
        return null;
    }

    @Override
    public VisualdevModelDataInfoVO getDetailsDataInfo(String id, VisualdevEntity visualdevEntity) {
        return null;
    }
}
