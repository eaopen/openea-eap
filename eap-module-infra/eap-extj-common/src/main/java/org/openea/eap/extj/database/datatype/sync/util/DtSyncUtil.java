package org.openea.eap.extj.database.datatype.sync.util;

import org.openea.eap.extj.database.datatype.db.interfaces.DtInterface;
import org.openea.eap.extj.database.datatype.sync.enums.DtConvertEnum;

public class DtSyncUtil {


    public static DtInterface getToFixCovert(DtInterface fromDtEnum, String toDbType) throws Exception {
        return DtConvertEnum.getConvertModel(fromDtEnum).getDtEnum(toDbType);
    }
}
