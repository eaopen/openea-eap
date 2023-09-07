package org.openea.eap.extj.base.model.comfields;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ComFieldsListVO {
    private String id;
    private String fieldName;
    private String dataType;
    private String dataLength;
    private Integer allowNull;
    @NotBlank(message = "必填")
    private String field;
    private Long creatorTime;
}
