package org.openea.eap.extj.base.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.openea.eap.extj.base.entity.SuperEntity;

/**
 * 在线开发表单外链实体
 *
 */
@Data
@TableName("base_visualdev_short_link")
public class VisualdevShortLinkEntity extends SuperEntity<String> {

    /**
     * 短链接
     */
    @TableField("F_SHORTLINK")
    private String shortLink;

    /**
     * 外链填单开关
     */
    @TableField("F_FORMUSE")
    private Integer formUse;

    /**
     * 外链填单
     */
    @TableField("F_FORMLINK")
    private String formLink;

    /**
     * 外链密码开关
     */
    @TableField("F_FORMPASSUSE")
    private Integer formPassUse;

    /**
     * 外链填单密码
     */
    @TableField("F_FORMPASSWORD")
    private String formPassword;

    /**
     * 公开查询开关
     */
    @TableField("F_COLUMNUSE")
    private Integer columnUse;

    /**
     * 公开查询
     */
    @TableField("F_COLUMNLINK")
    private String columnLink;

    /**
     * 查询密码开关
     */
    @TableField("F_COLUMNPASSUSE")
    private Integer columnPassUse;

    /**
     * 公开查询密码
     */
    @TableField("F_COLUMNPASSWORD")
    private String columnPassword;

    /**
     * 查询条件
     */
    @TableField("F_COLUMNCONDITION")
    private String columnCondition;

    /**
     * 显示内容
     */
    @TableField("F_COLUMNTEXT")
    private String columnText;

    /**
     * PC端链接
     */
    @TableField("F_REALPCLINK")
    private String realPcLink;

    /**
     * App端链接
     */
    @TableField("F_REALAPPLINK")
    private String realAppLink;

    /**
     * 用户id
     */
    @TableField("F_USERID")
    private String userId;

    /**
     * 状态
     */
    @TableField("F_ENABLEDMARK")
    private Integer enabledMark;

}
