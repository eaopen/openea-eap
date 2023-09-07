package org.openea.eap.extj.message.model.messagedatatype;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;


@Data
public class MessageDataTypeInfoVO {
    /**
     * 主键
     **/
    @JsonProperty("id")
    private String id;

    /**
     * 数据类型
     **/
    @JsonProperty("type")
    private String type;

    /**
     * 数据名称
     **/
    @JsonProperty("fullName")
    private String fullName;

    /**
     * 数据编码
     **/
    @JsonProperty("enCode")
    private String enCode;

    /**
     * 创建时间
     **/
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonProperty("creatortime")
    private Date creatortime;

    /**
     * 创建人员
     **/
    @JsonProperty("creatorUserId")
    private String creatoruserid;

    /**
     * 修改时间
     **/
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonProperty("lastModifyTime")
    private Date lastmodifytime;

    /**
     * 修改人员
     **/
    @JsonProperty("lastModifyUserId")
    private String lastmodifyuserid;

}