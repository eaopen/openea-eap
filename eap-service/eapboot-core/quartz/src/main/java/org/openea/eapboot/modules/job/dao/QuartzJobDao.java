package org.openea.eapboot.modules.job.dao;

import org.openea.eapboot.base.EapBaseDao;
import org.openea.eapboot.modules.job.entity.QuartzJob;

import java.util.List;

/**
 * 定时任务数据处理层
 */
public interface QuartzJobDao extends EapBaseDao<QuartzJob,String> {

    /**
     * 通过类名获取
     * @param jobClassName
     * @return
     */
    List<QuartzJob> findByJobClassName(String jobClassName);
}