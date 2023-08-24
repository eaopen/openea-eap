package org.openea.eap.module.visualdev.base.model.InterfaceOauth;

import lombok.Data;
import org.openea.eap.extj.base.PaginationTime;

@Data
public class PaginationIntrfaceLog extends PaginationTime {
    private String keyword;
}
