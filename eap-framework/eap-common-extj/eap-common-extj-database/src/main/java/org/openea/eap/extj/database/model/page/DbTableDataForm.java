package org.openea.eap.extj.database.model.page;

import org.openea.eap.extj.base.Pagination;
import lombok.Data;

/**
 * 表数据页面对象
 *
 * 
 */
@Data
public class DbTableDataForm extends Pagination {
     private String field;
}
