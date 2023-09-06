package org.openea.eap.extj.database.model.dbtable;


import org.openea.eap.extj.database.model.dbfield.DbFieldModel;
import org.openea.eap.extj.database.model.dbtable.base.DbTableModelBase;
import org.openea.eap.extj.database.model.dto.ModelDTO;
import org.openea.eap.extj.database.model.interfaces.JdbcGetMod;
import io.swagger.annotations.ApiModelProperty;
import org.openea.eap.extj.database.enums.DbAliasEnum;
import org.openea.eap.extj.database.source.DbBase;
import org.openea.eap.extj.util.StringUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.ResultSet;
import java.util.List;

/**
 *
 *
 */
@Data
@ToString(callSuper=true)
@NoArgsConstructor
public class DbTableFieldModel extends DbTableModelBase implements JdbcGetMod {

    /**
    * 标识
    */
    @ApiModelProperty("标识")
    private String id;

    /**
     * 数据源主键
     */
    @ApiModelProperty("数据源主键")
    private String dbLinkId;

    /**
     * 数据库编码
     */
    @ApiModelProperty("数据库编码")
    private String dbEncode;

    /**
     * 更新时新表名
     */
    @ApiModelProperty("更新时新表名")
    private String updateNewTable;

    /**
     * 更新前的旧表名
     */
    @ApiModelProperty("更新前的旧表名")
    private String updateOldTable;

    /**
     * 字段信息集合
     */
    @ApiModelProperty("字段信息集合")
    private List<DbFieldModel> dbFieldModelList;

    /**
     * 表是否存在信息
     */
    @ApiModelProperty("表是否存在信息")
    private Boolean hasTableData;

    /**
     * 类型 0-表 1-视图
     */
    @ApiModelProperty("类型 0-表 1-视图")
    private Integer type;

    /**
     * 前端注释
     */
    public String getTableName(){
        return getComment();
    }

    public void setTableName(String comment) {
        super.setComment(comment);
    }

    public DbTableFieldModel(String table, String tableComment, List<DbFieldModel> dbFieldModelList){
        this.setTable(table);
        this.setComment(tableComment);
        this.dbFieldModelList = dbFieldModelList;
    }

    @Override
    public void setMod(ModelDTO modelDTO) {
        try {
            String dbEncode = modelDTO.getDbEncode();
            ResultSet resultSet = modelDTO.getResultSet();
            // ============== 表名 ==============
            try {
                String table = resultSet.getString(DbAliasEnum.TABLE_NAME.getAlias(dbEncode));
                this.setTable(table);
            } catch (Exception e) {

            }
            // ============== 表注释 ==============
            try {
                String tableComment = resultSet.getString(DbAliasEnum.TABLE_COMMENT.getAlias(dbEncode));
                this.setComment(tableComment);
            } catch (Exception e) {

            }

            // ============== 表总数 ==============
            this.setSum("0");
            try {
                String sum = resultSet.getString(DbAliasEnum.TABLE_SUM.getAlias(dbEncode));
                if(sum != null)
                    this.setSum(sum);
            } catch (Exception e) {

            }
            this.setType(0);
            try {
                String tableType = resultSet.getString(DbAliasEnum.TABLE_TYPE.getAlias(dbEncode));
                if (StringUtil.isNotEmpty(tableType)) {
                    if (dbEncode.equals(DbBase.SQL_SERVER) && tableType.equalsIgnoreCase("V ")) {
                        this.setType(1);
                    } else if ((dbEncode.equals(DbBase.ORACLE) || dbEncode.equals(DbBase.MYSQL)
                            || dbEncode.equals(DbBase.DM) || dbEncode.equals(DbBase.POSTGRE_SQL) || dbEncode.equals(DbBase.KINGBASE_ES)
                    )
                            && tableType.equalsIgnoreCase("VIEW")) {
                        this.setType(1);
                    }
                }
            } catch (Exception e) {

            }
            // ============== 表大小（由于部分数据库，版本取消了此功能）==============
            /*String size = resultSet.getString(DbAliasEnum.TABLE_SIZE.AS());*/
            this.setDbEncode(dbEncode);
//            this.setSize(size);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
