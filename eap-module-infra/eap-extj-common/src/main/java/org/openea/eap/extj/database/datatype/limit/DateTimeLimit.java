package org.openea.eap.extj.database.datatype.limit;

import org.openea.eap.extj.database.datatype.db.interfaces.DtLimitBase;
import org.openea.eap.extj.database.datatype.model.DtModel;
import org.openea.eap.extj.database.datatype.model.DtModelDTO;

public class DateTimeLimit extends DtLimitBase {
    public static final String CATEGORY = "type-DateTime";
    public static final String JAVA_TYPE = "date";

    public DateTimeLimit(Boolean modify) {
        this.isModifyFlag = modify;
    }

    public String initDtCategory() {
        return "type-DateTime";
    }

    public DtModel convert(DtModelDTO dtModelDTO) {
        DtModel dataTypeModel = new DtModel(dtModelDTO.getConvertTargetDtEnum());
        if (this.isModifyFlag) {
            dataTypeModel.setFormatLengthStr("");
        }

        return dataTypeModel;
    }

    public DateTimeLimit() {
    }
}
