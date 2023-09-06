package org.openea.eap.extj.database.datatype.limit.util;

import org.openea.eap.extj.database.datatype.db.interfaces.DtInterface;
import org.openea.eap.extj.database.datatype.limit.base.DtLimitModel;
import org.openea.eap.extj.database.datatype.limit.model.DtModel;
import org.openea.eap.extj.database.datatype.limit.model.DtModelDTO;

/**
 * 类功能
 *
 * 
 */
public class DtLimitUtil {


    /* ============== 转换方式 =============== */
    /*
     * 说明：
     * 1、不可修改转可修改（按默认值）
     * 2、可修改转可修改
     * 3、创建可修改
     *
     * （以下都不进行长度设置）
     * 4、可修改转不可修改
     * 5、不可修改转不可修改
     * 6、创建不可修改
     * */

    /**
     * 数值转换
     * @param dto 前端数据类型模型
     * @return 转换数据类型模板
     */
    public static DtModel convertNumeric(DtModelDTO dto){
        DtInterface toDtEnum = dto.getConvertTargetDtEnum();
        DtModel toModel = new DtModel(toDtEnum);
        DtLimitModel numPrecisionLm = toDtEnum.getNumPrecisionLm();
        DtLimitModel numScaleLm = toDtEnum.getNumScaleLm();
        // 精度、标度：标准化 =========
        Integer numPrecision = dto.getNumPrecision();
        Integer numScale = dto.getNumScale();
        if(toDtEnum.getIsModifyFlag()){
            switch (dto.getConvertType()){
                // 1：数据库取到的值
                case DtModelDTO.DB_VAL:
                    break;
                // 2：使用固定值
                case DtModelDTO.FIX_VAL:
                    numPrecision = (Integer) numPrecisionLm.getFixed();
                    numScale = (Integer) numScaleLm.getFixed();
                    break;
                // 3：前端传来的参数
                case DtModelDTO.VIEW_VAL:
                    // 验证比较
                    numPrecision = convertNum(numPrecision, (Integer) numPrecisionLm.getMin(), (Integer) numPrecisionLm.getMax(), (Integer)numPrecisionLm.getDefaults());
                    numScale = convertNum(numScale, (Integer) numScaleLm.getMin(), (Integer) numScaleLm.getMax(), (Integer)numScaleLm.getDefaults());
                    break;
                default:
            }
            // 数据型设置 =========
            toModel.setNumPrecision(numPrecision);
            toModel.setNumScale(numScale);
            toModel.formatNumLength(numPrecision, numScale);
        }
        return toModel;
    }

    /**
     * 字符转换
     * @return 转换数据类型模板
     */
    public static Long convertCharacter(Long inputVarLength, String convertType, DtLimitModel varLengthLm){
        // 精度、标度：标准化 =========
        Long outVarLength = null;
        switch (convertType){
            // 1：数据库取到的值，且同库(不用转换直接使用)
            case DtModelDTO.DB_VAL:
                outVarLength = inputVarLength;
                break;
            // 2：使用固定值
            case DtModelDTO.FIX_VAL:
                outVarLength = (Long)varLengthLm.getFixed();
                break;
            // 3：主要转换：前端传来的参数
            case DtModelDTO.VIEW_VAL:
                // 验证比较
                outVarLength = convertLongNum(inputVarLength, (Long) varLengthLm.getMax(), (Long) varLengthLm.getDefaults());
                break;
            default:
        }
        return outVarLength;
    }

    /* ================ */

    /**
     * 获取数值类型显示
     */
    public static void getNumericLength(DtModel model){
        model.setFormatLengthStr(model.getNumPrecision() + "," + model.getNumScale());
    }

    /* =========== 内部使用算法 =========== */

    /**
     * originNum < [Min, Max] < originNum
     */
    private static Integer convertNum(Integer originNum, Integer toMin, Integer toMax, Integer defaultNum){
        originNum = originNum == null || originNum < toMin ? defaultNum : originNum;
        // 区间内
        if(originNum >= toMin && toMax >= originNum){
            return originNum;
        }else if(originNum > toMax){
            // 大于区间，为保证尽可能数据不丢失，返回最大长度
            return toMax;
        }else {
            return toMin;
        }
    }

    /**
     * charLength 的 Long类型
     * >= 0
     */
    private static Long convertLongNum(Long originNum, Long toMax, Long defaultNum){
        originNum = originNum == null || originNum < 1 ? defaultNum : originNum;
        if(originNum <= toMax){
            return originNum;
        }else{
            return toMax;
        }
    }

    /*
         说明：
         UTF-8：一个汉字 = 3个字节，英文一个字母占用一个字节
         GBK： 一个汉字 = 2个字节，英文一个字母占用一个字节
         MySQL的char_length计算的是字符长度，而Oracle的bit_length计算的是字节长度

         1、GBK是在国家标准GB2312基础上扩容后兼容GB2312的标准。GBK编码专门用来解决中文编码的，是双字节的。不论中英文都是双字节的。
         2、UTF8编码是用以解决国际上字符的一种多字节编码，它对英文使用8位（即一个字节），中文使用24位（三个字节）来编码。
          对于英文字符较多的论坛则用UTF－8节省空间。另外，如果是外国人访问你的GBK网页，需要下载中文语言包支持。
          访问UTF-8编码的网页则不出现这问题。可以直接访问。
         3、GBK包含全部中文字符；UTF8则包含全世界所有国家需要用到的字符。
     */


}
