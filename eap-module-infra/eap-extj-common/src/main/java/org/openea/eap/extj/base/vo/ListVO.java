package org.openea.eap.extj.base.vo;

import java.util.List;

public class ListVO<T> {
    private List<T> list;

    public List<T> getList() {
        return this.list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ListVO)) {
            return false;
        } else {
            ListVO<?> other = (ListVO)o;
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

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof ListVO;
    }

    public int hashCode() {
        int result = 1;
        Object $list = this.getList();
        result = result * 59 + ($list == null ? 43 : $list.hashCode());
        return result;
    }

    public String toString() {
        return "ListVO(list=" + this.getList() + ")";
    }

    public ListVO(List<T> list) {
        this.list = list;
    }

    public ListVO() {
    }
}