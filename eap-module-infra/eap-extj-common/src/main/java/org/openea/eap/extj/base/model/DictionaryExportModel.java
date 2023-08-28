package org.openea.eap.extj.base.model;

import lombok.Data;
import org.openea.eap.extj.base.entity.DictionaryTypeEntity;

import java.io.Serializable;
import java.util.List;

@Data
public class DictionaryExportModel implements Serializable {

    /**
     * 字典分类
     */
    private List<DictionaryTypeEntity> list;

    /**
     * 数据集合
     */
    private List<DictionaryDataExportModel> modelList;

}
