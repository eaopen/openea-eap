package org.openea.eap.extj.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 大屏分类分页模型
 *
 *
 */
@Data
public class VisualPageVO<T> {
    /**
     * 数据
     */
    @Schema(description = "数据")
    private List<T> records;
    /**
     * 当前页
     */
    @Schema(description = "当前页")
    private Long current;
    /**
     * 每页行数
     */
    @Schema(description = "每页行数")
    private Long size;
    /**
     * 总记录数
     */
    @Schema(description = "总记录数")
    private Long total;
    /**
     * 总页数
     */
    @Schema(description = "总页数")
    private Long pages;

}
