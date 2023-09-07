package org.openea.eap.extj.base.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.base.entity.VisualdevReleaseEntity;
import org.openea.eap.extj.base.mapper.VisualdevReleaseMapper;
import org.openea.eap.extj.base.service.VisualdevReleaseService;
import org.springframework.stereotype.Service;

/**

 */
@Service
public class VisualdevReleaseServiceImpl extends SuperServiceImpl<VisualdevReleaseMapper, VisualdevReleaseEntity> implements VisualdevReleaseService {

	@Override
	public long beenReleased(String id) {
		QueryWrapper<VisualdevReleaseEntity> queryWrapper = new QueryWrapper<>();
		queryWrapper.lambda().eq(VisualdevReleaseEntity::getId, id);
		return this.count(queryWrapper);
	}
}
