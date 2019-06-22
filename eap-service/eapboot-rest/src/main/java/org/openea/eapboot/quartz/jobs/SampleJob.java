package org.openea.eapboot.quartz.jobs;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


/**
 * 示例带参定时任务
 */
@Slf4j
public class SampleJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        log.info(String.format("欢迎使用EapBoot前后端分离开发平台! 时间:"+ DateUtil.now()));
    }
}
