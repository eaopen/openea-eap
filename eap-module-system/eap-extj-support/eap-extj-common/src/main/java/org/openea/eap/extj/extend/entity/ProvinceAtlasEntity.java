package org.openea.eap.extj.extend.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import org.openea.eap.extj.base.entity.SuperExtendEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 行政区划-地图
 *
 */
@Data
@TableName("base_province_atlas")
public class ProvinceAtlasEntity extends SuperExtendEntity.SuperExtendSortEntity<String> implements Serializable {

    /**
     * 区域上级
     */
    @TableField("F_PARENTID")
    private String parentId;

    /**
     * 区域编码
     */
    @TableField("F_ENCODE")
    private String enCode;

    /**
     * 区域名称
     */
    @TableField("F_FULLNAME")
    private String fullName;

    /**
     * 快速查询
     */
    @TableField("F_QUICKQUERY")
    private String quickQuery;

    /**
     * 区域类型
     */
    @TableField("F_TYPE")
    private String type;

    /**
     * 修改时间
     */
    @TableField(value = "F_LASTMODIFYTIME",fill = FieldFill.UPDATE)
    @JSONField(name = "F_LastModifyTime")
    private Date lastModifyTime;

    /**
     * 租户id
     */
    @TableField("F_ATLASCENTER")
    private String atlasCenter;
}
