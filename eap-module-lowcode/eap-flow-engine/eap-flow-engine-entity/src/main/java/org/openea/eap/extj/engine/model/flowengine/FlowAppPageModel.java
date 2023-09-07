package org.openea.eap.extj.engine.model.flowengine;

import org.openea.eap.extj.base.vo.PaginationVO;
import org.openea.eap.extj.engine.entity.FlowTemplateEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlowAppPageModel {
    private List<FlowTemplateEntity> list = new ArrayList<>();
    private PaginationVO paginationVO = new PaginationVO();
}
