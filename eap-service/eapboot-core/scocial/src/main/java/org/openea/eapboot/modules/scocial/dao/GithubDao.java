package org.openea.eapboot.modules.scocial.dao;

import org.openea.eapboot.base.EapBaseDao;
import org.openea.eapboot.modules.scocial.entity.Github;

/**
 * Github登录数据处理层
 */
public interface GithubDao extends EapBaseDao<Github,String> {

    /**
     * 通过openId获取
     * @param openId
     * @return
     */
    Github findByOpenId(String openId);

    /**
     * 通过username获取
     * @param username
     * @return
     */
    Github findByRelateUsername(String username);

    /**
     * 通过username删除
     * @param username
     */
    void deleteByUsername(String username);
}