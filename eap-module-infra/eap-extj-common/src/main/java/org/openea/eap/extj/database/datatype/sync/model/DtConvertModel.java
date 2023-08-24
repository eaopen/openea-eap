package org.openea.eap.extj.database.datatype.sync.model;

import lombok.Data;
import org.openea.eap.extj.database.datatype.db.*;
import org.openea.eap.extj.database.datatype.db.interfaces.DtInterface;

import java.lang.reflect.Method;

@Data
public class DtConvertModel<T> {

    private T majorEnumClz;
    private DtMySQLEnum dtMySQLEnum;
    private DtOracleEnum dtOracleEnum;
    private DtSQLServerEnum dtSQLServerEnum;
    private DtDMEnum dtDMEnum;
    private DtKingbaseESEnum dtKingbaseESEnum;
    private DtPostgreSQLEnum dtPostgreSQLEnum;
    private DtDorisEnum dtDorisEnum;

    public DtInterface getDtEnum(String convertDbEncode) throws Exception {
        Method method = DtConvertModel.class.getMethod("getDt" + convertDbEncode + "Enum");
        return (DtInterface)method.invoke(this);
    }

    public void setDtEnum(DtInterface dtEnum) throws Exception {
        Method method = DtConvertModel.class.getMethod("setDt" + dtEnum.getDbType() + "Enum", DtInterface.getClz(dtEnum.getDbType()));
        method.invoke(this, dtEnum);
    }
}
