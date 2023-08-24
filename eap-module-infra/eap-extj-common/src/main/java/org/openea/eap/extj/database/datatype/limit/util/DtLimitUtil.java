package org.openea.eap.extj.database.datatype.limit.util;

import org.openea.eap.extj.database.datatype.db.interfaces.DtInterface;
import org.openea.eap.extj.database.datatype.limit.base.DtLimitModel;
import org.openea.eap.extj.database.datatype.model.DtModel;
import org.openea.eap.extj.database.datatype.model.DtModelDTO;

public class DtLimitUtil {
    public DtLimitUtil() {
    }

    public static DtModel convertNumeric(DtModelDTO dto) {
        DtInterface toDtEnum = dto.getConvertTargetDtEnum();
        DtModel toModel = new DtModel(toDtEnum);
        DtLimitModel numPrecisionLm = toDtEnum.getNumPrecisionLm();
        DtLimitModel numScaleLm = toDtEnum.getNumScaleLm();
        Integer numPrecision = dto.getNumPrecision();
        Integer numScale = dto.getNumScale();
        if (toDtEnum.getIsModifyFlag()) {
            switch (dto.getConvertType()) {
                case "DB_VAL":
                default:
                    break;
                case "FIX_VAL":
                    numPrecision = (Integer)numPrecisionLm.getFixed();
                    numScale = (Integer)numScaleLm.getFixed();
                    break;
                case "VIEW_VAL":
                    numPrecision = convertNum(numPrecision, (Integer)numPrecisionLm.getMin(), (Integer)numPrecisionLm.getMax(), (Integer)numPrecisionLm.getDefaults());
                    numScale = convertNum(numScale, (Integer)numScaleLm.getMin(), (Integer)numScaleLm.getMax(), (Integer)numScaleLm.getDefaults());
            }

            toModel.setNumPrecision(numPrecision);
            toModel.setNumScale(numScale);
            toModel.formatNumLength(numPrecision, numScale);
        }

        return toModel;
    }

    public static Long convertCharacter(Long inputVarLength, String convertType, DtLimitModel varLengthLm) {
        Long outVarLength = null;
        switch (convertType) {
            case "DB_VAL":
                outVarLength = inputVarLength;
                break;
            case "FIX_VAL":
                outVarLength = (Long)varLengthLm.getFixed();
                break;
            case "VIEW_VAL":
                outVarLength = convertLongNum(inputVarLength, (Long)varLengthLm.getMax(), (Long)varLengthLm.getDefaults());
        }

        return outVarLength;
    }

    public static void getNumericLength(DtModel model) {
        model.setFormatLengthStr(model.getNumPrecision() + "," + model.getNumScale());
    }

    private static Integer convertNum(Integer originNum, Integer toMin, Integer toMax, Integer defaultNum) {
        originNum = originNum != null && originNum >= toMin ? originNum : defaultNum;
        if (originNum >= toMin && toMax >= originNum) {
            return originNum;
        } else {
            return originNum > toMax ? toMax : toMin;
        }
    }

    private static Long convertLongNum(Long originNum, Long toMax, Long defaultNum) {
        originNum = originNum != null && originNum >= 1L ? originNum : defaultNum;
        return originNum <= toMax ? originNum : toMax;
    }
}
