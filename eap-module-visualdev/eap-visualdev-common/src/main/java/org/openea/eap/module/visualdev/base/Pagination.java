package org.openea.eap.module.visualdev.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

public class Pagination extends Page {
    private long pageSize = 20L;
    private String sort = "DESC";
    private String sidx = "";
    private long currentPage = 1L;
    @JsonIgnore
    private long total;
    @JsonIgnore
    private long records;

    public <T> List<T> setData(List<T> data, long records) {
        this.total = records;
        return data;
    }

    public Pagination() {
    }

    public long getPageSize() {
        return this.pageSize;
    }

    public String getSort() {
        return this.sort;
    }

    public String getSidx() {
        return this.sidx;
    }

    public long getCurrentPage() {
        return this.currentPage;
    }

    public long getTotal() {
        return this.total;
    }

    public long getRecords() {
        return this.records;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public void setSidx(String sidx) {
        this.sidx = sidx;
    }

    public void setCurrentPage(long currentPage) {
        this.currentPage = currentPage;
    }

    @JsonIgnore
    public void setTotal(long total) {
        this.total = total;
    }

    @JsonIgnore
    public void setRecords(long records) {
        this.records = records;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Pagination)) {
            return false;
        } else {
            Pagination other = (Pagination)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getPageSize() != other.getPageSize()) {
                return false;
            } else if (this.getCurrentPage() != other.getCurrentPage()) {
                return false;
            } else if (this.getTotal() != other.getTotal()) {
                return false;
            } else if (this.getRecords() != other.getRecords()) {
                return false;
            } else {
                Object this$sort = this.getSort();
                Object other$sort = other.getSort();
                if (this$sort == null) {
                    if (other$sort != null) {
                        return false;
                    }
                } else if (!this$sort.equals(other$sort)) {
                    return false;
                }

                Object this$sidx = this.getSidx();
                Object other$sidx = other.getSidx();
                if (this$sidx == null) {
                    if (other$sidx != null) {
                        return false;
                    }
                } else if (!this$sidx.equals(other$sidx)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof Pagination;
    }

    public int hashCode() {
        int result = 1;
        long $pageSize = this.getPageSize();
        result = result * 59 + (int)($pageSize >>> 32 ^ $pageSize);
        long $currentPage = this.getCurrentPage();
        result = result * 59 + (int)($currentPage >>> 32 ^ $currentPage);
        long $total = this.getTotal();
        result = result * 59 + (int)($total >>> 32 ^ $total);
        long $records = this.getRecords();
        result = result * 59 + (int)($records >>> 32 ^ $records);
        Object $sort = this.getSort();
        result = result * 59 + ($sort == null ? 43 : $sort.hashCode());
        Object $sidx = this.getSidx();
        result = result * 59 + ($sidx == null ? 43 : $sidx.hashCode());
        return result;
    }

    public String toString() {
        return "Pagination(pageSize=" + this.getPageSize() + ", sort=" + this.getSort() + ", sidx=" + this.getSidx() + ", currentPage=" + this.getCurrentPage() + ", total=" + this.getTotal() + ", records=" + this.getRecords() + ")";
    }
}