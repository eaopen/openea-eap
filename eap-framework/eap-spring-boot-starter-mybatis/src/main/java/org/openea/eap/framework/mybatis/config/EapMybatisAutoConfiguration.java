package org.openea.eap.framework.mybatis.config;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.incrementer.*;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.github.pagehelper.PageInterceptor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.NullValue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.openea.eap.extj.config.ConfigValueUtil;
import org.openea.eap.extj.database.plugins.*;
import org.openea.eap.framework.mybatis.core.handler.DefaultDBFieldHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * MyBaits 配置类
 *
 */
@AutoConfiguration
//value = ${eap.info.base-package}
@MapperScan(value = "org.openea.eap", annotationClass = Mapper.class,
        lazyInitialization = "${mybatis.lazy-initialization:false}") // Mapper 懒加载，目前仅用于单元测试
public class EapMybatisAutoConfiguration {

    @Autowired
    private ConfigValueUtil configValueUtil;

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();

        //开启逻辑删除插件功能
        if(configValueUtil.isEnableLogicDelete()) {
            mybatisPlusInterceptor.addInnerInterceptor(myLogicDeleteInnerInterceptor());
        }

        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor()); // 分页插件

        //乐观锁
        mybatisPlusInterceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return mybatisPlusInterceptor;
    }

    @Bean
    public Interceptor myMasterSlaveAutoRoutingPlugin(DataSource dataSource){
        return  new MyMasterSlaveAutoRoutingPlugin(dataSource);
    }

    @Bean
    public Interceptor myDynamicDataSourceAutoRollbackInterceptor(){
        return new MyDynamicDataSourceAutoRollbackInterceptor();
    }

    @Bean
    public Interceptor resultSetInterceptor(){
        return new ResultSetInterceptor();
    }

    @Bean
    public ISqlInjector sqlInjector(){
        return new MyDefaultSqlInjector();
    }

    @Bean
    public MetaObjectHandler defaultMetaObjectHandler(){
        return new DefaultDBFieldHandler(); // 自动填充参数类
    }


    @Bean("myLogicDeleteInnerInterceptor")
    @ConditionalOnProperty(prefix = "config", name = "EnableLogicDelete", havingValue = "true", matchIfMissing = false)
    public MyLogicDeleteInnerInterceptor myLogicDeleteInnerInterceptor(){
        MyLogicDeleteInnerInterceptor myLogicDeleteInnerInterceptor = new MyLogicDeleteInnerInterceptor();
        myLogicDeleteInnerInterceptor.setLogicDeleteHandler(new LogicDeleteHandler() {
            @Override
            public Expression getNotDeletedValue() {
                return new NullValue();
            }

            @Override
            public String getLogicDeleteColumn() {
                return configValueUtil.getLogicDeleteColumn();
            }
        });
        return myLogicDeleteInnerInterceptor;
    }

    @Bean
    public PageInterceptor pageHelper() {
        PageInterceptor pageHelper = new PageInterceptor();
        // 配置PageHelper参数
        Properties properties = new Properties();
        properties.setProperty("dialectAlias", "kingbase8=com.github.pagehelper.dialect.helper.MySqlDialect");
        properties.setProperty("autoRuntimeDialect", "true");
        properties.setProperty("offsetAsPageNum", "false");
        properties.setProperty("rowBoundsWithCount", "false");
        properties.setProperty("pageSizeZero", "true");
        properties.setProperty("reasonable", "false");
        properties.setProperty("supportMethodsArguments", "false");
        properties.setProperty("returnPageInfo", "none");
        pageHelper.setProperties(properties);
        return pageHelper;
    }
    @Bean
    @ConditionalOnProperty(prefix = "mybatis-plus.global-config.db-config", name = "id-type", havingValue = "INPUT")
    public IKeyGenerator keyGenerator(ConfigurableEnvironment environment) {
        DbType dbType = IdTypeEnvironmentPostProcessor.getDbType(environment);
        if (dbType != null) {
            switch (dbType) {
                case POSTGRE_SQL:
                    return new PostgreKeyGenerator();
                case ORACLE:
                case ORACLE_12C:
                    return new OracleKeyGenerator();
                case H2:
                    return new H2KeyGenerator();
                case KINGBASE_ES:
                    return new KingbaseKeyGenerator();
                case DM:
                    return new DmKeyGenerator();
            }
        }
        // 找不到合适的 IKeyGenerator 实现类
        throw new IllegalArgumentException(StrUtil.format("DbType{} 找不到合适的 IKeyGenerator 实现类", dbType));
    }

}
