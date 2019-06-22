package org.openea.eapboot.modules.base.controller.manage;

import org.openea.eapboot.common.constant.CommonConstant;
import org.openea.eapboot.common.utils.PageUtil;
import org.openea.eapboot.common.utils.ResultUtil;
import org.openea.eapboot.common.vo.PageVo;
import org.openea.eapboot.modules.base.service.GithubService;
import org.openea.eapboot.modules.base.service.QQService;
import org.openea.eapboot.modules.base.service.WeiboService;
import org.openea.eapboot.modules.base.vo.RelateUserInfo;
import org.openea.eapboot.common.vo.Result;
import org.openea.eapboot.common.vo.SearchVo;
import org.openea.eapboot.modules.base.entity.social.Github;
import org.openea.eapboot.modules.base.entity.social.QQ;
import org.openea.eapboot.modules.base.entity.social.Weibo;
import org.openea.eapboot.modules.base.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 */
@Slf4j
@RestController
@Api(description = "社交账号接口")
@RequestMapping("/eapboot/relate")
@CacheConfig(cacheNames = "relate")
@Transactional
public class SocialController {

    @Autowired
    private GithubService githubService;

    @Autowired
    private QQService qqService;

    @Autowired
    private WeiboService weiboService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RequestMapping(value = "/getRelatedInfo/{username}", method = RequestMethod.GET)
    @ApiOperation(value = "获取绑定账号信息")
    @Cacheable(key = "'relatedInfo:'+#username")
    public Result<RelateUserInfo> getRelateUserInfo(@PathVariable String username){

        RelateUserInfo r = new RelateUserInfo();
        Github g = githubService.findByRelateUsername(username);
        if(g!=null){
            r.setGithubId(g.getId());
            r.setGithub(g.getIsRelated());
            r.setGithubUsername(g.getUsername());
        }
        QQ q = qqService.findByRelateUsername(username);
        if(q!=null){
            r.setQqId(q.getId());
            r.setQq(q.getIsRelated());
            r.setQqUsername(q.getUsername());
        }
        Weibo w = weiboService.findByRelateUsername(username);
        if(w!=null){
            r.setWeiboId(w.getId());
            r.setWeibo(w.getIsRelated());
            r.setWeiboUsername(w.getUsername());
        }
        return new ResultUtil<RelateUserInfo>().setData(r);
    }

    @RequestMapping(value = "/delByIds", method = RequestMethod.POST)
    @ApiOperation(value = "解绑")
    public Result<Object> delByIds(@RequestParam String[] ids,
                                   @RequestParam String[] usernames,
                                   @RequestParam Integer socialType){

        for(String id : ids){
            if(CommonConstant.SOCIAL_TYPE_GITHUB.equals(socialType)){
                githubService.delete(id);
            }else if(CommonConstant.SOCIAL_TYPE_QQ.equals(socialType)){
                qqService.delete(id);
            }else if(CommonConstant.SOCIAL_TYPE_WEIBO.equals(socialType)){
                weiboService.delete(id);
            }
        }
        // 删除缓存
        for(String username : usernames){
            redisTemplate.delete("relate::relatedInfo:" + username);
        }
        return new ResultUtil<Object>().setSuccessMsg("解绑成功");
    }

    @RequestMapping(value = "/findByCondition", method = RequestMethod.GET)
    @ApiOperation(value = "多条件分页获取")
    public Result<Object> delByIds(@RequestParam(required = false) String username,
                                   @RequestParam(required = false) String relateUsername,
                                   @RequestParam Integer socialType,
                                   @ModelAttribute SearchVo searchVo,
                                   @ModelAttribute PageVo pv){

        if(CommonConstant.SOCIAL_TYPE_GITHUB.equals(socialType)){
            Page<Github> githubPage = githubService.findByCondition(username, relateUsername, searchVo, PageUtil.initPage(pv));
            return new ResultUtil<Object>().setData(githubPage);
        }else if(CommonConstant.SOCIAL_TYPE_QQ.equals(socialType)){
            Page<QQ> qqPage = qqService.findByCondition(username, relateUsername, searchVo, PageUtil.initPage(pv));
            return new ResultUtil<Object>().setData(qqPage);
        }else if(CommonConstant.SOCIAL_TYPE_WEIBO.equals(socialType)){
            Page<Weibo> weiboPage = weiboService.findByCondition(username, relateUsername, searchVo, PageUtil.initPage(pv));
            return new ResultUtil<Object>().setData(weiboPage);
        }
        return new ResultUtil<Object>().setErrorMsg("获取第三方绑定账号信息失败");
    }
}
