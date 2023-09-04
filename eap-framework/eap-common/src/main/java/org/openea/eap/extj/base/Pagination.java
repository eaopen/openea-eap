package org.openea.eap.extj.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
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

}