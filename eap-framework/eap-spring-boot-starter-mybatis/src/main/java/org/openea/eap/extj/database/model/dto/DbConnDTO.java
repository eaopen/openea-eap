package org.openea.eap.extj.database.model.dto;

import lombok.Data;
import org.openea.eap.extj.database.model.interfaces.DbSourceOrDbLink;
import org.openea.eap.extj.database.source.DbBase;
import org.openea.eap.extj.database.util.DataSourceUtil;

import java.sql.Connection;
import java.util.function.Function;

@Data
public class DbConnDTO {

    private DbBase dbBase;
    private DbSourceOrDbLink dbSourceInfo;
    private Connection conn;
    private Function<String, Connection> connFunc;

    public DbConnDTO(DbBase dbBase, DataSourceUtil dbSource, Connection conn) {
        this.dbBase = dbBase;
        this.dbSourceInfo = dbSource;
        this.conn = conn;
    }
}
