package org.openea.eap.module.visualdev.onlinedev.service;

import org.openea.eap.module.visualdev.base.entity.VisualdevEntity;
import org.openea.eap.module.visualdev.onlinedev.model.VisualdevModelDataInfoVO;

/**
 *
 * 功能设计表单数据
 */
public interface VisualDevInfoService {

	/**
	 *	编辑页数据回显
	 * @param id 主键id
	 * @param visualdevEntity 可视化实体
	 * @return
	 */
	VisualdevModelDataInfoVO getEditDataInfo(String id, VisualdevEntity visualdevEntity);

	/**
	 * 详情页数据
	 * @param id
	 * @param visualdevEntity
	 * @return
	 */
	VisualdevModelDataInfoVO getDetailsDataInfo(String id, VisualdevEntity visualdevEntity);
}
