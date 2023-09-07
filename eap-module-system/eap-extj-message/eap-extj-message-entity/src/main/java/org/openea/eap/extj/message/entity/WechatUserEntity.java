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
@TableName("base_message_wechat_user")
public class WechatUserEntity extends SuperEntity<String> {

    /** 公众号元素id **/
    @TableField("F_GZHID")
    private String gzhId;

    /** 用户id **/
    @TableField("F_USERID")
    private String userId;

    /** 公众号用户id **/
    @TableField("F_OPENID")
    private String openId;

    /** 是否关注公众号 **/
    @TableField("F_CLOSEMARK")
    private Integer closeMark;

    /**
     * 状态
     */
    @TableField("F_ENABLEDMARK")
    private Integer enabledMark;
}
