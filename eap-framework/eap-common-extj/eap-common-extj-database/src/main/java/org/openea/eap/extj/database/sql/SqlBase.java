package org.openea.eap.extj.database.sql;

import org.openea.eap.extj.database.model.dbfield.JdbcColumnModel;
import org.openea.eap.extj.database.model.interfaces.DbSourceOrDbLink;
import org.openea.eap.extj.database.source.DbBase;
import org.openea.eap.extj.database.source.impl.DbMySQL;
import org.openea.eap.extj.database.util.DbTypeUtil;
import org.openea.eap.extj.exception.DataException;
import lombok.Data;

import java.util.List;

/**
 * SQL语句模板基类
 * 用以一些SQL语句不同库的特殊处理
 *
 * 
 */
@Data
public abstract class SqlBase {

    /**
     * 数据基类
     */
    protected String dbEncode;




    protected DbBase getDb(){
        try {
            return DbTypeUtil.getEncodeDb(this.dbEncode);
        } catch (DataException e) {
            e.printStackTrace();
        }
        return new DbMySQL();
    }

    /**
     * 初始结构参数
     */
    public abstract void initStructParams(String table, DbSourceOrDbLink dbSourceOrDbLink);


    /**
     * 批量添加数据
     */
    // TODO 其余几个数据还没有添加方法
    public String batchInsertSql(List<List<JdbcColumnModel>> dataList, String table) {
        return "";
    }




}
