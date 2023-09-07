package org.openea.eap.extj.base.model.vo;

import lombok.Data;

@Data
public class PrintLogVO {

    /**
     * 打印人
     */
    private String printMan;
    /**
     * 打印时间
     */
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh")
    private long printTime;
    /**
     * 打印条数
     */
    private Integer printNum;
    /**
     * 打印功能名称
     */
    private String printTitle;

    /**
     * 基于哪一个模板
     */
    private String printId;
}
