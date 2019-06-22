package org.openea.eapboot.modules.your;

import org.openea.eapboot.common.annotation.RateLimiter;
import org.openea.eapboot.common.annotation.SystemLog;
import org.openea.eapboot.common.enums.LogType;
import org.openea.eapboot.common.lock.Callback;
import org.openea.eapboot.common.lock.RedisDistributedLockTemplate;
import org.openea.eapboot.common.utils.ResultUtil;
import org.openea.eapboot.common.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;


/**
 */
@Slf4j
@Controller
@Api(description = "测试接口")
@Transactional
@RequestMapping(value = "/test")
public class TestController {

    @Autowired
    private RedisDistributedLockTemplate lockTemplate;

    @RequestMapping(value = "/lockAndLimit", method = RequestMethod.GET)
    @SystemLog(description = "短信登录", type = LogType.LOGIN)
    @RateLimiter(limit = 1, timeout = 5000)
    @ApiOperation(value = "同步锁限流测试")
    @ResponseBody
    public Result<Object> test1(HttpServletResponse response){

        lockTemplate.execute("订单流水号", 5000, new Callback() {
            @Override
            public Object onGetLock() throws InterruptedException {
                //TODO 获得锁后要做的事
                log.info("生成订单流水号");
                return null;
            }

            @Override
            public Object onTimeout() throws InterruptedException {
                //TODO 获得锁超时后要做的事
                return null;
            }
        });

        return new ResultUtil<Object>().setData(null);
    }
}

