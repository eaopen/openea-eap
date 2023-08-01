package org.openea.eap.module.obpm.service.sys;

import org.openbpm.sys.api.model.dto.DataDictDTO;
import org.openbpm.sys.api.service.DataDictService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EapDataDictServiceImpl implements DataDictService {
    @Override
    public List<DataDictDTO> getDictNodeList(String dictKey, Boolean hasRoot) {
        return null;
    }
}
