package org.openea.eap.module.visualdev.base.service;


import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.module.visualdev.base.entity.VisualdevReleaseEntity;

/**
 *
 */
public interface VisualdevReleaseService extends SuperService<VisualdevReleaseEntity> {

    long beenReleased(String id);

}

