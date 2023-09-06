package org.openea.eap.extj.database.datatype.limit;

import org.openea.eap.extj.database.datatype.db.DtMySQLEnum;
import org.openea.eap.extj.database.datatype.db.interfaces.DtInterface;
import org.openea.eap.extj.database.datatype.db.interfaces.DtLimitBase;
import org.openea.eap.extj.database.datatype.limit.model.DtModel;
import org.openea.eap.extj.database.datatype.limit.model.DtModelDTO;
import org.openea.eap.extj.database.datatype.sync.util.DtSyncUtil;
import org.openea.eap.extj.database.source.DbBase;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 整型数据类型
 *
 * 
 */
@Data
@NoArgsConstructor
public class IntegerLimit extends DtLimitBase {

    public final static String CATEGORY = "type-Integer";
    public final static String JAVA_TYPE = "int";

    @Override
    public String initDtCategory() {
        return CATEGORY;
    }

    @Override
    public DtModel convert(DtModelDTO viewDtModel){
        DtInterface targetDtEnum = viewDtModel.getConvertTargetDtEnum();
        DtModel toModel = new DtModel(targetDtEnum);
        // 当转换成Oracle的数字类型
        if(targetDtEnum.getDtCategory().equals(NumberLimit.CATEGORY)){
            try{
                // 先当前数据库转成DtMySQL枚举
                DtMySQLEnum dtEnum = (DtMySQLEnum) DtSyncUtil.getToFixCovert(targetDtEnum, DbBase.MYSQL);
                // 在进行转换对比
                switch (dtEnum){
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
                        break;
                    default:
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if(this.isModifyFlag){
            toModel.setFormatLengthStr("");
        }
        return toModel;
    }

}
