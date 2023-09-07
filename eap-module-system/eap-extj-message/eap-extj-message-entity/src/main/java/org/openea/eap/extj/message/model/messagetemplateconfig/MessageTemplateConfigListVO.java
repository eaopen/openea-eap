package org.openea.eap.extj.message.model.messagetemplateconfig;


import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.openea.eap.extj.message.entity.SmsFieldEntity;
import org.openea.eap.extj.message.entity.TemplateParamEntity;

import java.util.List;


@Data
public class MessageTemplateConfigListVO {
    private String id;


    /**
     * 子表数据
     **/
    @Schema(description = "子表数据")
    @JsonProperty("tableField112")
    @JSONField(name = "tableField112")
    private List<TemplateParamEntity> templateParamList;

    /**
     * 子表数据
     **/
    @Schema(description = "子表数据")
    @JsonProperty("tableField116")
    @JSONField(name = "tableField116")
    private List<SmsFieldEntity> smsFieldList;

    /**
     * 名称
     **/
    @Schema(description = "名称")
    @JSONField(name = "fullName")
    private String fullName;

    /**
     * 编码
     **/
    @Schema(description = "编码")
    @JSONField(name = "enCode")
    private String enCode;

    /**
     * 模板类型
     **/
    @Schema(description = "模板类型")
    @JSONField(name = "templateType")
    private String templateType;

    /**
     * 消息来源
     **/
    @Schema(description = "消息来源")
    @JSONField(name = "messageSource")
    private String messageSource;

    /**
     * 消息类型
     **/
    @Schema(description = "消息类型")
    @JSONField(name = "messageType")
    private String messageType;

    /**
     * 排序
     **/
    @Schema(description = "排序")
    @JSONField(name = "sortCode")
    private Integer sortCode;
    /**
     * 状态
     **/
    @Schema(description = "状态")
    @JSONField(name = "enabledMark")
    private Integer enabledMark;

//    /**
//     * 说明
//     **/
//    @JSONField(name = "remark")
//    private String remark;

//    /**
//     * 消息标题
//     **/
//    @JSONField(name = "title")
//    private String title;
//
//    /**
//     * 消息内容
//     **/
//    @JSONField(name = "content")
//    private String content;
//
//    /**
//     * 模板编号
//     **/
//    @JSONField(name = "templateCode")
//    private String templateCode;

    /**
     * 创建时间
     **/
    @Schema(description = "创建时间")
    @JSONField(name = "creatorTime")
    private Long creatorTime;

    /**
     * 创建人员
     **/
    @Schema(description = "创建人员")
    @JSONField(name = "creatorUserId")
    private String creatorUserId;

    /**
     * 修改时间
     **/
    @Schema(description = "修改时间")
    @JSONField(name = "lastModifyTime")
    private Long lastModifyTime;

//    /**
//     * 修改人员
//     **/
//    @JSONField(name = "lastModifyUserId")
//    private String lastModifyUserId;

    @Schema(description = "创建人")
    private String creatorUser;


}