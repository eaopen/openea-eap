package org.openea.eap.extj.database.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.openea.eap.extj.database.model.dto.PrepSqlDTO;
import org.openea.eap.extj.database.util.DataSourceUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 数据连接
 *
 * 
 */
@Data
@TableName("base_dblink")
@NoArgsConstructor
public class DbLinkEntity extends DataSourceUtil {
    /**
     * 连接主键
     */
    @TableId("F_ID")
    private String id;

    /**
     * 连接名称
     */
    @TableField("F_FULLNAME")
    private String fullName;

    /**
     * 描述
     */
    @TableField("F_DESCRIPTION")
    private String description;

    /**
     * 排序码
     */
    @TableField("F_SORTCODE")
    private Long sortCode;

    /**
     * 有效标志
     */
    @TableField("F_ENABLEDMARK")
    private Integer enabledMark;

    /**
     * 创建时间
     */
    @TableField(value = "F_CREATORTIME",fill = FieldFill.INSERT)
    private Date creatorTime;

    /**
     * 创建用户
     */
    @TableField(value = "F_CREATORUSERID",fill = FieldFill.INSERT)
    private String creatorUserId;

    /**
     * 修改时间
     */
    @TableField(value = "F_LASTMODIFYTIME",fill = FieldFill.UPDATE)
    private Date lastModifyTime;

    /**
     * 修改用户
     */
    @TableField(value = "F_LASTMODIFYUSERID",fill = FieldFill.UPDATE)
    private String lastModifyUserId;

    /**
     * 删除标志
     */
    @TableField("F_DELETEMARK")
    private Integer deleteMark;

    /**
     * 删除时间
     */
    @TableField("F_DELETETIME")
    private Date deleteTime;

    /**
     * 删除用户
     */
    @TableField("F_DELETEUSERID")
    private String deleteUserId;

    /**
     * Oracle扩展开关
     */
    @TableField("F_ORACLE_EXTEND")
    private Boolean oracleExtend;

    public static DbLinkEntity newInstance(String dbLinkId){
        return PrepSqlDTO.DB_LINK_FUN.apply(dbLinkId);
    }

    public DbLinkEntity(String dbType){
        super.setDbType(dbType);
    }

}
