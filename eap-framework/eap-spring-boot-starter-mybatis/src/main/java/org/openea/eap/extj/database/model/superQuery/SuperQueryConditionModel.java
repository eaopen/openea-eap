package org.openea.eap.extj.database.model.superQuery;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SuperQueryConditionModel<T> {

    private QueryWrapper<T> obj;
    private List<ConditionJsonModel> conditionList;
    private String matchLogic;
    private String tableName;
}
