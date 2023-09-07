package org.openea.eap.extj.permission.model.organize;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.openea.eap.extj.base.Page;

import java.io.Serializable;
import java.util.List;

/**
 * 
 */
@Data
public class OrganizeConditionModel extends Page implements Serializable {

    @Schema(description = "部门id集合")
    private List<String> departIds;

}
