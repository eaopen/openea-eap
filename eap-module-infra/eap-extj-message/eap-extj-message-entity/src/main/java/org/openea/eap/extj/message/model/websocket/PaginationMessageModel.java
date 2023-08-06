package org.openea.eap.extj.message.model.websocket;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 消息分页参数模型
 */
@Data
@Builder
public class PaginationMessageModel implements Serializable {

    /**
     * 当前页
     */
    private Integer currentPage;


    private Integer pageSize;


    private Long total;

}
