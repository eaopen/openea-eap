package org.openea.eap.extj.database.datatype.limit.model;


import org.openea.eap.extj.database.datatype.db.interfaces.DtInterface;
import org.openea.eap.extj.database.datatype.limit.base.DtModelBase;
import org.openea.eap.extj.util.StringUtil;
import lombok.Data;

/**
 * 数据类型模型
 *
 * 
 */
@Data
public class DtModel extends DtModelBase {

    /**
     * 显示长度
     */
    private String formatLengthStr;


    public DtModel(DtInterface dtEnum){
        this.dtEnum = dtEnum;
    }

    /**
     * 表字段数据类型名
     */
    public String getDataType(){
        return this.dtEnum.getDataType();
    }

    /**
     * java数据类型
     */
    public String getJavaType(){
        return this.dtEnum.getJavaType();
    }

    /**
     * 当精度（>=1）小于标度
     * 重置标度，让其小于精度(精度-1)
     */
    public void formatNumLength(Integer numPrecision, Integer numScale){
        if(numScale != null && numPrecision < numScale){
            this.numScale = numPrecision - 1;
        }
    }

    public String formatDataType(){
        String lengthInfo = getFormatLengthStr();
        return getDataType() + (StringUtil.isNotEmpty(lengthInfo) ? "(" + lengthInfo + ")" : "");
    }

}
