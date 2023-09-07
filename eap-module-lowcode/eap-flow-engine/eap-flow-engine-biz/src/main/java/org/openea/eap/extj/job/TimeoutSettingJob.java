package org.openea.eap.extj.job;

import org.openea.eap.extj.engine.model.flowtask.WorkTimeoutJobModel;
import org.openea.eap.extj.util.RedisUtil;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 超时设置定时器
 *
 *
 */
@DisallowConcurrentExecution
public class TimeoutSettingJob extends QuartzJobBean {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private WorkTimeoutJobUtil workTimeoutJobUtil;
    @Autowired
    private
    RedisTemplate redisTemplate;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        List<WorkTimeoutJobModel> listRedis = workTimeoutJobUtil.getListRedis(redisUtil);
        for (WorkTimeoutJobModel entity : listRedis) {
            boolean useSuccess = redisTemplate.opsForValue().setIfAbsent(WorkTimeoutJobUtil.WORKTIMEOUT_REDIS_KEY+"_key:"+entity.getTaskNodeOperatorId(), System.currentTimeMillis(), 300, TimeUnit.SECONDS);
            if(!useSuccess || entity.isSuspend())continue;
            workTimeoutJobUtil.runTimeOutMethod(entity,true);
            redisUtil.remove(WorkTimeoutJobUtil.WORKTIMEOUT_REDIS_KEY+"_key:"+entity.getTaskNodeOperatorId());
        }
    }

}
