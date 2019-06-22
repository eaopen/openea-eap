package org.openea.eapboot.modules.base.serviceimpl;

import org.openea.eapboot.common.vo.SearchVo;
import org.openea.eapboot.modules.base.dao.QQDao;
import org.openea.eapboot.modules.base.entity.social.QQ;
import org.openea.eapboot.modules.base.service.QQService;
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
 * qq登录接口实现
 */
@Slf4j
@Service
@Transactional
public class QQServiceImpl implements QQService {

    @Autowired
    private QQDao qqDao;

    @Override
    public QQDao getRepository() {
        return qqDao;
    }

    @Override
    public QQ findByOpenId(String openId) {

        return qqDao.findByOpenId(openId);
    }

    @Override
    public QQ findByRelateUsername(String username) {

        return qqDao.findByRelateUsername(username);
    }

    @Override
    public Page<QQ> findByCondition(String username, String relateUsername, SearchVo searchVo, Pageable pageable) {

        return qqDao.findAll(new Specification<QQ>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<QQ> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {

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

        qqDao.deleteByUsername(username);
    }
}