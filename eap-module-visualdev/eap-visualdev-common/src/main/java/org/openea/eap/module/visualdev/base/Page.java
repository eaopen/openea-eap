package org.openea.eap.module.visualdev.base;

public class Page {
    private String keyword = "";

    public Page() {
    }

    public String getKeyword() {
        return this.keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Page)) {
            return false;
        } else {
            Page other = (Page)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$keyword = this.getKeyword();
                Object other$keyword = other.getKeyword();
                if (this$keyword == null) {
                    if (other$keyword != null) {
                        return false;
                    }
                } else if (!this$keyword.equals(other$keyword)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof Page;
    }

    public int hashCode() {
        int result = 1;
        Object $keyword = this.getKeyword();
        result = result * 59 + ($keyword == null ? 43 : $keyword.hashCode());
        return result;
    }

    public String toString() {
        return "Page(keyword=" + this.getKeyword() + ")";
    }
}
