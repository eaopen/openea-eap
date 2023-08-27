package org.openea.eap.module.visualdev.generater.service;


import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.module.visualdev.base.entity.VisualdevEntity;
import org.openea.eap.module.visualdev.base.model.DownloadCodeForm;

/**
 *
 */
public interface VisualdevGenService extends SuperService<VisualdevEntity> {

    /**
     * 代码生成
     * @param visualdevEntity 可视化开发功能
     * @param downloadCodeForm 下载相关信息
     * @return 下载文件名
     * @throws Exception ignore
     */
    String codeGengerate(VisualdevEntity visualdevEntity, DownloadCodeForm downloadCodeForm) throws Exception;
}

