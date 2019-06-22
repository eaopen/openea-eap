package org.openea.eapboot.config.interceptor;

import org.openea.eapboot.common.annotation.RateLimiter;
import org.openea.eapboot.common.constant.CommonConstant;
import org.openea.eapboot.common.constant.SettingConstant;
import org.openea.eapboot.common.exception.EapbootException;
import org.openea.eapboot.common.limit.RedisRaterLimiter;
import org.openea.eapboot.common.utils.IpInfoUtil;
import org.openea.eapboot.modules.base.vo.OtherSetting;
import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 限流拦截器
 */
@Slf4j
@Component
public class LimitRaterInterceptor extends HandlerInterceptorAdapter {

    @Value("${eapboot.rateLimit.enable}")
    private boolean rateLimitEnable;

    @Value("${eapboot.rateLimit.limit}")
    private Integer limit;

    @Value("${eapboot.rateLimit.timeout}")
    private Integer timeout;

    @Autowired
    private RedisRaterLimiter redisRaterLimiter;

    @Autowired
    private IpInfoUtil ipInfoUtil;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public OtherSetting getOtherSetting(){

        String v = redisTemplate.opsForValue().get(SettingConstant.OTHER_SETTING);
        if(StrUtil.isBlank(v)){
            return null;
        }
        return new Gson().fromJson(v, OtherSetting.class);
    }

    /**
     * 预处理回调方法，实现处理器的预处理（如登录检查）
     * 第三个参数为响应的处理器，即controller
     * 返回true，表示继续流程，调用下一个拦截器或者处理器
     * 返回false，表示流程中断，通过response产生响应
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        // IP限流 在线Demo所需 一秒限15个请求
        String ip = ipInfoUtil.getIpAddr(request);
        String token1 = redisRaterLimiter.acquireTokenFromBucket(ip, 15, 1000);
        if (StrUtil.isBlank(token1)) {
            throw new EapbootException("你手速怎么这么快，请点慢一点");
        }

        if(rateLimitEnable){
            String token2 = redisRaterLimiter.acquireTokenFromBucket(CommonConstant.LIMIT_ALL, limit, timeout);
            if (StrUtil.isBlank(token2)) {
                throw new EapbootException("当前访问总人数太多啦，请稍后再试");
            }
        }

        // IP黑名单
        OtherSetting os = getOtherSetting();
        if(os!=null&&StrUtil.isNotBlank(os.getBlacklist())){
            String[] list = os.getBlacklist().split("\n");
            for(String item : list){
                if(item.equals(ip)){
                    throw new EapbootException("滚");
                }
            }
        }

        try {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            RateLimiter rateLimiter = method.getAnnotation(RateLimiter.class);
            if (rateLimiter != null) {
                int limit = rateLimiter.limit();
                int timeout = rateLimiter.timeout();
                String token3 = redisRaterLimiter.acquireTokenFromBucket(method.getName(), limit, timeout);
                if (StrUtil.isBlank(token3)) {
                    throw new EapbootException("当前访问人数太多啦，请稍后再试");
                }
            }
        }catch (Exception e){

        }

        return true;
    }

    /**
     * 当前请求进行处理之后，也就是Controller方法调用之后执行，
     * 但是它会在DispatcherServlet 进行视图返回渲染之前被调用。
     * 此时我们可以通过modelAndView对模型数据进行处理或对视图进行处理。
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 方法将在整个请求结束之后，也就是在DispatcherServlet渲染了对应的视图之后执行。
     * 这个方法的主要作用是用于进行资源清理工作的。
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
    }

}
