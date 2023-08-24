package org.openea.eap.extj.database.datatype.limit;

import org.openea.eap.extj.database.datatype.db.DtMySQLEnum;
import org.openea.eap.extj.database.datatype.db.interfaces.DtInterface;
import org.openea.eap.extj.database.datatype.db.interfaces.DtLimitBase;
import org.openea.eap.extj.database.datatype.model.DtModel;
import org.openea.eap.extj.database.datatype.model.DtModelDTO;
import org.openea.eap.extj.database.datatype.sync.util.DtSyncUtil;

public class IntegerLimit extends DtLimitBase {
    public static final String CATEGORY = "type-Integer";
    public static final String JAVA_TYPE = "int";

    public String initDtCategory() {
        return "type-Integer";
    }

    public DtModel convert(DtModelDTO viewDtModel) {
        DtInterface targetDtEnum = viewDtModel.getConvertTargetDtEnum();
        DtModel toModel = new DtModel(targetDtEnum);
        if (targetDtEnum.getDtCategory().equals("type-Number")) {
            try {
                DtMySQLEnum dtEnum = (DtMySQLEnum) DtSyncUtil.getToFixCovert(targetDtEnum, "MySQL");
                switch (dtEnum) {
                    case TINY_INT:
                        toModel.setNumPrecision(3);
                        break;
                    case SMALL_INT:
                        toModel.setNumPrecision(5);
                        break;
                    case MEDIUM_INT:
                        toModel.setNumPrecision(7);
                        break;
                    case INT:
                        toModel.setNumPrecision(10);
                        break;
                    case BIGINT:
                        toModel.setNumPrecision(19);
                }
            } catch (Exception var5) {
                var5.printStackTrace();
            }
        }

        if (this.isModifyFlag) {
            toModel.setFormatLengthStr("");
        }

        return toModel;
    }
}
