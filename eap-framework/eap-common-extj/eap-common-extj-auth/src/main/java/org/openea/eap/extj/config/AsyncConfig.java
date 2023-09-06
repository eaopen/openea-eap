package org.openea.eap.extj.config;

import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.util.data.DataSourceContextHolder;
import org.openea.eap.extj.util.UserProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 提供一个全局的Spring线程池对象
 *
 */
@Slf4j
@Configuration
@EnableAsync(proxyTargetClass = true)
@AllArgsConstructor
public class AsyncConfig implements AsyncConfigurer {


//    private final Map<Object, Integer> asyncTaskCount = new HashMap<>();



    @Primary
    @Bean("threadPoolTaskExecutor")
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置线程池核心容量
        executor.setCorePoolSize(10);
        // 设置线程池最大容量
        executor.setMaxPoolSize(50);
        // 设置任务队列长度
        executor.setQueueCapacity(2000);
        // 设置线程超时时间
        executor.setKeepAliveSeconds(30);
        // 设置线程名称前缀
        executor.setThreadNamePrefix("sysTaskExecutor");
        // 设置任务丢弃后的处理策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setTaskDecorator( r ->{
            //实现线程上下文穿透， 异步线程内无法获取之前的Request，租户信息等， 如有新的上下文对象在此处添加
            //此方法在请求结束后在无法获取request, 下方完整异步Servlet请求
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            String dataSourceId = DataSourceContextHolder.getDatasourceId();
            String dataSourceName = DataSourceContextHolder.getDatasourceName();
            Boolean isAssign = DataSourceContextHolder.isAssignDataSource();
            UserInfo userInfo = UserProvider.getUser();
            return () -> {
                try {
                    if(attributes!= null) {
                        RequestContextHolder.setRequestAttributes(attributes);
                    }
                    if(dataSourceId != null || dataSourceName != null){
                        DataSourceContextHolder.setDatasource(dataSourceId, dataSourceName, isAssign);
                    }
                    UserProvider.setLocalLoginUser(userInfo);
                    r.run();
                } finally {
                    UserProvider.clearLocalUser();
                    RequestContextHolder.resetRequestAttributes();
                    DataSourceContextHolder.clearDatasourceType();
                }
            };
        });

        /*
        //Tomcat异步Servlet只能增加吞吐量, 不能减少响应时间
        executor.setTaskDecorator( r ->{
            //实现线程上下文穿透， 异步线程内无法获取之前的Request，租户信息等， 如有新的上下文对象在此处添加
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            String dataSourceId = DataSourceContextHolder.getDatasourceId();
            String dataSourceName = DataSourceContextHolder.getDatasourceName();
            final AsyncContext asyncContext = (AsyncContext) Optional.ofNullable(null).orElseGet(()->{
                if(attributes!= null) {
                    HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
                    HttpServletResponse response = ((ServletRequestAttributes) attributes).getResponse();
                    synchronized (AsyncConfig.class) {
                        //开启多个异步
                        AsyncContext tmpAsyncContext = request.isAsyncStarted() ? request.getAsyncContext() : request.startAsync(request, response);
                        asyncTaskCount.put(tmpAsyncContext, asyncTaskCount.getOrDefault(tmpAsyncContext, 0) +1);
                        return tmpAsyncContext;
                    }
                }
                return null;
            });
            return () -> {
                if (asyncContext != null) {
                    asyncContext.start(() -> {
                        if (dataSourceId != null || dataSourceName != null) {
                            DataSourceContextHolder.setDatasource(dataSourceId, dataSourceName);
                        }
                        RequestContextHolder.setRequestAttributes(attributes);
                        try {
                            r.run();
                        }finally{
                            synchronized (AsyncConfig.class){
                                //多个异步 最后一个执行关闭
                                int count = asyncTaskCount.get(asyncContext)-1;
                                if(count > 0){
                                    asyncTaskCount.put(asyncContext, count);
                                }else{
                                    asyncTaskCount.remove(asyncContext);
                                    asyncContext.complete();
                                }
                            }
                            RequestContextHolder.resetRequestAttributes();
                            DataSourceContextHolder.clearDatasourceType();
                        }
                    });
                } else {
                    if (dataSourceId != null || dataSourceName != null) {
                        DataSourceContextHolder.setDatasource(dataSourceId, dataSourceName);
                    }
                    try {
                        r.run();
                    }finally{
                        DataSourceContextHolder.clearDatasourceType();
                    }
                }
            };
        });
         */
        return executor;
    }


    @Bean("defaultExecutor")
    public ThreadPoolTaskExecutor getAsyncExecutorDef(@Qualifier("threadPoolTaskExecutor") Executor executor) {
        return (ThreadPoolTaskExecutor) executor;
    }

}
