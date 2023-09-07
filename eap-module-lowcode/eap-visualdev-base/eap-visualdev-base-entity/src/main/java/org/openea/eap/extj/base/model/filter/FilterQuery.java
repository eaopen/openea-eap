package org.openea.eap.extj.base.model.filter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openea.eap.extj.base.entity.FilterEntity;


@Data
@EqualsAndHashCode(callSuper = false)
public class FilterQuery extends Page<FilterEntity> {
    
}