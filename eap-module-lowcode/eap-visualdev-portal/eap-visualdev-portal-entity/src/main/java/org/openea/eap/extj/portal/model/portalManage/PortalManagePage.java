package org.openea.eap.extj.portal.model.portalManage;

import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.openea.eap.extj.base.Pagination;
import org.openea.eap.extj.base.vo.PaginationVO;

/**
 * 类功能
 *
 */
@Data
public class PortalManagePage extends Pagination {

    @Schema(description = "平台")
    private String platform;

    @Schema(description = "分类（字典）")
    private String category;

    @Schema(description = "系统ID")
    private String systemId;

    @Schema(description = "是否禁用")
    private Integer enabledMark;

    public <T> PageDTO<T> getPageDto(){
        return new PageDTO<T>(getCurrentPage(), getPageSize());
    }

    public PaginationVO getPaginationVO(){
        PaginationVO paginationVO = new PaginationVO();
        paginationVO.setTotal(Long.valueOf(getTotal()).intValue());
        paginationVO.setCurrentPage(getCurrentPage());
        paginationVO.setPageSize(getPageSize());
        return paginationVO;
    }

}
