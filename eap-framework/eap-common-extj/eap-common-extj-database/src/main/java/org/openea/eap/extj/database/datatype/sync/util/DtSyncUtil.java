package org.openea.eap.extj.database.datatype.sync.util;

import org.openea.eap.extj.database.datatype.db.interfaces.DtInterface;
import org.openea.eap.extj.database.datatype.sync.enums.DtConvertEnum;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.database.datatype.sync.enums.DtConvertMultiEnum;
import org.openea.eap.extj.exception.DataException;

import java.util.Map;

/**
 * 类功能
 *
 * 
 */
public class DtSyncUtil {

    /**
     * 返回数据类型转换结果
     * @param fromDbType 被转化数据库类型
     * @param toDbType 转换数据数库类型
     * @param dataTypeName 转换数据类型
     * @param convertRuleMap 转换规则
     */
    public static DtInterface getToCovert(String fromDbType, String toDbType, String dataTypeName, Map<String, String> convertRuleMap) throws Exception {
        // 存在规则类型配对
        if(convertRuleMap != null) {
            for (String key : convertRuleMap.keySet()) {
                if (key.equalsIgnoreCase(dataTypeName)) {
                    String toDataType = convertRuleMap.get(key);
                    // 直接通过转换类型、数据库类型获取数据类型枚举
                    return DtInterface.newInstanceByDt(toDataType, toDbType);
                }
            }
        }
        // 获取被同步数据类型枚举
        DtInterface formDtEnum = DtInterface.newInstanceByDt(dataTypeName, fromDbType);
        if(formDtEnum != null){
            return getToFixCovert(formDtEnum, toDbType);
        }
        throw  new DataException(MsgCode.DB005.get() + ":" + fromDbType + "(" + dataTypeName + ")");
    }

    /**
     * 获取固定转换后数据类型枚举
     * @param fromDtEnum 被转换类型
     * @param toDbType 转换数据库类型
     * @return 数据类型枚举
     * @throws DataException ignore
     */
    public static DtInterface getToFixCovert(DtInterface fromDtEnum, String toDbType) throws Exception {
        return DtConvertEnum.getConvertModel(fromDtEnum).getDtEnum(toDbType);
    }

    /**
     * 获取所有可转换数据类型枚举集合
     * @param fromDtEnum 被转换类型
     * @param toDbType 转换数据库类型
     * @return 数据类型枚举集合
     * @throws Exception ignore
     */
    public static DtInterface[] getAllConverts(DtInterface fromDtEnum, String toDbType) throws Exception {
        for (DtConvertMultiEnum convertEnum : DtConvertMultiEnum.values()) {
            if(convertEnum.getAllConverts().contains(fromDtEnum)){
                return DtConvertMultiEnum.getConverts(toDbType, convertEnum);
            }
        }
        return null;
    }

}
