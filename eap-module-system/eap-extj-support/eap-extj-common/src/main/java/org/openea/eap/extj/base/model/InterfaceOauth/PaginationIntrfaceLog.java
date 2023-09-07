package org.openea.eap.extj.base.model.InterfaceOauth;

import lombok.Data;
import org.openea.eap.extj.base.PaginationTime;

@Data
public class PaginationIntrfaceLog extends PaginationTime {
    private String keyword;
}
