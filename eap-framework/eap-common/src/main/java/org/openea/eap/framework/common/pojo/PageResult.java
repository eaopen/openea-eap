package org.openea.eap.framework.common.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "分页结果")
@Data
public final class PageResult<T> implements Serializable {

    @Schema(description = "数据", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<T> list;

    @Schema(description = "总量", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long total;

    @Schema(description = "分页", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private PaginationVO pagination;

    public PageResult() {
    }

    public PageResult(List<T> list, Long total) {
        this.list = list;
        this.total = total;
        // 针对返回全部数据进行优化
        if(this.pagination!=null){
            this.pagination.total = this.total;
            if(this.list!=null && this.list.size()>this.pagination.pageSize){
                this.pagination.pageSize = (list.size()+1)%10*10;
            }
        }
    }

    public PageResult(List<T> list, PaginationVO pagination){
        this.list = list;
        this.pagination = pagination;
        if(this.pagination!=null && this.total==null){
            this.total = this.pagination.total;
        }
    }

    public PageResult(Long total) {
        this.list = new ArrayList<>();
        this.total = total;
    }

    public static <T> PageResult<T> empty() {
        return new PageResult<>(0L);
    }

    public static <T> PageResult<T> empty(Long total) {
        return new PageResult<>(total);
    }

}
