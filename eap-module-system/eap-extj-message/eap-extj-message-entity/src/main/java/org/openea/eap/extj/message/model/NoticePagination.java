package org.openea.eap.extj.message.model;

import lombok.Data;
import org.openea.eap.extj.base.Pagination;

import java.util.List;

/**
 * 分页模型
 */
@Data
public class NoticePagination extends Pagination {
    private List<String> type;
    private List<Integer> enabledMark;
}
