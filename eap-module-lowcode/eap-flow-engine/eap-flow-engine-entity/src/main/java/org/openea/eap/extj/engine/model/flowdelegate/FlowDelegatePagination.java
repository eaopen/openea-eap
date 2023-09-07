package org.openea.eap.extj.engine.model.flowdelegate;

import org.openea.eap.extj.base.Pagination;
import lombok.Data;

/**
 * 流程设计
 *
 *
 */
@Data
public class FlowDelegatePagination extends Pagination {
    private String myOrDelagateToMe;
}
