package org.openea.eap.extj.base.model.dbtable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openea.eap.extj.database.constant.DbAliasConst;
import org.openea.eap.extj.database.datatype.viewshow.constant.DtViewConst;
import org.openea.eap.extj.database.model.dbfield.DbFieldModel;
import org.openea.eap.extj.util.StringUtil;

@Data
@NoArgsConstructor
public class DbFieldVO {
    @Schema(description = "数据库字段名")
    private String columnName;

    @Schema(description = "字段名")
    private String field;
    @Schema(description = "字段说明")
    private String fieldName;
    @Schema(description = "数据类型")
    private String dataType;
    @Schema(description = "数据长度")
    private String dataLength;
    @Schema(description = "主键")
    private Integer primaryKey;
    @Schema(description = "允空")
    private Integer allowNull;
    @Schema(description = "自增标识 1:是 0:否")
    private Integer autoIncrement;

    public DbFieldVO(DbFieldModel dbFieldModel){
        this.field = dbFieldModel.getField();
        this.fieldName = dbFieldModel.getComment();
        this.dataType = dbFieldModel.getDataType();
        this.dataLength = StringUtil.isNotEmpty(dbFieldModel.getLength()) ? dbFieldModel.getLength() : DtViewConst.DEFAULT;;
        this.primaryKey = DbAliasConst.PRIMARY_KEY.getNum(dbFieldModel.getIsPrimaryKey());
        this.allowNull = DbAliasConst.ALLOW_NULL.getNum(dbFieldModel.getNullSign());
        this.autoIncrement = DbAliasConst.AUTO_INCREMENT.getNum(dbFieldModel.getIsAutoIncrement());
    }

}
