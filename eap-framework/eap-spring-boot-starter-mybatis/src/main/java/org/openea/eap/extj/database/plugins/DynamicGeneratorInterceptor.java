package org.openea.eap.extj.database.plugins;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.aop.DynamicDataSourceAnnotationInterceptor;
import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.baomidou.dynamic.datasource.processor.DsProcessor;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import org.aopalliance.intercept.MethodInvocation;
import org.openea.eap.extj.database.util.ConnUtil;
import org.openea.eap.extj.database.util.DataSourceUtil;
import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.context.SpringContext;

public class DynamicGeneratorInterceptor extends DynamicDataSourceAnnotationInterceptor {
    private DynamicRoutingDataSource dynamicRoutingDataSource;
    private DefaultDataSourceCreator dataSourceCreator;

    public DynamicGeneratorInterceptor(Boolean allowedPublicOnly, DsProcessor dsProcessor) {
        super(allowedPublicOnly, dsProcessor);
    }

    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        try {
            if (methodInvocation.getThis() instanceof DynamicSourceGeneratorInterface) {
                DS ds = (DS)methodInvocation.getThis().getClass().getAnnotation(DS.class);
                if (ds != null && StringUtil.isNotEmpty(ds.value())) {
                    String datasourceName = ds.value();
                    DynamicSourceGeneratorInterface m = (DynamicSourceGeneratorInterface)methodInvocation.getThis();
                    String now = null;

                    try {
                        boolean invalid = true;
                        if (Boolean.TRUE.equals(m.cachedConnection())) {
                            if (this.dynamicRoutingDataSource == null) {
                                this.dynamicRoutingDataSource = (DynamicRoutingDataSource) SpringContext.getBean(DynamicRoutingDataSource.class);
                                this.dataSourceCreator = (DefaultDataSourceCreator)SpringContext.getBean(DefaultDataSourceCreator.class);
                            }

                            if (this.dynamicRoutingDataSource.getDataSources().containsKey(datasourceName)) {
                                invalid = false;
                            }
                        }

                        if (invalid) {
                            now = DynamicDataSourceContextHolder.push((String)null);
                            DataSourceUtil dataSource = m.getDataSource();
                            if (dataSource != null) {
                                DataSourceProperty dataSourceProperty = new DataSourceProperty();
                                dataSourceProperty.setUsername(dataSource.getUserName());
                                dataSourceProperty.setPassword(dataSource.getPassword());
                                dataSourceProperty.setUrl(ConnUtil.getUrl(dataSource));
                                dataSourceProperty.setDriverClassName(dataSource.getDriver());
                                this.dynamicRoutingDataSource.addDataSource(datasourceName, this.dataSourceCreator.createDataSource(dataSourceProperty));
                            }
                        }
                    } finally {
                        if (now != null) {
                            DynamicDataSourceContextHolder.poll();
                        }

                    }
                }
            }
        } catch (Exception var13) {
            var13.printStackTrace();
        }

        return methodInvocation.proceed();
    }
}
