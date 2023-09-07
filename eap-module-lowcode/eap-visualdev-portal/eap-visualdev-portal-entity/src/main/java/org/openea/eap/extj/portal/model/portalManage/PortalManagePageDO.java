package org.openea.eap.extj.portal.model.portalManage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.openea.eap.extj.portal.entity.PortalManageEntity;
import org.springframework.beans.BeanUtils;

import java.util.Date;


/**
 * 类功能
 *
 */
@Data
public class PortalManagePageDO extends PortalManageEntity {

    @Schema(description = "门户管理ID")
    private String id;

    @Schema(description = "门户名")
    private String portalName;

    @Schema(description = "分类名")
    private String categoryName;

    @Schema(description = "分类Id")
    private String categoryId;

    @Schema(description = "创建者名")
    private String createUserName;

    @Schema(description = "创建时间")
    private Date creatorTime;

    @Schema(description = "创建者账号")
    private String createUserAccount;

    @Schema(description = "修改者名")
    private String modifyUserName;

    @Schema(description = "修改者账号")
    private String modifyUserAccount;

    @Schema(description = "最后修改时间")
    private Date lastModifyTime;

    public PortalManageVO convert(){
        PortalManageVO vo = new PortalManageVO();
        BeanUtils.copyProperties(this, vo);
        // 创建人
        if(StringUtils.isNotEmpty(createUserName)) vo.setCreatorUser(createUserName + "/" + createUserAccount);
        // 修改人
        if(StringUtils.isNotEmpty(modifyUserName)) vo.setLastModifyUser(modifyUserName + "/" + modifyUserAccount);
        // 门户名称
        vo.setFullName(portalName);
        vo.setCategoryName(categoryName);
        return vo;
    }

}
