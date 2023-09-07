package org.openea.eap.extj.permission.model.authorize;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizeConditionModel<T>{
	private QueryWrapper<T> obj;
	private String moduleId;
	private String tableName;
}
