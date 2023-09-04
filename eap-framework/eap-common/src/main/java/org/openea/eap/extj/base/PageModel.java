package org.openea.eap.extj.base;

import lombok.Data;

import java.util.List;

@Data
public class PageModel {
    private int rows;
    private int page;
    private long total;
    private long records;
    private String sidx;
    private String sord;
    private String queryJson;
    private String keyword;

    public <T> List<T> setData(List<T> data, long records) {
        this.records = records;
        if (this.records > 0L) {
            this.total = this.records % (long)this.rows == 0L ? this.records / (long)this.rows : this.records / (long)this.rows + 1L;
        } else {
            this.total = 0L;
        }

        return data;
    }

}