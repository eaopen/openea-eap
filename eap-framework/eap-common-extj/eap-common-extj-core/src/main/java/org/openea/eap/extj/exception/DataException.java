package org.openea.eap.extj.exception;

import org.openea.eap.extj.constant.MsgCode;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库异常类
 *
 * 
 */
public class DataException extends RuntimeException {

    public DataException(){
        super();
    }

    public DataException(String message) {
        super(message);
    }

    public static DataException errorLink(String warning) {
        return new DataException(MsgCode.DB002.get() + warning );
    }

    /**
     * mysql表重复
     * @param error 错误信息
     */
    public static DataException tableExists(String error,Connection rollbackConn){
        executeRollback(rollbackConn);
        //Mysql英文报错，临时解决方案
        error = error.replace("Table","表").replace("already exists","已经存在。");
        return new DataException(error);
    }

    public static SQLException rollbackDataException(SQLException e, Connection rollbackConn) {
        executeRollback(rollbackConn);
        return e;
    }

    private static void executeRollback(Connection conn){
        try {
            conn.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
