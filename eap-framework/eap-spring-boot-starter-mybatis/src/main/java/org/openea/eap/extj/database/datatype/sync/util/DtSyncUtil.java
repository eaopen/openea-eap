package org.openea.eap.extj.database.datatype.sync.util;

import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.database.datatype.db.interfaces.DtInterface;
import org.openea.eap.extj.database.datatype.sync.enums.DtConvertEnum;
import org.openea.eap.extj.database.datatype.sync.enums.DtConvertMultiEnum;
import org.openea.eap.extj.exception.DataException;

import java.util.Iterator;
import java.util.Map;

public class DtSyncUtil {

    public DtSyncUtil() {
    }

    public static DtInterface getToCovert(String fromDbType, String toDbType, String dataTypeName, Map<String, String> convertRuleMap) throws Exception {
        if (convertRuleMap != null) {
            Iterator var4 = convertRuleMap.keySet().iterator();

            while(var4.hasNext()) {
                String key = (String)var4.next();
                if (key.equalsIgnoreCase(dataTypeName)) {
                    String toDataType = (String)convertRuleMap.get(key);
                    return DtInterface.newInstanceByDt(toDataType, toDbType);
                }
            }
        }

        DtInterface formDtEnum = DtInterface.newInstanceByDt(dataTypeName, fromDbType);
        if (formDtEnum != null) {
            return getToFixCovert(formDtEnum, toDbType);
        } else {
            throw new DataException(MsgCode.DB005.get() + ":" + fromDbType + "(" + dataTypeName + ")");
        }
    }

    public static DtInterface getToFixCovert(DtInterface fromDtEnum, String toDbType) throws Exception {
        return DtConvertEnum.getConvertModel(fromDtEnum).getDtEnum(toDbType);
    }

    public static DtInterface[] getAllConverts(DtInterface fromDtEnum, String toDbType) throws Exception {
        DtConvertMultiEnum[] var2 = DtConvertMultiEnum.values();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            DtConvertMultiEnum convertEnum = var2[var4];
            if (convertEnum.getAllConverts().contains(fromDtEnum)) {
                return DtConvertMultiEnum.getConverts(toDbType, convertEnum);
            }
        }

        return null;
    }
}
