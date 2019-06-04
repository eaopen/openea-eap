package org.openea.config;

import liquibase.integration.spring.SpringLiquibase;
import org.openea.datasource.config.DynamicDataSourceConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;

import javax.sql.DataSource;

/**
 * 自配置文件，如分模块控制数据库版本需要此文件
 */

//@Configuration
//@AutoConfigureBefore(DynamicDataSourceConfig.class)
public class LiquibaseConfig {

    @Value("${liquibase.path}")
    private String path;

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(path);
        liquibase.setContexts("dev,test,prod");
        liquibase.setShouldRun(true);
        return liquibase;
    }


    /**
     *  用户模块Liquibase
     */
    @Bean
    public SpringLiquibase userLiquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        // 用户模块Liquibase文件路径
        liquibase.setChangeLog("classpath:liquibase/user/master.xml");
        liquibase.setDataSource(dataSource);
        liquibase.setShouldRun(true);
        liquibase.setResourceLoader(new DefaultResourceLoader());
        // 覆盖Liquibase changelog表名
        liquibase.setDatabaseChangeLogTable("user_changelog_table");
        liquibase.setDatabaseChangeLogLockTable("user_changelog_lock_table");
        return liquibase;
    }
}
