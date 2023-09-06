package org.openea.eap.extj.base.util.dbutil;

import org.openea.eap.extj.database.constant.DbConst;

import java.util.Random;

/**
 * 表字段相关工具类
 *
 * 
 */
public class TableUtil {


    /**
     * 随机生成包含大小写字母及数字的字符串
     *
     * @param length
     * @return
     */
    public static String getStringRandom(int length) {
        String val = "";
        Random random = new Random();
        //参数length，表示生成几位随机数
        for (int i = 0; i < length; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //输出字母还是数字
            if ("char".equalsIgnoreCase(charOrNum)) {
                //输出是大写字母还是小写字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char) (random.nextInt(26) + temp);
            } else if ("num".equalsIgnoreCase(charOrNum)) {
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }

    /**
     * 检测自带表
     *
     * @param tableName 表明
     * @return ignore
     */
    public static Boolean checkByoTable(String tableName) {
        String[] tables = DbConst.BYO_TABLE.split(",");
        boolean exists;
        for (String table : tables) {
            exists = tableName.toLowerCase().equals(table);
            if (exists) {
                return true;
            }
        }
        return false;
    }

}
