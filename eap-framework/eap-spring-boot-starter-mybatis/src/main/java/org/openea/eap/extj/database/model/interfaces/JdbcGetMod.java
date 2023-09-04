package org.openea.eap.extj.database.model.interfaces;

import org.openea.eap.extj.database.model.dto.ModelDTO;

import java.sql.SQLException;

public interface JdbcGetMod {
    void setMod(ModelDTO var1) throws SQLException;
}
