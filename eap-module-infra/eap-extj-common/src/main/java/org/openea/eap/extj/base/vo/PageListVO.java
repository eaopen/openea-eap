package org.openea.eap.extj.base.vo;

import java.util.List;

public class PageListVO<T> {
    private List<T> list;
    PaginationVO pagination;

    public PageListVO() {
    }

    public List<T> getList() {
        return this.list;
    }

    public PaginationVO getPagination() {
        return this.pagination;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public void setPagination(PaginationVO pagination) {
        this.pagination = pagination;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof PageListVO)) {
            return false;
        } else {
            PageListVO<?> other = (PageListVO)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$list = this.getList();
                Object other$list = other.getList();
                if (this$list == null) {
                    if (other$list != null) {
                        return false;
                    }
                } else if (!this$list.equals(other$list)) {
                    return false;
                }

                Object this$pagination = this.getPagination();
                Object other$pagination = other.getPagination();
                if (this$pagination == null) {
                    if (other$pagination != null) {
                        return false;
                    }
                } else if (!this$pagination.equals(other$pagination)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof PageListVO;
    }

    public int hashCode() {
        int result = 1;
        Object $list = this.getList();
        result = result * 59 + ($list == null ? 43 : $list.hashCode());
        Object $pagination = this.getPagination();
        result = result * 59 + ($pagination == null ? 43 : $pagination.hashCode());
        return result;
    }

    public String toString() {
        return "PageListVO(list=" + this.getList() + ", pagination=" + this.getPagination() + ")";
    }
}
