package org.openea.eapboot.modules.base.service;


import org.openea.eapboot.base.EapBaseService;
import org.openea.eapboot.common.vo.SearchVo;
import org.openea.eapboot.modules.base.entity.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 日志接口
 */
public interface LogService extends EapBaseService<Log,String> {

    /**
     * 分页搜索获取日志
     * @param type
     * @param key
     * @param searchVo
     * @param pageable
     * @return
     */
    Page<Log> findByConfition(Integer type, String key, SearchVo searchVo, Pageable pageable);

    /**
     * 删除所有
     */
    void deleteAll();
}
