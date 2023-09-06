package org.openea.eap.extj.database.datatype.db.interfaces;

import org.openea.eap.extj.database.datatype.limit.base.DtLimitModel;
import org.openea.eap.extj.database.datatype.limit.model.DtModel;
import org.openea.eap.extj.database.datatype.limit.model.DtModelDTO;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

/**
 * 数据类型：长度精度限制
 * default:为可变长度时的默认值
 * fixed:不可变长度的固有值
 *
 * 
 */
@Data
public abstract class DtLimitBase {

    {
        this.dtCategory = initDtCategory();
    }

    /**
     * 数据类型 - 归属
     */
    protected String dtCategory;

    /**
     * 数据类型 - 可修改表示
     * true:可修改  false:不可修改
     */
    protected Boolean isModifyFlag = false;

    private String javaType;

    /**
     * 默认最短长度
     */
    private final static Integer ZERO_MIN_LENGTH = 0;
    private final static Integer ONE_MIN_LENGTH = 1;

    /* 数据类型 - 长度限制模型 =========== */
    /**
     * 字符长度
     * 会显示类似5E+1长度的字符串,例如：4294967295，固Long类型
     */
    @Getter(AccessLevel.PACKAGE)
    private DtLimitModel charLengthLm;

    /**
     * 字节长度
     */
    @Getter(AccessLevel.PACKAGE)
    private DtLimitModel bitLengthLm;

    /**
     * 精度（数值整体长度）
     * >= 1
     */
    @Getter(AccessLevel.PACKAGE)
    private DtLimitModel numPrecisionLm;

    /**
     * 标度（数值小数点长度）
     * >= 0 && 标度 < 精度
     * 默认为: 0
     */
    @Getter(AccessLevel.PACKAGE)
    private DtLimitModel numScaleLm;

    /**
     * 设置数据归档类型
     */
    public abstract String initDtCategory();

    /**
     * 转换
     * 特殊：MySQL的varchar作为主键的长度是一个特殊值
     * @param dtModelDTO 前端数据类型模型
     * @return 数据类型模型
     */
    public abstract DtModel convert(DtModelDTO dtModelDTO);


    /* ======================================== */

    /**
     * 精度
     */
    public DtLimitBase precision(Integer precisionMax, Integer precisionDefault){
        this.numPrecisionLm = new DtLimitModel(precisionMax, ONE_MIN_LENGTH, precisionDefault);
        return this;
    }

    /**
     * 标度
     */
    public DtLimitBase scale(Integer scaleMax, Integer scaleDefault){
        return scale(scaleMax, ZERO_MIN_LENGTH, scaleDefault);
    }

    public DtLimitBase scale(Integer scaleMax, Integer scaleMin, Integer scaleDefault){
        this.numScaleLm = new DtLimitModel(scaleMax, scaleMin, scaleDefault);
        return this;
    }

    /**
     * 字符长度
     */

    public DtLimitBase charLength(Long charLengthMax, Long defaultLength){
        this.charLengthLm = new DtLimitModel(charLengthMax, ONE_MIN_LENGTH, defaultLength);
        return this;
    }

    /**
     * 字节长度
     */
    public DtLimitBase bitLength(Long bitLengthMax, Long defaultLength){
        this.bitLengthLm = new DtLimitModel(bitLengthMax, ONE_MIN_LENGTH, defaultLength);
        return this;
    }

    /* ==================== 固定长度（当没有输入默认长度时，为固定长度） ====================== */

    /**
     * 固定精度标度
     */
    public DtLimitBase fixedScale(Integer scaleFixed){
        this.numScaleLm = new DtLimitModel(scaleFixed);
        return this;
    }

    public DtLimitBase fixedPrecision(Integer precisionFixed){
        this.numPrecisionLm = new DtLimitModel(precisionFixed);
        return this;
    }

    public DtLimitBase fixedBitLength(Long bitLengthFixed){
        this.bitLengthLm = new DtLimitModel(bitLengthFixed);
        return this;
    }

    public DtLimitBase fixedCharLength(Long charLengthFixed){
        this.charLengthLm = new DtLimitModel(charLengthFixed);
        return this;
    }

}
