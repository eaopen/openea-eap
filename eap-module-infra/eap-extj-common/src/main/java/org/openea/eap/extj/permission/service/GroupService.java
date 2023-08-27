package org.openea.eap.extj.permission.service;

import org.openea.eap.extj.permission.entity.GroupEntity;

import java.util.List;
import java.util.Map;

public interface GroupService {
    GroupEntity getInfo(String va);

    Map<String, Object> getGroupMap();

    Map<String, Object> getGroupEncodeMap();

    List<GroupEntity> list();
}
