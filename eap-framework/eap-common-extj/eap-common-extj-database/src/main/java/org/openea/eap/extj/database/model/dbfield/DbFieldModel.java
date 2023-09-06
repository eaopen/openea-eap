package org.openea.eap.extj.database.model.dbfield;

import org.openea.eap.extj.database.constant.DbAliasConst;
import org.openea.eap.extj.database.datatype.limit.model.DtModel;
import org.openea.eap.extj.database.datatype.limit.model.DtModelDTO;
import org.openea.eap.extj.database.model.dto.ModelDTO;
import org.openea.eap.extj.database.enums.DbAliasEnum;
import org.openea.eap.extj.database.model.dbfield.base.DbFieldModelBase;
import org.openea.eap.extj.database.model.interfaces.JdbcGetMod;
import org.openea.eap.extj.database.source.DbBase;
import org.openea.eap.extj.database.util.DbTypeUtil;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.util.StringUtil;
import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 表字段模型
 *
 *
 */
@Data
public class DbFieldModel extends DbFieldModelBase implements JdbcGetMod {

    /**
     * 数据类型传递对象
     */
    private DtModelDTO dtModelDTO;

    /**
     * 早期注释叫法
     */
    public String getFieldName(){
        return super.getComment();
    }

    /**
     * 数据类型格式化
     * -- 创表
     */
    public String formatDataTypeByView(String dbEncode) throws Exception {
        DtModel dataTypeModel;
        try{
            // 1、前端指定数据类型转换
            if(dtModelDTO == null) dtModelDTO = new DtModelDTO(getDataType(), getLength(), dbEncode, true);
            // 2、标准数据类型对象转换
            dataTypeModel = dtModelDTO.convert();
        }catch (DataException e){
            e.printStackTrace();
            return getDataType();
        }catch (Exception e){
            e.printStackTrace();
            // 准备异常时，使用默认可执行数据类型
            dtModelDTO.setConvertType(DtModelDTO.FIX_VAL);
            dataTypeModel = dtModelDTO.convert();
        }
        return dataTypeModel.formatDataType();
    }

    @Override
    public void setMod(ModelDTO modelDTO) throws SQLException {
        ResultSet result = modelDTO.getResultSet();
        String dbEncode = modelDTO.getDbEncode();
        // 根据不同的库进行字段处理
        // ============== 字段数据类型 ===============
        this.dataType = result.getString(DbAliasEnum.DATA_TYPE.getAlias(dbEncode));
        // ============== 字段类型、长度 ===============
        try{
            DbTypeUtil.getEncodeDb(dbEncode).setPartFieldModel(this, result);
            this.dtModelDTO.setConvertType(DtModelDTO.DB_VAL).setConvertTargetDtEnum(this.dtModelDTO.getDtEnum());
            this.setLength(this.dtModelDTO.convert().getFormatLengthStr());
        }catch (Exception e){
            e.printStackTrace();
        }
        // ============== 字段名 ===============
        if(this.field == null) this.field = result.getString(DbAliasEnum.FIELD.getAlias(dbEncode));
        // ============== 字段注释 ===============
        if(this.comment == null) this.comment = result.getString(DbAliasEnum.FIELD_COMMENT.getAlias(dbEncode));
        // ============== 字段主键（0:非主键、1：主键） ==============
        if(this.isPrimaryKey == null) this.isPrimaryKey = DbAliasConst.PRIMARY_KEY.getSign(result.getInt(DbAliasEnum.PRIMARY_KEY.getAlias(dbEncode)));
        // ============== 字段允空（0:空值 NULL、1:非空值 NOT NULL） ==============
        if(this.nullSign == null) this.nullSign = DbAliasConst.ALLOW_NULL.getSign(result.getInt(DbAliasEnum.ALLOW_NULL.getAlias(dbEncode)));
        // ============== 自增(0:非自增 1:自增) ==============
        if(this.isAutoIncrement == null && DbBase.MYSQL.equals(dbEncode)) this.isAutoIncrement = result.getInt(DbAliasEnum.AUTO_INCREMENT.getAlias(dbEncode)) == 1;

        if(this.isAutoIncrement == null && DbBase.DM.equals(dbEncode)) this.isAutoIncrement = result.getInt(DbAliasEnum.AUTO_INCREMENT.getAlias(dbEncode)) == 1;

        if(DbBase.POSTGRE_SQL.equals(dbEncode) || DbBase.KINGBASE_ES.equals(dbEncode)){
            String columnDefault = result.getString(DbAliasEnum.COLUMN_DEFAULT.getAlias(dbEncode));
            String tableName = result.getString(DbAliasEnum.TABLE_NAME.getAlias(dbEncode));
            if(StringUtil.isNotEmpty(columnDefault) && StringUtil.isNotEmpty(tableName) && this.field != null) {
                this.isAutoIncrement = ("nextval(\'" + tableName + '_' + this.field + "_seq" + "\'::regclass)").equals(columnDefault);
            }
        }

        if(DbBase.ORACLE.equals(dbEncode)){
            if(this.isPrimaryKey){
                this.isAutoIncrement = result.getInt(DbAliasEnum.AUTO_TRIGGER.getAlias(dbEncode)) > 0;
            }
        }

        if(this.isAutoIncrement == null && DbBase.SQL_SERVER.equals(dbEncode)) this.isAutoIncrement = result.getInt(DbAliasEnum.IS_IDENTITY.getAlias(dbEncode)) == 1;
    }

}
