package org.openea.eap.extj.extend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.openea.eap.extj.extend.entity.ProvinceAtlasEntity;
import org.openea.eap.extj.extend.mapper.ProvinceAtlasMapper;
import org.openea.eap.extj.extend.service.ProvinceAtlasService;
import org.openea.eap.extj.util.StringUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 行政区划
 *
 */
@Service
public class ProvinceAtlasServiceImpl extends SuperServiceImpl<ProvinceAtlasMapper, ProvinceAtlasEntity>
        implements ProvinceAtlasService {

    @Override
    public List<ProvinceAtlasEntity> getList() {
        QueryWrapper<ProvinceAtlasEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ProvinceAtlasEntity::getEnabledMark, 1);
        return  this.list(queryWrapper);
    }

    @Override
    public List<ProvinceAtlasEntity> getListByPid(String pid) {
        QueryWrapper<ProvinceAtlasEntity> queryWrapper = new QueryWrapper<>();
        if (StringUtil.isNotEmpty(pid)) {
            queryWrapper.lambda().eq(ProvinceAtlasEntity::getParentId, pid);
        }else{
            queryWrapper.lambda().eq(ProvinceAtlasEntity::getParentId, "-1");
        }
        queryWrapper.lambda().eq(ProvinceAtlasEntity::getEnabledMark, 1);
        return  this.list(queryWrapper);
    }
}
