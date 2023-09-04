package org.openea.eap.extj.database.datatype.limit;

import org.openea.eap.extj.database.datatype.db.interfaces.DtLimitBase;
import org.openea.eap.extj.database.datatype.limit.util.DtLimitUtil;
import org.openea.eap.extj.database.datatype.model.DtModel;
import org.openea.eap.extj.database.datatype.model.DtModelDTO;

public class DecimalLimit extends DtLimitBase {
    public static final String CATEGORY = "type-Decimal";
    public static final String JAVA_TYPE = "decimal";

    public DecimalLimit(Boolean modify) {
        this.dtCategory = "type-Decimal";
        this.isModifyFlag = modify;
    }

    public String initDtCategory() {
        return "type-Decimal";
    }

    public DtModel convert(DtModelDTO viewDtModel) {
        DtModel dataTypeModel = DtLimitUtil.convertNumeric(viewDtModel);
        if (this.isModifyFlag) {
            DtLimitUtil.getNumericLength(dataTypeModel);
        }

        return dataTypeModel;
    }
}