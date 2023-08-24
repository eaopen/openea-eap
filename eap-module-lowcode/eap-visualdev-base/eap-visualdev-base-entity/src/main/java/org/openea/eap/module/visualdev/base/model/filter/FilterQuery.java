package org.openea.eap.module.visualdev.base.model.filter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openea.eap.module.visualdev.base.entity.FilterEntity;


@Data
@EqualsAndHashCode(callSuper = false)
public class FilterQuery extends Page<FilterEntity> {
    
}