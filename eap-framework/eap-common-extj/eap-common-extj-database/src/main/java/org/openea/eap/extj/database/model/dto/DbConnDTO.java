package org.openea.eap.extj.database.model.dto;

import org.openea.eap.extj.database.model.interfaces.DbSourceOrDbLink;
import org.openea.eap.extj.database.source.DbBase;
import org.openea.eap.extj.database.util.DataSourceUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.util.function.Function;

/**
 * 数据连接相关数据传输对象
 *
 *
 */
@Data
@NoArgsConstructor
public class DbConnDTO {

    public DbConnDTO(DbBase dbBase, DataSourceUtil dbSource, Connection conn){
        this.dbBase = dbBase;
        this.dbSourceInfo = dbSource;
        this.conn = conn;
    }

    /**
     * 数据库基类
     */
    private DbBase dbBase;

    /**
     * 数据源信息
     */
    private DbSourceOrDbLink dbSourceInfo;

    /**
     * 数据连接
     */
    private Connection conn;


    private Function<String, Connection> connFunc;

}
