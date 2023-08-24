package org.openea.eap.extj.database.model.page;

import lombok.Data;

import java.util.List;

@Data
public class JdbcPageMod {

    private Integer pageSize;
    private Integer currentPage;
    private Integer totalRecord;
    private List<?> dataList;
}
