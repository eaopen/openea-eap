package org.openea.eap.extj.job;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 流程设计
 *
 *
 */

@Configuration
public class JobConfig {
    @Bean
    public JobDetail testJobDetail() {
        JobDetail JobDetail = JobBuilder.newJob(WorkJobNew.class)
                .storeDurably() //必须调用该方法，添加任务
                .build();

        return JobDetail;
    }

    @Bean
    public Trigger testTrigger() {
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("0/10 * * * * ?"); //配置任务频率，测试环境10s正式环境修改成30s
        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(testJobDetail())
                .withSchedule(cronScheduleBuilder) //对触发器配置任务
                .build();
        return trigger;
    }




    @Bean
    public JobDetail timeoutSettingJobDetail() {
        JobDetail JobDetail = JobBuilder.newJob(TimeoutSettingJob.class)
                .storeDurably() //必须调用该方法，添加任务
                .build();

        return JobDetail;
    }
    @Bean
    public Trigger timeoutTrigger() {
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("0 0 * * * ?"); //配置任务频率，测试环境每分钟，正式环境修改成每小时0 0 * * * ?
        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(timeoutSettingJobDetail())
                .withSchedule(cronScheduleBuilder) //对触发器配置任务
                .build();
        return trigger;
    }
}
