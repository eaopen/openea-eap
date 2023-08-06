package org.openea.eap.extj.base.service;

import org.openea.eap.extj.base.entity.DictionaryTypeEntity;

public interface DictionaryTypeService {

    /**
     * 信息
     *
     * @param enCode 代码
     * @return ignore
     */
    DictionaryTypeEntity getInfoByEnCode(String enCode);
}
