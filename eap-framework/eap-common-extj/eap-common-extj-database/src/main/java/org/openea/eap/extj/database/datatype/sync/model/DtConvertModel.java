package org.openea.eap.extj.database.datatype.sync.model;

import org.openea.eap.extj.database.datatype.db.*;
import org.openea.eap.extj.database.datatype.db.*;
import org.openea.eap.extj.database.datatype.db.interfaces.DtInterface;
import org.openea.eap.extj.database.datatype.db.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

/**
 * 数据类型互相转换模型
 *
 * 
 */
@Data
@NoArgsConstructor
public class DtConvertModel<T> {

    private T majorEnumClz;

    /**
     * 各数据库数据类型枚举
     */
    private DtMySQLEnum dtMySQLEnum;
    private DtOracleEnum dtOracleEnum;
    private DtSQLServerEnum dtSQLServerEnum;
    private DtDMEnum dtDMEnum;
    private DtKingbaseESEnum dtKingbaseESEnum;
    private DtPostgreSQLEnum dtPostgreSQLEnum;
    private DtDorisEnum dtDorisEnum;

    /**
     * 获取数据类型枚举
     * @param convertDbEncode 转换数据库类型
     * @return ignore
     * @throws Exception ignore
     */
    public DtInterface getDtEnum(String convertDbEncode) throws Exception {
        Method method = DtConvertModel.class.getMethod("getDt" + convertDbEncode + "Enum");
        return (DtInterface) method.invoke(this);
    }

    public void setDtEnum(DtInterface dtEnum) throws Exception {
        Method method = DtConvertModel.class.getMethod("setDt" + dtEnum.getDbType() + "Enum", DtInterface.getClz(dtEnum.getDbType()));
        method.invoke(this, dtEnum);
    }

}
