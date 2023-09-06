package org.openea.eap.extj.database.sql.util;

/**
 * 类功能
 *
 * 
 */
public class SqlFrameFastUtil {

    /**
     * 增填权限
     */
    public final static String INSERT_AUTHORIZE = "INSERT INTO base_authorize (F_ID, F_ITEMTYPE, F_ITEMID, F_OBJECTTYPE, F_OBJECTID, F_SORTCODE, F_CREATORTIME, F_CREATORUSERID %COLUMN_KEY% ) VALUES  (?,?,?,?,?,?,?,? %COLUMN_PLACEHOLDER%)";
    public final static String INSERT_AUTHORIZE2 = "INSERT INTO base_authorize (F_ID, F_ITEMTYPE, F_ITEMID, F_OBJECTTYPE, F_OBJECTID, F_SORTCODE, F_CREATORTIME, F_CREATORUSERID %COLUMN_KEY% ) VALUES  (?,?,?,?,?,?,TO_DATE(?,'yyyy-mm-dd hh24:mi:ss'),? %COLUMN_PLACEHOLDER%)";
    public final static String AUTHOR_DEL = "DELETE FROM base_authorize WHERE (F_OBJECTID in( '{authorizeIds}') AND F_ITEMTYPE <> 'portal')";

}
