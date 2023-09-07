package org.openea.eap.extj.message.model.websocket;

import lombok.Builder;
import lombok.Data;
import org.openea.eap.extj.message.entity.ImContentEntity;

import java.io.Serializable;
import java.util.List;

/**
 * 消息分页返回模型
 */
@Data
@Builder
public class PaginationMessageVo implements Serializable {

    /**
     * 消息列表
     */
    private List<ImContentEntity> list;

    /**
     * 分页参数
     */
    private PaginationMessageModel pagination;

    /**
     * 方法名
     */
    private String method;
}
