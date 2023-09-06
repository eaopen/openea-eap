package org.openea.eap.extj.database.datatype.limit;

import org.openea.eap.extj.database.datatype.db.interfaces.DtLimitBase;
import org.openea.eap.extj.database.datatype.limit.model.DtModel;
import org.openea.eap.extj.database.datatype.limit.model.DtModelDTO;
import org.openea.eap.extj.database.datatype.limit.util.DtLimitUtil;
import lombok.NoArgsConstructor;

/**
 * 浮点数据类型
 *
 * 
 */
@NoArgsConstructor
public class FloatLimit extends DtLimitBase {

    public final static String CATEGORY = "type-Float";
    public final static String JAVA_TYPE = "float";

    public FloatLimit(Boolean modify){
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
