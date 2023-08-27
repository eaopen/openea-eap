package org.openea.eap.extj.permission.service.impl;

import org.openea.eap.extj.permission.entity.PositionEntity;
import org.openea.eap.extj.permission.service.PositionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class EapPositionService implements PositionService {
    @Override
    public PositionEntity getInfo(String pos) {
        return null;
    }

    @Override
    public List<PositionEntity> getPosList(Set<String> posList) {
        return null;
    }

    @Override
    public Map<String, Object> getPosMap() {
        return null;
    }

    @Override
    public Map<String, Object> getPosEncodeAndName() {
        return null;
    }
}
