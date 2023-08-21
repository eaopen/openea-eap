package org.openea.eap.module.visualdev.base.model.InterfaceOauth;

import lombok.Data;
import org.openea.eap.extj.base.Pagination;

@Data
public class PaginationOauth extends Pagination {
    private String keyword;
}
