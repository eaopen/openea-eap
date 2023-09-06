package org.openea.eap.extj.database.model.page;

import lombok.Data;

import java.util.List;

/**
 * jdbc分页模型
 *
 * 
 */
@Data
public class JdbcPageMod {

    /**
     * 页面大小
     */
    private Integer pageSize;

    /**
     * 当前页
     */
    private Integer currentPage;

    /**
     * 数据总条数
     */
    private Integer totalRecord;

    /**
     * 数据
     */
    private List<?> dataList;

}
