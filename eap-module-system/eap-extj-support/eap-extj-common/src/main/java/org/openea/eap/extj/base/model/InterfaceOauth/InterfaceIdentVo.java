package org.openea.eap.extj.base.model.InterfaceOauth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.openea.eap.extj.base.model.dataInterface.DataInterfaceVo;

import java.util.Date;
import java.util.List;

@Data
public class InterfaceIdentVo {

    @Schema(description = "id")
    private String id;

    @Schema(description = "应用id")
    private String appId;

    @Schema(description = "应用名称")
    private String appName;

    @Schema(description = "应用秘钥")
    private String appSecret;

    @Schema(description = "验证签名")
    private Integer verifySignature;

    @Schema(description = "使用期限")
    private Date usefulLife;

    @Schema(description = "白名单")
    private String whiteList;

    @Schema(description = "黑名单")
    private String blackList;

    @Schema(description = "排序")
    private Long sortCode;

    @Schema(description = "状态")
    private Integer enabledMark;

    @Schema(description = "说明")
    private String description;

    @Schema(description = "创建人id")
    private String creatorUserId;

    @Schema(description = "创建人")
    private String creatorUser;

    @Schema(description = "创建时间")
    private Long creatorTime;

    @Schema(description = "修改人id")
    private String lastModifyUserId;

    @Schema(description = "修改人")
    private String lastModifyUser;

    @Schema(description = "修改时间")
    private Long lastModifyTime;

    @Schema(description = "租户id")
    private String tenantId;



    /**
     * 接口列表
     */
    @Schema(description = "接口列表字符串")
    private String dataInterfaceIds;
    /**
     * 接口列表
     */
    @Schema(description = "接口列表")
    private List<DataInterfaceVo> list;
}
