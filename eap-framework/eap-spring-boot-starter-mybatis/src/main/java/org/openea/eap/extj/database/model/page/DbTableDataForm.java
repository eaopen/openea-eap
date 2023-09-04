package org.openea.eap.extj.database.model.page;

import lombok.Data;
import org.openea.eap.extj.base.Pagination;

@Data
public class DbTableDataForm extends Pagination {
    private String field;
}
