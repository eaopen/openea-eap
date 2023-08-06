package org.openea.eap.extj.base;

import org.openea.eap.extj.base.vo.PageListVO;

public class DataInterfacePageListVO<T> extends PageListVO {
    private String dataProcessing;

    public DataInterfacePageListVO() {
    }

    public String getDataProcessing() {
        return this.dataProcessing;
    }

    public void setDataProcessing(String dataProcessing) {
        this.dataProcessing = dataProcessing;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof DataInterfacePageListVO)) {
            return false;
        } else {
            DataInterfacePageListVO<?> other = (DataInterfacePageListVO)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$dataProcessing = this.getDataProcessing();
                Object other$dataProcessing = other.getDataProcessing();
                if (this$dataProcessing == null) {
                    if (other$dataProcessing != null) {
                        return false;
                    }
                } else if (!this$dataProcessing.equals(other$dataProcessing)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof DataInterfacePageListVO;
    }

    public int hashCode() {
        int result = 1;
        Object $dataProcessing = this.getDataProcessing();
        result = result * 59 + ($dataProcessing == null ? 43 : $dataProcessing.hashCode());
        return result;
    }

    public String toString() {
        return "DataInterfacePageListVO(dataProcessing=" + this.getDataProcessing() + ")";
    }
}
