package org.openea.base.api.query;

import java.util.List;

/**
 * 字段条件组合查询
 *
 */
public interface FieldLogic extends WhereClause {
    public List<WhereClause> getWhereClauses();
}
