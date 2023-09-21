package org.openea.eap.framework.datasource;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import lombok.Getter;
import org.openea.eap.extj.util.context.SpringContext;
import org.openea.eap.framework.datasource.dynamicds.EapDynamicRoutingDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;


public class EapDynamicDataSourceUtil {

    @Getter
    private static DynamicRoutingDataSource dynamicRoutingDataSource;
    private static DynamicDataSourceProperties dynamicDataSourceProperties;
    private static DefaultDataSourceCreator defaultDataSourceCreator;

    public static DataSource getDataSource(String ds){
        DataSource dataSource = null;
        if (dynamicRoutingDataSource == null) {
            dynamicRoutingDataSource = SpringContext.getBean(DynamicRoutingDataSource.class);
            defaultDataSourceCreator = SpringContext.getBean(DefaultDataSourceCreator.class);
        }
        if(dynamicRoutingDataSource==null){
           // throw exception
        }
        dataSource = dynamicRoutingDataSource.getDataSource(ds);
        if(dataSource==null){
            dataSource = dynamicRoutingDataSource.determineDataSource();
        }
        return dataSource;
    }

    public static JdbcTemplate getJdbcTemplate(String ds){
        return new JdbcTemplate(getDataSource(ds));
    }


    protected static DataSource dynamicDataSource(DynamicDataSourceProperties properties) {
        // 动态路由数据源（关键）
        DynamicRoutingDataSource dataSource = new EapDynamicRoutingDataSource();
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
}
