package org.openea.eap.extj.permission.model.role;


import lombok.Data;
import org.openea.eap.extj.base.Pagination;

@Data
public class RolePagination extends Pagination {
    private String organizeId;
}
