package org.openea.eap.extj.util.treeutil;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public class SumTree<T> {
    private String id;
    private String parentId;
    private Boolean hasChildren;
    private List<SumTree<T>> children;

    public SumTree() {
    }

    public String getId() {
        return this.id;
    }

    public String getParentId() {
        return this.parentId;
    }

    public Boolean getHasChildren() {
        return this.hasChildren;
    }

    public List<SumTree<T>> getChildren() {
        return this.children;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setHasChildren(Boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public void setChildren(List<SumTree<T>> children) {
        this.children = children;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof SumTree)) {
            return false;
        } else {
            SumTree<?> other = (SumTree)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label59: {
                    Object this$hasChildren = this.getHasChildren();
                    Object other$hasChildren = other.getHasChildren();
                    if (this$hasChildren == null) {
                        if (other$hasChildren == null) {
                            break label59;
                        }
                    } else if (this$hasChildren.equals(other$hasChildren)) {
                        break label59;
                    }

                    return false;
                }

                Object this$id = this.getId();
                Object other$id = other.getId();
                if (this$id == null) {
                    if (other$id != null) {
                        return false;
                    }
                } else if (!this$id.equals(other$id)) {
                    return false;
                }

                Object this$parentId = this.getParentId();
                Object other$parentId = other.getParentId();
                if (this$parentId == null) {
                    if (other$parentId != null) {
                        return false;
                    }
                } else if (!this$parentId.equals(other$parentId)) {
                    return false;
                }

                Object this$children = this.getChildren();
                Object other$children = other.getChildren();
                if (this$children == null) {
                    if (other$children != null) {
                        return false;
                    }
                } else if (!this$children.equals(other$children)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof SumTree;
    }

    public int hashCode() {
        int result = 1;
        Object $hasChildren = this.getHasChildren();
        result = result * 59 + ($hasChildren == null ? 43 : $hasChildren.hashCode());
        Object $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Object $parentId = this.getParentId();
        result = result * 59 + ($parentId == null ? 43 : $parentId.hashCode());
        Object $children = this.getChildren();
        result = result * 59 + ($children == null ? 43 : $children.hashCode());
        return result;
    }

    public String toString() {
        return "SumTree(id=" + this.getId() + ", parentId=" + this.getParentId() + ", hasChildren=" + this.getHasChildren() + ", children=" + this.getChildren() + ")";
    }
}