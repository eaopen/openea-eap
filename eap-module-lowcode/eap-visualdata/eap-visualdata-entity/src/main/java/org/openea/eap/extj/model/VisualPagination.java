package org.openea.eap.extj.model;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 大屏分类分页模型
 *
 *
 */
@Data
public class VisualPagination {
    @Schema(description = "每页条数",example = "10")
    private long size=10;
    @Schema(description = "当前页数",example = "1")
    private long current=1;
    @Schema(hidden = true)
    private long total;
    @Schema(hidden = true)
    private long pages;

    public <T> List<T> setData(IPage<T> page) {
        this.total = page.getTotal();
        if (this.total > 0) {
            this.pages = this.total % this.size == 0 ? this.total / this.size : this.total / this.size + 1;
        } else {
            this.pages = 0L;
        }
        return page.getRecords();
    }
}
