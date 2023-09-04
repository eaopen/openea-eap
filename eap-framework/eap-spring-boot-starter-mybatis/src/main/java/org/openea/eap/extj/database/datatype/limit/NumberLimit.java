package org.openea.eap.extj.database.datatype.limit;

import org.openea.eap.extj.database.datatype.db.DtOracleEnum;
import org.openea.eap.extj.database.datatype.db.interfaces.DtLimitBase;
import org.openea.eap.extj.database.datatype.limit.util.DtLimitUtil;
import org.openea.eap.extj.database.datatype.model.DtModel;
import org.openea.eap.extj.database.datatype.model.DtModelDTO;
public class NumberLimit extends DtLimitBase {
    public static final String CATEGORY = "type-Number";
    public static final String JAVA_TYPE = "number";

    public NumberLimit(Boolean modify) {
        this.isModifyFlag = modify;
    }

    public String initDtCategory() {
        return "type-Number";
    }

    public DtModel convert(DtModelDTO viewDtModel) {
        DtModel dataTypeModel;
        switch (viewDtModel.getDtEnum().getDtCategory()) {
            case "type-Decimal":
            case "type-Integer":
            case "type-Number":
                dataTypeModel = DtLimitUtil.convertNumeric(viewDtModel);
                break;
            default:
                dataTypeModel = new DtModel(viewDtModel.getDtEnum());
        }

        if (viewDtModel.getConvertTargetDtEnum().getIsModifyFlag()) {
            if (viewDtModel.getConvertTargetDtEnum().getDbType().equals("Oracle") && dataTypeModel.getNumPrecision().equals(0) && dataTypeModel.getNumScale().equals(0)) {
                dataTypeModel.setNumPrecision(Integer.valueOf(DtOracleEnum.NUMBER.getNumPrecisionLm().getDefaults().toString()));
                dataTypeModel.setNumScale(Integer.valueOf(DtOracleEnum.NUMBER.getNumScaleLm().getDefaults().toString()));
            }

            DtLimitUtil.getNumericLength(dataTypeModel);
        }

        return dataTypeModel;
    }
}
