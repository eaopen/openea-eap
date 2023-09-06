package org.openea.eap.extj.model;

import org.openea.eap.extj.base.Pagination;
import lombok.Data;

/**
 *
 *
 */
@Data
public class UserLogForm extends Pagination {
    private String startTime;
    private String endTime;
    private int category;
}
