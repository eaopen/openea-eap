package org.openea.eap.extj.base.model.vo;

import lombok.Data;

/**
 * 分页列表
 *
 * 
 */
@Data
public class PrintDevListVO {

    private String id;

    private String fullName;

    private String enCode;

    private Integer enabledMark;

    private String creatorUser;

    private Long creatorTime;

    private String lastModifyUser;

    private Long lastModifyTime;

    private Long sortCode;

    private String category;
}
