package org.openea.eap.extj.database.datatype.limit;

import org.openea.eap.extj.database.datatype.db.DtMySQLEnum;
import org.openea.eap.extj.database.datatype.db.interfaces.DtInterface;
import org.openea.eap.extj.database.datatype.db.interfaces.DtLimitBase;
import org.openea.eap.extj.database.datatype.limit.base.DtLimitModel;
import org.openea.eap.extj.database.datatype.limit.util.DtLimitUtil;
import org.openea.eap.extj.database.datatype.model.DtModel;
import org.openea.eap.extj.database.datatype.model.DtModelDTO;

import java.util.function.BiFunction;

public class StringLimit extends DtLimitBase {
    public static final String CATEGORY = "type-String";
    public static final String JAVA_TYPE = "String";

    public String initDtCategory() {
        return "type-String";
    }

    public StringLimit(Boolean modify) {
        this.isModifyFlag = modify;
    }

    public DtModel convert(DtModelDTO dto) {
        DtInterface originEnum = dto.getDtEnum();
        DtInterface targetEnum = dto.getConvertTargetDtEnum();
        DtModel dataTypeModel = new DtModel(targetEnum);
        this.special(dto);
        if (this.isModifyFlag) {
            BiFunction<Long, DtLimitModel, Long> setMod = (inputVarLength, varLengthLm) -> {
                Long targetLength = DtLimitUtil.convertCharacter(inputVarLength, dto.getConvertType(), varLengthLm);
                dataTypeModel.setFormatLengthStr(targetLength.toString());
                return targetLength;
            };
            boolean originBigFlag = originEnum.getBitLengthLm() != null;
            boolean originCharFlag = originEnum.getCharLengthLm() != null;
            boolean targetBigFlag = targetEnum.getBitLengthLm() != null;
            boolean targetCharFlag = targetEnum.getCharLengthLm() != null;
            if (originBigFlag && targetCharFlag) {
                dataTypeModel.setCharLength((Long)setMod.apply(dto.getBitLength() / 3L, targetEnum.getCharLengthLm()));
            } else if (originCharFlag && targetBigFlag) {
                dataTypeModel.setBitLength((Long)setMod.apply(dto.getCharLength() * 3L, targetEnum.getBitLengthLm()));
            } else if (originBigFlag && targetBigFlag) {
                dataTypeModel.setBitLength((Long)setMod.apply(dto.getBitLength(), targetEnum.getBitLengthLm()));
            } else if (originCharFlag) {
                dataTypeModel.setCharLength((Long)setMod.apply(dto.getCharLength(), targetEnum.getCharLengthLm()));
            }
        }

        return dataTypeModel;
    }

    public void special(DtModelDTO dto) {
        DtInterface originEnum = dto.getDtEnum();
        if (originEnum.getDbType().equals("MySQL") && originEnum.getDataType().equals(DtMySQLEnum.BIT.getDataType()) && dto.getCharLength() == 1L) {
            dto.setCharLength(15L);
        }

    }

    public StringLimit() {
    }
}

