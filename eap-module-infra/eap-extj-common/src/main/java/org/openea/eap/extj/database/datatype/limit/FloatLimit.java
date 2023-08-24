package org.openea.eap.extj.database.datatype.limit;

import org.openea.eap.extj.database.datatype.db.interfaces.DtLimitBase;
import org.openea.eap.extj.database.datatype.limit.util.DtLimitUtil;
import org.openea.eap.extj.database.datatype.model.DtModel;
import org.openea.eap.extj.database.datatype.model.DtModelDTO;

public class FloatLimit extends DtLimitBase {
    public static final String CATEGORY = "type-Float";
    public static final String JAVA_TYPE = "float";

    public FloatLimit(Boolean modify) {
        this.isModifyFlag = modify;
    }

    public String initDtCategory() {
        return "type-Float";
    }

    public DtModel convert(DtModelDTO viewDtModel) {
        DtModel dataTypeModel = DtLimitUtil.convertNumeric(viewDtModel);
        if (this.isModifyFlag) {
            DtLimitUtil.getNumericLength(dataTypeModel);
        }

        return dataTypeModel;
    }

    public FloatLimit() {
    }
}
