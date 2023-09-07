package org.openea.eap.extj.message.model.messagedatatype;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class MessageDataTypeForm {
    /**
     * 主键
     */
    @Schema(description = "主键")
    private String id;
    /**
     * 数据类型
     **/
    @Schema(description = "数据类型")
    @JsonProperty("type")
    private String type;

    /**
     * 数据名称
     **/
    @Schema(description = "数据名称")
    @JsonProperty("fullName")
    private String fullName;

    /**
     * 数据编码（为防止与系统后续更新的功能的数据编码冲突，客户自定义添加的功能编码请以ZDY开头。例如：ZDY1）
     **/
    @Schema(description = "数据编码")
    @JsonProperty("enCode")
    private String enCode;

    /**
     * 创建时间
     **/
    @Schema(description = "创建时间")
    @JsonProperty("creatorTime")
    private String creatorTime;

    /**
     * 创建人员
     **/
    @Schema(description = "创建人员")
    @JsonProperty("creatorUserId")
    private String creatorUserId;

    /**
     * 修改时间
     **/
    @Schema(description = "修改时间")
    @JsonProperty("lastModifyTime")
    private String lastModifyTime;

    /**
     * 修改人员
     **/
    @Schema(description = "修改人员")
    @JsonProperty("lastModifyUserId")
    private String lastModifyUserId;


}