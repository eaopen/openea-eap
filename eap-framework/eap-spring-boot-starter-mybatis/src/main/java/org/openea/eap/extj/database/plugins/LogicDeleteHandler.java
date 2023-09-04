package org.openea.eap.extj.database.plugins;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;

import java.util.List;

public interface LogicDeleteHandler {
    Expression getNotDeletedValue();

    default String getLogicDeleteColumn() {
        return "F_DELETEMARK";
    }

    default String getDeleteSql() {
        return "UPDATE a SET F_DELETEMARK = 1";
    }

    default boolean ignoreTable(String tableName) {
        return false;
    }

    default boolean ignoreInsert(List<Column> columns, String tenantIdColumn) {
        return columns.stream().map(Column::getColumnName).anyMatch((i) -> {
            return i.equalsIgnoreCase(tenantIdColumn);
        });
    }
}