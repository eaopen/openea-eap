package org.openea.eap.extj.database.datatype.limit;

import org.openea.eap.extj.database.datatype.db.interfaces.DtLimitBase;
import org.openea.eap.extj.database.datatype.limit.util.DtLimitUtil;
import org.openea.eap.extj.database.datatype.limit.model.DtModel;
import org.openea.eap.extj.database.datatype.limit.model.DtModelDTO;

/**
 * 小数数据类型
 *
 * 
 */
public class DecimalLimit extends DtLimitBase {

    public final static String CATEGORY = "type-Decimal";
    public final static String JAVA_TYPE = "decimal";
    {this.dtCategory = CATEGORY;}

    public DecimalLimit(Boolean modify) {
        this.isModifyFlag = modify;
    }

    @Override
    public String initDtCategory() {
        return CATEGORY;
    }

    @Override
    public DtModel convert(DtModelDTO viewDtModel){
        DtModel dataTypeModel = DtLimitUtil.convertNumeric(viewDtModel);
        if(this.isModifyFlag){
            DtLimitUtil.getNumericLength(dataTypeModel);
        }
        return dataTypeModel;
    }

}
