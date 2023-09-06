package org.openea.eap.extj.database.datatype.db.interfaces;

import org.openea.eap.extj.database.datatype.limit.base.DtLimitModel;
import org.openea.eap.extj.database.datatype.viewshow.ViewDataTypeEnum;
import org.openea.eap.extj.database.source.DbBase;
import org.openea.eap.extj.util.StringUtil;

/**
 * 数据库数据类型接口
 *
 * 
 */
public interface DtInterface {

    /**
     * 获取数据库自身数据类型
     * @return ignore
     */
    String getDataType();

    /**
     * 获取枚举名
     * @return ignore
     */
    String name();

    /**
     * 获取长度规则模型
     * @return ignore
     */
    DtLimitBase getDtLimit();

    /**
     * 字符长度
     */
    default DtLimitModel getCharLengthLm(){
        return getDtLimit().getCharLengthLm();
    }

    /**
     * 字节长度
     */
    default DtLimitModel getBitLengthLm(){
        return getDtLimit().getBitLengthLm();
    }

    /**
     * 精度
     */
    default DtLimitModel getNumPrecisionLm(){
        return getDtLimit().getNumPrecisionLm();
    }

    /**
     * 标度
     */
    default DtLimitModel getNumScaleLm(){
        return getDtLimit().getNumScaleLm();
    }

    /**
     * 数据类型
     */
    default String getDtCategory(){
        return getDtLimit().getDtCategory();
    }

    /**
     * 是否可修改
     */
    default Boolean getIsModifyFlag(){
        return getDtLimit().getIsModifyFlag();
    }

    /**
     * java类型
     */
    default String getJavaType(){
        return getDtLimit().getJavaType();
    }

    /**
     * 数据库类型
     * @return dbType
     */
    default String getDbType(){
        return this.getClass().getSimpleName().replace("Dt","").replace("Enum", "");
    }


    /**
     * 根据数据库类型编码获取枚举类
     */
    static <T extends DtInterface> Class<T> getClz(String dbType) throws Exception {
        // 数据类型枚举类命名规则：Dt + jnpfDbEncode
        return (Class<T>)Class.forName("jnpf.database.datatype.db.Dt" + dbType + "Enum");
    }




    /**
     * 根据前端数据类型，返回对应枚举
     * @param viewDataType 前端数据类型名称
     * @param dbEncode 数据类型枚数据库编码
     * @return 数据类型枚举
     */
    static DtInterface newInstanceByView(String viewDataType, String dbEncode) throws Exception {
            if (StringUtil.isNotNull(viewDataType)) {
                for (ViewDataTypeEnum value : ViewDataTypeEnum.values()) {
                    if (value.getViewFieldType().equalsIgnoreCase(viewDataType)) {
                        switch (dbEncode){
                            case DbBase.MYSQL:
                                return value.getDtMySQLEnum();
                            case DbBase.ORACLE:
                                return value.getDtOracleEnum();
                            case DbBase.SQL_SERVER:
                                return value.getDtSQLServerEnum();
                            case DbBase.DM:
                                return value.getDtDMEnum();
                            case DbBase.KINGBASE_ES:
                                return value.getDtKingbaseESEnum();
                            case DbBase.POSTGRE_SQL:
                                return value.getDtPostgreSQLEnum();
                            case DbBase.DORIS:
                                return value.getDtDorisEnum();
                            default:
                        }
                    }
                }
            }
        return null;
    }

    /**
     * 根据数据类型，返回对应枚举
     * @param dtDataType 数据库数据类型名称
     * @param dbEncode 数据类型枚数据库编码
     * @return 数据类型枚举
     */
    static DtInterface newInstanceByDt(String dtDataType, String dbEncode) throws Exception {
        // 当类型无法在预设中找到时,在枚举中寻找
        for (DtInterface enumConstant : getClz(dbEncode).getEnumConstants()) {
            if(enumConstant.getDataType().equalsIgnoreCase(dtDataType)){
                return enumConstant;
            }
        }
        return null;
    }


}
