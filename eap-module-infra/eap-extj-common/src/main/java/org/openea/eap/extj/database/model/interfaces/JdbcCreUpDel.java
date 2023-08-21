package org.openea.eap.extj.database.model.interfaces;

import org.openea.eap.extj.exception.DataException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@FunctionalInterface
public interface JdbcCreUpDel<T> {
    T execute() throws SQLException;

    static void setData(PreparedStatement preparedStatement, List<?> data) throws SQLException {
        if (data != null) {
            for(int i = 0; i < data.size(); ++i) {
                preparedStatement.setObject(i + 1, data.get(i));
            }
        }

    }

    static <T> T get(Connection conn, JdbcCreUpDel<T> creUpDel) throws SQLException {
        try {
            conn.setAutoCommit(false);
            T result = creUpDel.execute();
            conn.commit();
            return result;
        } catch (SQLException var3) {
            throw DataException.rollbackDataException(var3, conn);
        }
    }
}
