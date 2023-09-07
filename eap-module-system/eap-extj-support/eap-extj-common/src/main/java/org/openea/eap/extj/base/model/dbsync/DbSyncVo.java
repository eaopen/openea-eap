package org.openea.eap.extj.base.model.dbsync;

import lombok.Data;
import org.openea.eap.extj.database.model.dbtable.DbTableFieldModel;

import java.util.List;
import java.util.Map;

@Data
public class DbSyncVo {

    /**
     * 验证结果
     */
    private Boolean checkDbFlag;

    /**
     * 表集合
     */
    private List<DbTableFieldModel> tableList;

    /**
     * 转换规则
     */
    private Map<String, List<String>> convertRuleMap;

}