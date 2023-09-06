package org.openea.eap.extj.database.datatype.sync.util;

import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.database.datatype.db.*;
import org.openea.eap.extj.database.datatype.db.*;
import org.openea.eap.extj.database.datatype.db.*;
import org.openea.eap.extj.database.datatype.db.interfaces.DtInterface;
import org.openea.eap.extj.database.datatype.sync.enums.DtConvertMultiEnum;
import org.openea.eap.extj.database.source.DbBase;
import org.openea.eap.extj.exception.DataException;

import java.util.*;

/**
 * 测试数据类型完整性
 *
 * 
 */
public class DtSyncTest {

    public static void main(String[] args) throws Exception {
        checkDataType(true, true, true);
    }

    public static ActionResult<Map<String, List<String>>> getConvertRules(String fromDbType, String toDbType) throws Exception{
        Map<String, List<String>> map = new LinkedHashMap<>();
        for (DtInterface dtInterface : DtInterface.getClz(fromDbType).getEnumConstants()) {
            List<String> list = new LinkedList<>();
            DtInterface[] allConverts = DtSyncUtil.getAllConverts(dtInterface, toDbType);
            for (DtInterface allConvert : allConverts) {
                list.add(allConvert.getDataType());
            }
            map.put(dtInterface.getDataType(), list);
        }
        return ActionResult.success(map);
    }

    public static ActionResult<Map<String, String>> getDefaultRules(String fromDbType, String toDbType) throws Exception{
        Map<String, String> map = new LinkedHashMap<>();
        for (DtInterface dtInterface : DtInterface.getClz(fromDbType).getEnumConstants()) {
            DtInterface toFixCovert = DtSyncUtil.getToFixCovert(dtInterface, toDbType);
            if(toFixCovert != null){
                map.put(dtInterface.getDataType(), toFixCovert.getDataType());
            }else {
                throw  new DataException(MsgCode.DB006.get());
            }
        }
        return ActionResult.success(map);
    }

    /**
     * 检测各数据库
     * 数据类型对应关系是否缺失
     */
    public static void checkDataType(Boolean oneFlag, Boolean multiFlag, Boolean mutualFlag) throws Exception {
        // 1、检测一对一固定转换
        if(oneFlag){
            for (String fromDbEncode : DbBase.DB_ENCODES) {
                for (String toDbEncode : DbBase.DB_ENCODES) {
                    if(fromDbEncode.equals(toDbEncode)){
                        continue;
                    }
                    System.out.println("================ " + fromDbEncode + " 转 " + toDbEncode + " ==============");
                    Map<String, String> resultMap = getDefaultRules(fromDbEncode, toDbEncode).getData();
                    for (String key : resultMap.keySet()) {
                        System.out.println(key + getTab(key, fromDbEncode) + ": \t" + resultMap.get(key));
                    }
                }
            }
        }

        // 2、检测一对多选择转换
        if(multiFlag){
            System.out.println("\n\n");
            for (String fromDbEncode : DbBase.DB_ENCODES) {
                for (String toDbEncode : DbBase.DB_ENCODES) {
                    if(fromDbEncode.equals(toDbEncode)){
                        continue;
                    }
                    System.out.println("================ " + fromDbEncode + " 转 " + toDbEncode + " ==============");
                    Map<String, List<String>> resultMap = getConvertRules(fromDbEncode, toDbEncode).getData();
                    for (String key : resultMap.keySet()) {
                        System.out.println(key + getTab(key, fromDbEncode) + ": \t" + resultMap.get(key).toString());
                    }
                }
            }
        }

        // 3、多对多
        if(mutualFlag){
            System.out.println("\n\n");
            for (DtConvertMultiEnum value : DtConvertMultiEnum.values()) {
                System.out.println("================ " + value + " ==============");
                System.out.println(DbBase.MYSQL + "\t\t:" + Arrays.asList(value.getDtMySQLEnums()).toString());
                System.out.println(DbBase.ORACLE + "\t\t:" + Arrays.asList(value.getDtOracleEnums()).toString());
                System.out.println(DbBase.SQL_SERVER + "\t:" + Arrays.asList(value.getDtDMEnums()).toString());
                System.out.println(DbBase.DM + "\t\t\t:" + Arrays.asList(value.getDtSQLServerEnums()).toString());
                System.out.println(DbBase.KINGBASE_ES + "\t:" + Arrays.asList(value.getDtKingbaseESEnums()).toString());
                System.out.println(DbBase.POSTGRE_SQL + "\t:" + Arrays.asList(value.getDtPostgreSQLEnums()).toString());
            }
        }

        // 4、数据库类型
        System.out.println("\n\n");
        System.out.println("================ " + DbBase.MYSQL + " ==============");
        for (DtMySQLEnum value : DtMySQLEnum.values()) {
            System.out.println(value.getDataType());
        }
        System.out.println("================ " + DbBase.ORACLE + " ==============");
        for (DtOracleEnum value : DtOracleEnum.values()) {
            System.out.println(value.getDataType());
        }
        System.out.println("================ " + DbBase.SQL_SERVER + " ==============");
        for (DtSQLServerEnum value : DtSQLServerEnum.values()) {
            System.out.println(value.getDataType());
        }
        System.out.println("================ " + DbBase.DM + " ==============");
        for (DtDMEnum value : DtDMEnum.values()) {
            System.out.println(value.getDataType());
        }
        System.out.println("================ " + DbBase.KINGBASE_ES + " ==============");
        for (DtKingbaseESEnum value : DtKingbaseESEnum.values()) {
            System.out.println(value.getDataType());
        }
        System.out.println("================ " + DbBase.POSTGRE_SQL + " ==============");
        for (DtPostgreSQLEnum value : DtPostgreSQLEnum.values()) {
            System.out.println(value.getDataType());
        }

        // 5、数据库类型固定转换
//        System.out.println("\n\n");
//        for (DtConvertFixEnum value : DtConvertFixEnum.values()) {
//            System.out.println("================ " + value.name() + " ==============");
//            for (DtInterface dtMutualConvert : value.getDtMutualConverts()) {
//                System.out.println(dtMutualConvert.getDbType() + "\t:" + dtMutualConvert.getDataType());
//            }
//        }

    }

    // 制表符
    public static String getTab(String str, String dbType){
        int num = str.length();
        String tab = "\t\t";
        if(DbBase.ORACLE.equals(dbType) || DbBase.DM.equals(dbType)){
            if(num >= 8){
                tab = "\t";
            }
        }else {
            if(num > 8){
                tab = "\t";
            }
        }
        return tab;
    }

}
