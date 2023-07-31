package org.openea.eap.module.visualdev.base.vo;

public class PaginationVO {
    private Long currentPage;
    private Long pageSize;
    private Integer total;

    public PaginationVO() {
    }

    public Long getCurrentPage() {
        return this.currentPage;
    }

    public Long getPageSize() {
        return this.pageSize;
    }

    public Integer getTotal() {
        return this.total;
    }

    public void setCurrentPage(Long currentPage) {
        this.currentPage = currentPage;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof PaginationVO)) {
            return false;
        } else {
            PaginationVO other = (PaginationVO)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label47: {
                    Object this$currentPage = this.getCurrentPage();
                    Object other$currentPage = other.getCurrentPage();
                    if (this$currentPage == null) {
                        if (other$currentPage == null) {
                            break label47;
                        }
                    } else if (this$currentPage.equals(other$currentPage)) {
                        break label47;
                    }

                    return false;
                }

                Object this$pageSize = this.getPageSize();
                Object other$pageSize = other.getPageSize();
                if (this$pageSize == null) {
                    if (other$pageSize != null) {
                        return false;
                    }
                } else if (!this$pageSize.equals(other$pageSize)) {
                    return false;
                }

                Object this$total = this.getTotal();
                Object other$total = other.getTotal();
                if (this$total == null) {
                    if (other$total != null) {
                        return false;
                    }
                } else if (!this$total.equals(other$total)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof PaginationVO;
    }

    public int hashCode() {
        int result = 1;
        Object $currentPage = this.getCurrentPage();
        result = result * 59 + ($currentPage == null ? 43 : $currentPage.hashCode());
        Object $pageSize = this.getPageSize();
        result = result * 59 + ($pageSize == null ? 43 : $pageSize.hashCode());
        Object $total = this.getTotal();
        result = result * 59 + ($total == null ? 43 : $total.hashCode());
        return result;
    }

    public String toString() {
        return "PaginationVO(currentPage=" + this.getCurrentPage() + ", pageSize=" + this.getPageSize() + ", total=" + this.getTotal() + ")";
    }
}