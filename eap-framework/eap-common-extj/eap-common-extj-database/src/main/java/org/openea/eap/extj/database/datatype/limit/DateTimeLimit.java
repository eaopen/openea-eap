package org.openea.eap.extj.database.datatype.limit;

import org.openea.eap.extj.database.datatype.db.interfaces.DtLimitBase;
import org.openea.eap.extj.database.datatype.limit.model.DtModel;
import org.openea.eap.extj.database.datatype.limit.model.DtModelDTO;
import lombok.NoArgsConstructor;

/**
 * 时间数据类型
 *
 * 
 */
@NoArgsConstructor
public class DateTimeLimit extends DtLimitBase {

    public final static String CATEGORY = "type-DateTime";
    public final static String JAVA_TYPE = "date";

    public DateTimeLimit(Boolean modify){
        this.isModifyFlag = modify;
    }

    @Override
    public String initDtCategory() {
        return CATEGORY;
    }

    @Override
    public DtModel convert(DtModelDTO dtModelDTO){
        DtModel dataTypeModel = new DtModel(dtModelDTO.getConvertTargetDtEnum());
        if(this.isModifyFlag){
            dataTypeModel.setFormatLengthStr("");
        }
        return dataTypeModel;
    }

}
