package org.openea.eap.extj.database.sql.param;

import org.openea.eap.extj.database.constant.DbAliasConst;
import org.openea.eap.extj.database.model.dbfield.JdbcColumnModel;
import org.openea.eap.extj.database.source.DbBase;

import java.util.Map;

/**
 * 类功能
 *
 */
public class FormatSqlKingbaseES {

    /**
     * 非空时空串报错，因Oracle空串存储为NULL，用一个空格代替空串
     */
    public static void nullValue(String dbEncode, JdbcColumnModel model, Map<String, Object> map){
        if(DbBase.KINGBASE_ES.equals(dbEncode)){
            // 字符串类型 && 字符串不为空 && 空串
            if(model.getValue() instanceof String && model.getNullSign().equals(DbAliasConst.NOT_NULL)
                    && model.getValue().toString().equals("")){
                map.put(model.getField(), " ");
            }
        }
    }

}
