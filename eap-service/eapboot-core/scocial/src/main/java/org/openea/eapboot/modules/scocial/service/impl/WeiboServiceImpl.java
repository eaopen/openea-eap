package org.openea.eapboot.modules.scocial.service.impl;

import org.openea.eapboot.common.vo.SearchVo;
import org.openea.eapboot.modules.scocial.dao.WeiboDao;
import org.openea.eapboot.modules.scocial.entity.Weibo;
import org.openea.eapboot.modules.scocial.service.WeiboService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 微博登录接口实现
 */
@Slf4j
@Service
@Transactional
public class WeiboServiceImpl implements WeiboService {

    @Autowired
    private WeiboDao weiboDao;

    @Override
    public WeiboDao getRepository() {
        return weiboDao;
    }

    @Override
    public Weibo findByOpenId(String openId) {

        return weiboDao.findByOpenId(openId);
    }

    @Override
    public Weibo findByRelateUsername(String username) {

        return weiboDao.findByRelateUsername(username);
    }

    @Override
    public Page<Weibo> findByCondition(String username, String relateUsername, SearchVo searchVo, Pageable pageable) {

        return weiboDao.findAll(new Specification<Weibo>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<Weibo> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {

                Path<String> usernameField = root.get("username");
                Path<String> relateUsernameField = root.get("relateUsername");
                Path<Date> createTimeField=root.get("createTime");

                List<Predicate> list = new ArrayList<Predicate>();

                if(StrUtil.isNotBlank(username)){
                    list.add(cb.like(usernameField,'%'+ username + '%'));
                }
                if(StrUtil.isNotBlank(relateUsername)){
                    list.add(cb.like(relateUsernameField,'%'+ relateUsername + '%'));
                }
                //创建时间
                if(StrUtil.isNotBlank(searchVo.getStartDate())&&StrUtil.isNotBlank(searchVo.getEndDate())){
                    Date start = DateUtil.parse(searchVo.getStartDate());
                    Date end = DateUtil.parse(searchVo.getEndDate());
                    list.add(cb.between(createTimeField, start, DateUtil.endOfDay(end)));
                }

                Predicate[] arr = new Predicate[list.size()];
                cq.where(list.toArray(arr));
                return null;
            }
        }, pageable);
    }

    @Override
    public void deleteByUsername(String username) {

        weiboDao.deleteByUsername(username);
    }
}