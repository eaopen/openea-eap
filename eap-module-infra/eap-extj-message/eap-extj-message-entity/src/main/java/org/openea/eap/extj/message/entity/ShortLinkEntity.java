package org.openea.eap.extj.message.entity;

import org.openea.eap.extj.base.entity.SuperEntity;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 *
 * 短信变量表
 */
@Data
@TableName("base_message_short_link")
public class ShortLinkEntity extends SuperEntity<String> {

    /** 短链接 **/
    @TableField("F_SHORTLINK")
    private String shortLink;

    /** PC端链接 **/
    @TableField("F_REALPCLINK")
    private String realPcLink;

    /** App端链接 **/
    @TableField("F_REALAPPLINK")
    private String realAppLink;

    /** 流程内容 **/
    @TableField("F_BODYTEXT")
    private String bodyText;

    /** 是否点击后失效 **/
    @TableField("F_ISUSED")
    private Integer isUsed;

    /** 点击次数 **/
    @TableField("F_CLICKNUM")
    private Integer clickNum;

    /** 失效次数 **/
    @TableField("F_UNABLENUM")
    private Integer unableNum;

    /** 失效时间 **/
    @TableField("F_UNABLETIME")
    private Date unableTime;

    /** 用户id **/
    @TableField("F_USERID")
    private String userId;

    /**
     * 状态
     */
    @TableField("F_ENABLEDMARK")
    private Integer enabledMark;
}
