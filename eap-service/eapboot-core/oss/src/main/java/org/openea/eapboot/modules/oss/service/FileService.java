package org.openea.eapboot.modules.oss.service;

import org.openea.eapboot.base.EapBaseService;
import org.openea.eapboot.common.vo.SearchVo;
import org.openea.eapboot.modules.oss.entity.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 文件管理接口
 */
public interface FileService extends EapBaseService<File,String> {

    /**
     * 多条件获取列表
     * @param file
     * @param searchVo
     * @param pageable
     * @return
     */
    Page<File> findByCondition(File file, SearchVo searchVo, Pageable pageable);
}