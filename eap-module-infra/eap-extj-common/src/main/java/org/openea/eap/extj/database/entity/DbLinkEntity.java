package org.openea.eap.extj.database.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.openea.eap.extj.database.util.DataSourceUtil;

import java.util.Date;

@Data
@TableName("base_dblink")
public class DbLinkEntity extends DataSourceUtil {
    @TableId("F_ID")
    private String id;
    @TableField("F_FULLNAME")
    private String fullName;
    @TableField("F_DESCRIPTION")
    private String description;
    @TableField("F_SORTCODE")
    private Long sortCode;
    @TableField("F_ENABLEDMARK")
    private Integer enabledMark;
    @TableField(
            value = "F_CREATORTIME",
            fill = FieldFill.INSERT
    )
    private Date creatorTime;
    @TableField(
            value = "F_CREATORUSERID",
            fill = FieldFill.INSERT
    )
    private String creatorUserId;
    @TableField(
            value = "F_LASTMODIFYTIME",
            fill = FieldFill.UPDATE
    )
    private Date lastModifyTime;
    @TableField(
            value = "F_LASTMODIFYUSERID",
            fill = FieldFill.UPDATE
    )
    private String lastModifyUserId;
    @TableField("F_DELETEMARK")
    private Integer deleteMark;
    @TableField("F_DELETETIME")
    private Date deleteTime;
    @TableField("F_DELETEUSERID")
    private String deleteUserId;
    @TableField("F_ORACLE_EXTEND")
    private Boolean oracleExtend;

    public static DbLinkEntity newInstance(String dbLinkId) {
        // TODO
        //return (DbLinkEntity)PrepSqlDTO.DB_LINK_FUN.apply(dbLinkId);
        return null;
    }
    public DbLinkEntity(){}

    public DbLinkEntity(String dbType) {
        super.setDbType(dbType);
    }

}
