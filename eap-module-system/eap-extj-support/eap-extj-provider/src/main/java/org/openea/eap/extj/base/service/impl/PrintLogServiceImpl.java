package org.openea.eap.extj.base.service.impl;

import org.openea.eap.extj.base.entity.PrintLogEntity;
import org.openea.eap.extj.base.mapper.PrintLogMapper;
import org.openea.eap.extj.base.service.PrintLogService;
import org.openea.eap.extj.base.service.SuperServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class PrintLogServiceImpl extends SuperServiceImpl<PrintLogMapper, PrintLogEntity> implements PrintLogService {

}