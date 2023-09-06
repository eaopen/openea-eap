package org.openea.eap.extj.database.datatype.limit;

import org.openea.eap.extj.database.datatype.db.interfaces.DtLimitBase;
import org.openea.eap.extj.database.datatype.limit.util.DtLimitUtil;
import org.openea.eap.extj.database.datatype.db.DtOracleEnum;
import org.openea.eap.extj.database.datatype.limit.model.DtModel;
import org.openea.eap.extj.database.datatype.limit.model.DtModelDTO;
import org.openea.eap.extj.database.source.DbBase;

/**
 * 数字数据类型
 *
 * 
 */
public class NumberLimit extends DtLimitBase {

    public final static String CATEGORY = "type-Number";
    public final static String JAVA_TYPE = "number";

    public NumberLimit(Boolean modify){
        this.isModifyFlag = modify;
    }

    @Override
    public String initDtCategory() {
        return CATEGORY;
    }

    @Override
    public DtModel convert(DtModelDTO viewDtModel){
        DtModel dataTypeModel;
        switch (viewDtModel.getDtEnum().getDtCategory()){
            case DecimalLimit.CATEGORY:
            case IntegerLimit.CATEGORY:
            case NumberLimit.CATEGORY:
                dataTypeModel = DtLimitUtil.convertNumeric(viewDtModel);
                break;
            default:
                dataTypeModel = new DtModel(viewDtModel.getDtEnum());
        }
        if(viewDtModel.getConvertTargetDtEnum().getIsModifyFlag()){
            if(viewDtModel.getConvertTargetDtEnum().getDbType().equals(DbBase.ORACLE)){
                if(dataTypeModel.getNumPrecision().equals(0) && dataTypeModel.getNumScale().equals(0)){
                    dataTypeModel.setNumPrecision(Integer.valueOf(DtOracleEnum.NUMBER.getNumPrecisionLm().getDefaults().toString()));
                    dataTypeModel.setNumScale(Integer.valueOf(DtOracleEnum.NUMBER.getNumScaleLm().getDefaults().toString()));
                }
            }
            DtLimitUtil.getNumericLength(dataTypeModel);
        }
        return dataTypeModel;
    }

}
