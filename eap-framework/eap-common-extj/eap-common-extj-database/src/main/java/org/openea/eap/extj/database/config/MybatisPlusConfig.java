package org.openea.eap.extj.database.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.aop.DynamicDataSourceAnnotationAdvisor;
import com.baomidou.dynamic.datasource.processor.DsProcessor;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.baomidou.mybatisplus.autoconfigure.SpringBootVFS;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.incrementer.H2KeyGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.github.pagehelper.PageInterceptor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.StringValue;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.openea.eap.extj.base.entity.SuperBaseEntity;
import org.openea.eap.extj.config.ConfigValueUtil;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.database.plugins.*;
import org.openea.eap.extj.database.source.DbBase;
import org.openea.eap.extj.database.source.impl.DbOracle;
import org.openea.eap.extj.database.util.ConnUtil;
import org.openea.eap.extj.database.util.DataSourceUtil;
import org.openea.eap.extj.database.util.DbTypeUtil;
import org.openea.eap.extj.database.util.DynamicDataSourceUtil;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.model.MultiTenantType;
import org.openea.eap.extj.util.ClassUtil;
import org.openea.eap.extj.util.data.DataSourceContextHolder;
import org.springframework.aop.Advisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;

/**
 * MybatisPlus配置类
 *
 * @deprecated
 */
@Slf4j
//@Configuration
//@ComponentScan("org.openea,eap")
//@DependsOn({"tenantDataSourceUtil"})
//@MapperScan(basePackages = {"**.mapper", "mapper", "com.xxl.job.admin.dao"})
public class MybatisPlusConfig {

    /**
     * 对接数据库的实体层
     */
    static final String ALIASES_PACKAGE = "org.openea.eap.*.entity;com.xxl.job.admin.core.model";

    @Autowired
    private DataSourceUtil dataSourceUtil;
    @Autowired
    private ConfigValueUtil configValueUtil;

    @Primary
    @Bean(name = "dataSourceSystem")
    public DataSource dataSourceOne(DynamicDataSourceProperties properties) throws SQLException, IOException, URISyntaxException {
        DataSource dataSource = dynamicDataSource(properties);
        initDynamicDataSource(dataSource, properties);
        return dataSource;
    }

    @Bean(name = "sqlSessionFactorySystem")
    public SqlSessionFactory sqlSessionFactoryOne(@Qualifier("dataSourceSystem") DataSource dataSource) throws Exception {
        return createSqlSessionFactory(dataSource);
    }

    /**
     * 服务中查询其他服务的表数据, 未引用Mapper无法初始化MybatisPlus的TableInfo对象, 无法判断逻辑删除情况, 初始化MybatisPlus所有Entity对象
     * 微服务的情况才进行扫描
     * @param sqlSessionFactory
     * @return
     */
    @Bean
    @ConditionalOnClass(name = "org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient")
    public Object scanAllEntity(SqlSessionFactory sqlSessionFactory){
        Set<Class<?>> classes =  ClassUtil.scanCandidateComponents("org.openea.eap", c->
                !Modifier.isAbstract(c.getModifiers()) && SuperBaseEntity.SuperTBaseEntity.class.isAssignableFrom(c)
        );
        for (Class<?> aClass : classes) {
            MapperBuilderAssistant builderAssistant = new MapperBuilderAssistant(sqlSessionFactory.getConfiguration(), "resource");
            builderAssistant.setCurrentNamespace(aClass.getName());
            TableInfoHelper.initTableInfo(builderAssistant, aClass);
        }
        return null;
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        try{
            //判断是否多租户
            if (configValueUtil.isMultiTenancy()) {
                if(configValueUtil.getMultiTenantType().eq(MultiTenantType.COLUMN)){
                    interceptor.addInnerInterceptor(myTenantLineInnerInterceptor());
                }else if(configValueUtil.getMultiTenantType().eq(MultiTenantType.SCHEMA)){
                    interceptor.addInnerInterceptor(mySchemaInnerInterceptor());
                }else{
                    throw new IllegalArgumentException("config.MultiTenantType 多租户模式设置错误, 支持：SCHEMA, COLUMN");
                }
            }
            //开启逻辑删除插件功能
            if(configValueUtil.isEnableLogicDelete()) {
                interceptor.addInnerInterceptor(myLogicDeleteInnerInterceptor());
            }
            // 新版本分页必须指定数据库，否则分页不生效
            // 不指定会动态生效 多数据源不能指定数据库类型
            interceptor.addInnerInterceptor(new PaginationInnerInterceptor());

            //乐观锁
            interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        }catch (Exception e){
            e.printStackTrace();
        }
        return interceptor;
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

    @Bean("myTenantLineInnerInterceptor")
    @ConditionalOnProperty(prefix = "config", name = "MultiTenantType", havingValue = "COLUMN", matchIfMissing = false)
    public TenantLineInnerInterceptor myTenantLineInnerInterceptor(){
        TenantLineInnerInterceptor tenantLineInnerInterceptor = new MyTenantLineInnerInterceptor();
        tenantLineInnerInterceptor.setTenantLineHandler(new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                return new StringValue(DataSourceContextHolder.getDatasourceName());
            }

            @Override
            public String getTenantIdColumn() {
                return configValueUtil.getMultiTenantColumn();
            }
        });
        return tenantLineInnerInterceptor;
    }

    @Bean("mySchemaInnerInterceptor")
    @ConditionalOnProperty(prefix = "config", name = "MultiTenantType", havingValue = "SCHEMA", matchIfMissing = false)
    public DynamicTableNameInnerInterceptor mySchemaInnerInterceptor() throws Exception {
        DbLinkEntity dbLinkEntity = dataSourceUtil.init();
        DbBase dbBase = DbTypeUtil.getDb(dbLinkEntity);
        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new MySchemaInnerInterceptor();
        HashMap<String, TableNameHandler> map = new HashMap<>(150) ;
        // null空库保护
        List<String> tableNames = new ArrayList<>() ;
//        JdbcUtil.queryCustomMods(SqlComEnum.TABLES.getPrepSqlDto(dbLinkEntity, null), DbTableFieldModel.class)
//                .forEach(dbTableModel-> tableNames.add(dbTableModel.getTable().toLowerCase()));
        //将当前连接库的所有表保存, 在列表中的表才进行切库, 所有表名转小写, 后续比对转小写
        DbBase.dynamicAllTableName = tableNames;
        dynamicTableNameInnerInterceptor.setTableNameHandler(dbBase.getDynamicTableNameHandler());
        return dynamicTableNameInnerInterceptor;
    }




    protected DataSource dynamicDataSource(DynamicDataSourceProperties properties) {
        // 动态路由数据源（关键）
        DynamicRoutingDataSource dataSource = new MyDynamicRoutingDataSource();
        dataSource.setPrimary(properties.getPrimary());
        dataSource.setStrict(properties.getStrict());
        dataSource.setStrategy(properties.getStrategy());
        dataSource.setP6spy(properties.getP6spy());
        dataSource.setSeata(properties.getSeata());
        //创建失败不等待
//        properties.getDruid().setBreakAfterAcquireFailure(false);
//        properties.getDruid().setMaxWait(1000);
        return dataSource;
    }

    private void initDynamicDataSource(@Qualifier("dataSourceSystem") DataSource dataSource1, DynamicDataSourceProperties properties) throws DataException, SQLException, IOException, URISyntaxException {
        DynamicRoutingDataSource dataSource = (DynamicRoutingDataSource) dataSource1;
        //若未配置多数据源， 从主配置复制数据库配置填充多数据源
        boolean isPresentPrimary = properties.getDatasource().entrySet().stream().anyMatch(ds->
                ds.getKey().equals(properties.getPrimary()) || ds.getKey().startsWith(properties.getPrimary()+"_") || properties.getPrimary().equals(ds.getValue().getPoolName())
        );
        if(!isPresentPrimary){
            // null多租户空库保护
            DynamicDataSourceUtil.dynamicDataSourceProperties = properties;
            String url = ConnUtil.getUrl(dataSourceUtil, configValueUtil.isMultiTenancy() ? null : dataSourceUtil.getDbName());
            DataSourceProperty dataSourceProperty = DynamicDataSourceUtil.createDataSourceProperty(dataSourceUtil, url);
            dataSourceProperty.getDruid().setBreakAfterAcquireFailure(false);
            dataSourceProperty.setLazy(false);
            properties.getDatasource().put(properties.getPrimary(), dataSourceProperty);
        }
    }


    @Bean
    public Advisor myDynamicDatasourceGeneratorAdvisor(DsProcessor dsProcessor) {
        DynamicGeneratorInterceptor interceptor = new DynamicGeneratorInterceptor(true, dsProcessor);
        DynamicDataSourceAnnotationAdvisor advisor = new DynamicDataSourceAnnotationAdvisor(interceptor, DS.class);
        return advisor;
    }

    protected DataSource druidDataSource() throws Exception{
        DbBase dbBase = DbTypeUtil.getDb(dataSourceUtil);
        String userName = dataSourceUtil.getUserName();
        String password = dataSourceUtil.getPassword();
        String driver = dbBase.getDriver();
        String url = "";

        if (configValueUtil.isMultiTenancy()) {
            url = ConnUtil.getUrl(dataSourceUtil, null);
        }else {
            url = ConnUtil.getUrl(dataSourceUtil);
        }

        DruidDataSource dataSource = new DruidDataSource();
        if(dbBase.getClass() == DbOracle.class){
            // Oracle特殊创建数据源方式
//            String logonUer = "Default";
            String logonUer = "SYSDBA";
//            String logonUer = "SYSOPER";
            Properties properties = DbOracle.setConnProp(logonUer, userName, password);
            dataSource.setConnectProperties(properties);
        }else {
            dataSource.setUsername(userName);
            dataSource.setPassword(password);
        }
        dataSource.setUrl(url);
        dataSource.setDriverClassName(driver);
        return dataSource;
    }

    public Resource[] resolveMapperLocations() {
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        List<String> mapperLocations = new ArrayList<>();
        mapperLocations.add("classpath:mapper/*/*.xml");
        mapperLocations.add("classpath:mapper/*/*/*.xml");
        mapperLocations.add("classpath*:mybatis-mapper/*.xml");
        List<Resource> resources = new ArrayList<Resource>();
        for (String mapperLocation : mapperLocations) {
            try {
                Resource[] mappers = resourceResolver.getResources(mapperLocation);
                resources.addAll(Arrays.asList(mappers));
            } catch (IOException e) {
                // ignore
            }
        }
        return resources.toArray(new Resource[0]);
    }

    public SqlSessionFactory createSqlSessionFactory(DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        //全局配置
        GlobalConfig globalConfig = new GlobalConfig();
        //配置填充器
        globalConfig.setMetaObjectHandler(new MybatisPlusMetaObjectHandler());
        bean.setGlobalConfig(globalConfig);
        if(configValueUtil.isEnableLogicDelete()) {
            globalConfig.setDbConfig(new GlobalConfig.DbConfig());
            globalConfig.getDbConfig().setLogicDeleteField("deleteMark");
            globalConfig.getDbConfig().setLogicDeleteValue("1");
            globalConfig.getDbConfig().setLogicNotDeleteValue(StringPool.NULL);
        }
        globalConfig.setSqlInjector(new MyDefaultSqlInjector());

        bean.setVfs(SpringBootVFS.class);
        bean.setTypeAliasesPackage(ALIASES_PACKAGE);
        bean.setMapperLocations(resolveMapperLocations());
        bean.setConfiguration(configuration(dataSource));
        bean.setPlugins(new Interceptor[]{pageHelper(), new MyMasterSlaveAutoRoutingPlugin(dataSource), new MyDynamicDataSourceAutoRollbackInterceptor(),new ResultSetInterceptor()});
        return bean.getObject();
    }


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

    public MybatisConfiguration configuration(DataSource dataSource){
        MybatisConfiguration mybatisConfiguration = new MybatisConfiguration();
        mybatisConfiguration.setMapUnderscoreToCamelCase(false);
        mybatisConfiguration.setCacheEnabled(false);
        mybatisConfiguration.addInterceptor(mybatisPlusInterceptor());
        mybatisConfiguration.setLogImpl(Slf4jImpl.class);
        mybatisConfiguration.setJdbcTypeForNull(JdbcType.NULL);
        return mybatisConfiguration;
    }
    @Bean
    public IKeyGenerator keyGenerator() {
        return new H2KeyGenerator();
    }

    @Bean
    public ISqlInjector sqlInjector() {
        return (builderAssistant, mapperClass) -> {

        };
    }

    /**
     * 数据权限插件
     *
     * @return DataScopeInterceptor
     */
//    @Bean
//    @ConditionalOnMissingBean
//    public DataScopeInterceptor dataScopeInterceptor(DataSource dataSource) {
//        return new DataScopeInterceptor(dataSource);
//    }


}
