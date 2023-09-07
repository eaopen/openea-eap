package org.openea.eap.extj.base.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.openea.eap.extj.base.entity.SuperExtendEntity;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("base_interfaceoauth")
public class InterfaceOauthEntity extends SuperExtendEntity.SuperExtendSortEntity<String> implements Serializable {

    /**
     * 应用id appId
     */
    @TableField("F_AppId")
    private String appId;

    /**
     * 应用名称
     */
    @TableField("F_AppName")
    private  String appName;

    /**
     * 应用秘钥
     */
    @TableField("F_AppSecret")
    private  String appSecret;

    /**
     * 验证签名
     */
    @TableField("F_VerifySignature")
    private Integer verifySignature;

    /**
     * 使用期限
     */
    @TableField(value="F_UsefulLife",updateStrategy = FieldStrategy.IGNORED)
    private Date usefulLife;

    /**
     * 白名单
     */
    @TableField("F_WhiteList")
    private String whiteList;

    /**
     * 黑名单
     */
    @TableField("F_BlackList")
    private String blackList;

    /**
     * 接口id
     */
    @TableField("F_DataInterfaceIds")
    private String dataInterfaceIds;

}