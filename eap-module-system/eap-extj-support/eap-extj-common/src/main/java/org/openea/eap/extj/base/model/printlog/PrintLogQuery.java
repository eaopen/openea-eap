package org.openea.eap.extj.base.model.printlog;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openea.eap.extj.base.Pagination;


@Data
@EqualsAndHashCode(callSuper = false)
public class PrintLogQuery  extends Pagination {

    private String startTime;
    private String endTime;
}
