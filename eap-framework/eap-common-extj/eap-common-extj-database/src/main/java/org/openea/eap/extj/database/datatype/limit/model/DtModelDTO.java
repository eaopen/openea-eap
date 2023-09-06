package org.openea.eap.extj.database.datatype.limit.model;

import org.openea.eap.extj.database.datatype.db.interfaces.DtInterface;
import org.openea.eap.extj.database.datatype.limit.base.DtModelBase;
import org.openea.eap.extj.database.datatype.utils.DataTypeUtil;
import org.openea.eap.extj.database.datatype.viewshow.ViewDataTypeEnum;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.util.StringUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 前端数据类型模型
 *
 * 
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class DtModelDTO extends DtModelBase {

    /**
     * 转换目标类型
     */
    private DtInterface convertTargetDtEnum;

    /**
     * 数据库取到的值(不用转换直接使用)
     */
    public final static String DB_VAL = "DB_VAL";
    /**
     * 使用固定值
     */
    public final static String FIX_VAL = "FIX_VAL";
    /**
     * 主要转换：前端传来的参数
     */
    public final static String VIEW_VAL = "VIEW_VAL";
    /**
     * 转换类型
     */
    private String convertType = VIEW_VAL;

    /**
     * 初始化创建表使用
     *
     * 析长度信息,例如 decimal(18,3) 中的 18,3
     * @param dataType 数据类型
     * @param dtLength 数据精度、标度信息
     * @param dbEncode 数据库类型
     */
    public DtModelDTO(String dataType, String dtLength, String dbEncode, boolean viewFlag) throws Exception {
        // 设置长度
        if (StringUtil.isEmpty(dtLength)) dtLength = "-1";
        if (dtLength.contains(",")) {
            String[] split = dtLength.split(",");
            if (DataTypeUtil.numFlag(split[0], split[1])) {
                charLength = Long.parseLong(split[0]);
                bitLength = Long.parseLong(split[0]);
                numPrecision = Integer.parseInt(split[0]);
                numScale = Integer.parseInt(split[1]);
            }
        } else {
            if (DataTypeUtil.numFlag(dtLength)) {
                charLength = Long.parseLong(dtLength);
                bitLength = Long.parseLong(dtLength);
                numPrecision = Integer.parseInt(dtLength);
            }
        }
        // 1、根据前端信息判断内置数据标准类型枚举
        if(viewFlag) dtEnum = DtInterface.newInstanceByView(dataType, dbEncode);
        // 2、直接使用前端数据类型作为数据库类型查询
        if (dtEnum == null) dtEnum = DtInterface.newInstanceByDt(dataType, dbEncode);
        if (dtEnum == null) new DataException("目前还未支持" + dbEncode + "数据类型：" + dataType).printStackTrace();
        convertTargetDtEnum = dtEnum;
    }

    /**
     * JdbcColumn初始化显示表使用
     *
     * @param charLength 字符串长度
     * @param numPrecision 精度
     * @param numScale 标度
     */
    public DtModelDTO(DtInterface dtEnum, Long charLength, Integer numPrecision, Integer numScale) {
        this.dtEnum = dtEnum;
        this.numPrecision = numPrecision;
        this.numScale = numScale;
        if (dtEnum.getCharLengthLm() != null) {
            this.charLength = charLength;
        } else if(dtEnum.getBitLengthLm() != null){
            this.bitLength = charLength;
        }
    }

    /**
     * 默认建表字符串数据类型（当数据类型失败时候，使用这个减低错误率）
     *
     * @param dbEncode 数据类型编码
     * @return 字符串数据类型Sql片段
     */
    public static String getStringFixedDt(String dbEncode) throws Exception {
        DtInterface dtEnum = DtInterface.newInstanceByView(ViewDataTypeEnum.VARCHAR.getViewFieldType(), dbEncode);
        if (dtEnum != null) {
            DtModelDTO dto = new DtModelDTO(dtEnum, -1L, -1, -1);
            dto.setConvertType(FIX_VAL);
            return dto.convert().formatDataType();
        }
        return "varchar(50)";
    }

    public DtModel convert() {
        return dtEnum.getDtLimit().convert(this);
    }

}
