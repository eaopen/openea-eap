package org.openea.eapboot.modules.base.serviceimpl;

import org.openea.eapboot.common.vo.SearchVo;
import org.openea.eapboot.modules.base.dao.GithubDao;
import org.openea.eapboot.modules.base.entity.social.Github;
import org.openea.eapboot.modules.base.service.GithubService;
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
 * Github登录接口实现
 */
@Slf4j
@Service
@Transactional
public class GithubServiceImpl implements GithubService {

    @Autowired
    private GithubDao githubDao;

    @Override
    public GithubDao getRepository() {
        return githubDao;
    }

    @Override
    public Github findByOpenId(String openId) {

        return githubDao.findByOpenId(openId);
    }

    @Override
    public Github findByRelateUsername(String username) {

        return githubDao.findByRelateUsername(username);
    }

    @Override
    public Page<Github> findByCondition(String username, String relateUsername, SearchVo searchVo, Pageable pageable) {

        return githubDao.findAll(new Specification<Github>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<Github> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {

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

        githubDao.deleteByUsername(username);
    }
}