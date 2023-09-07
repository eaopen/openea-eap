package org.openea.eap.extj.base.model.dbtable;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.openea.eap.extj.base.vo.PaginationVO;

import java.util.List;

@Data
@AllArgsConstructor
public class DbTableListVO<T> {

    /**
     * 数据集合
     */
    private List<T> list;

    /**
     * 分页信息
     */
    PaginationVO pagination;

}
