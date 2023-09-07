package org.openea.eap.extj.extend.service;


import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.extend.entity.ProvinceAtlasEntity;

import java.util.List;

/**
 * 行政区划
 *
 */
public interface ProvinceAtlasService extends SuperService<ProvinceAtlasEntity> {

    List<ProvinceAtlasEntity> getList();

    List<ProvinceAtlasEntity> getListByPid(String pid);
}
