package org.openea.eap.extj.database.model.dbfield;

import lombok.Data;
import org.openea.eap.extj.database.constant.DbAliasConst;
import org.openea.eap.extj.database.datatype.model.DtModel;
import org.openea.eap.extj.database.datatype.model.DtModelDTO;
import org.openea.eap.extj.database.enums.DbAliasEnum;
import org.openea.eap.extj.database.model.dto.ModelDTO;
import org.openea.eap.extj.database.model.interfaces.JdbcGetMod;
import org.openea.eap.extj.database.util.DbTypeUtil;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.util.StringUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
public class DbFieldModel extends DbFieldModelBase implements JdbcGetMod {

    private DtModelDTO dtModelDTO;

    public String getFieldName() {
        return super.getComment();
    }

    public String formatDataTypeByView(String dbEncode) throws Exception {
        DtModel dataTypeModel;
        try {
            if (this.dtModelDTO == null) {
                this.dtModelDTO = new DtModelDTO(this.getDataType(), this.getLength(), dbEncode, true);
            }

            dataTypeModel = this.dtModelDTO.convert();
        } catch (DataException var4) {
            var4.printStackTrace();
            return this.getDataType();
        } catch (Exception var5) {
            var5.printStackTrace();
            this.dtModelDTO.setConvertType("FIX_VAL");
            dataTypeModel = this.dtModelDTO.convert();
        }

        return dataTypeModel.formatDataType();
    }

    public void setMod(ModelDTO modelDTO) throws SQLException {
        ResultSet result = modelDTO.getResultSet();
        String dbEncode = modelDTO.getDbEncode();
        this.dataType = result.getString(DbAliasEnum.DATA_TYPE.getAlias(dbEncode));

        try {
            DbTypeUtil.getEncodeDb(dbEncode).setPartFieldModel(this, result);
            this.dtModelDTO.setConvertType("DB_VAL").setConvertTargetDtEnum(this.dtModelDTO.getDtEnum());
            this.setLength(this.dtModelDTO.convert().getFormatLengthStr());
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        if (this.field == null) {
            this.field = result.getString(DbAliasEnum.FIELD.getAlias(dbEncode));
        }

        if (this.comment == null) {
            this.comment = result.getString(DbAliasEnum.FIELD_COMMENT.getAlias(dbEncode));
        }

        if (this.isPrimaryKey == null) {
            this.isPrimaryKey = (Boolean) DbAliasConst.PRIMARY_KEY.getSign(result.getInt(DbAliasEnum.PRIMARY_KEY.getAlias(dbEncode)));
        }

        if (this.nullSign == null) {
            this.nullSign = (String)DbAliasConst.ALLOW_NULL.getSign(result.getInt(DbAliasEnum.ALLOW_NULL.getAlias(dbEncode)));
        }

        try {
            if (this.isAutoIncrement == null && "MySQL".equals(dbEncode)) {
                this.isAutoIncrement = (Boolean)DbAliasConst.AUTO_INCREMENT.getSign(result.getInt(DbAliasEnum.AUTO_INCREMENT.getAlias(dbEncode)));
            }
        } catch (Exception var6) {
        }

        if (this.isAutoIncrement == null && "DM".equals(dbEncode)) {
            this.isAutoIncrement = result.getInt(DbAliasEnum.AUTO_INCREMENT.getAlias(dbEncode)) == 1;
        }

        if ("PostgreSQL".equals(dbEncode) || "KingbaseES".equals(dbEncode)) {
            String columnDefault = result.getString(DbAliasEnum.COLUMN_DEFAULT.getAlias(dbEncode));
            String tableName = result.getString(DbAliasEnum.TABLE_NAME.getAlias(dbEncode));
            if (StringUtil.isNotEmpty(columnDefault) && StringUtil.isNotEmpty(tableName) && this.field != null) {
                this.isAutoIncrement = ("nextval('" + tableName + '_' + this.field + "_seq'::regclass)").equals(columnDefault);
            }
        }

        if ("Oracle".equals(dbEncode) && this.isPrimaryKey) {
            this.isAutoIncrement = result.getInt(DbAliasEnum.AUTO_TRIGGER.getAlias(dbEncode)) > 0;
        }

        if (this.isAutoIncrement == null && "SQLServer".equals(dbEncode)) {
            this.isAutoIncrement = result.getInt(DbAliasEnum.IS_IDENTITY.getAlias(dbEncode)) == 1;
        }

    }
}
