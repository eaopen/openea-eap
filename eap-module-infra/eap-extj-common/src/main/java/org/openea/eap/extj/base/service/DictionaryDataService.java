package org.openea.eap.extj.base.service;

import org.openea.eap.extj.base.entity.DictionaryDataEntity;

import java.util.List;

/**
 * 字典数据
 *
 * todo eap待处理
 *
 */
public interface DictionaryDataService  {


    List<DictionaryDataEntity> getDicList(String id);

    DictionaryDataEntity getInfo(String portalCategoryId);

    List<DictionaryDataEntity> getList(String id);
}
