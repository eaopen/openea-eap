package org.openea.eap.extj.permission.service;

import org.openea.eap.extj.permission.entity.PositionEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PositionService {
    PositionEntity getInfo(String pos);

    List<PositionEntity> getPosList(Set<String> posList);

    Map<String, Object> getPosMap();

    Map<String, Object> getPosEncodeAndName();
}
