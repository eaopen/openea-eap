package org.openea.eap.framework.common.pojo;

import lombok.Data;

@Data
public class PaginationVO {
    Integer currentPage;   //currentPage/pageNo
    Integer pageSize;
    Long total;
}
