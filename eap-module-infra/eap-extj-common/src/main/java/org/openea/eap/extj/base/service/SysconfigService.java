package org.openea.eap.extj.base.service;

import org.openea.eap.extj.model.BaseSystemInfo;

public interface SysconfigService
//        extends SuperService<SysConfigEntity>
{

    /**
     * 获取系统配置信息
     *
     * @return ignore
     */
    BaseSystemInfo getSysInfo();

}
