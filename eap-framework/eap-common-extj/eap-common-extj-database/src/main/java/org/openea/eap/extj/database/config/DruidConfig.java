package org.openea.eap.extj.database.config;


import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.openea.eap.extj.database.util.ConnUtil;
import org.openea.eap.extj.database.util.DataSourceUtil;
import org.openea.eap.extj.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据源配置
 *
 * @deprecated
 */
//@Configuration
public class DruidConfig {

    @Autowired
    DataSourceUtil dataSourceUtil;

//    @Bean
    public DataSource druid() throws DataException {
        return ConnUtil.getDruidDataSource(dataSourceUtil);
    }


    /**
     * 配置Druid的监控
     * 配置一个管理后台的Servlet
     * @return
     */
    @Bean
    public ServletRegistrationBean statViewServlet(){
        ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        Map<String, String> initParams = new HashMap<>(4);
        //配置druid页面监控的账户密码
        initParams.put("loginUsername", "jnpf");
        initParams.put("loginPassword","123456");
        //默认就是允许所有访问
        initParams.put("allow","");
        //黑名单  注意 黑名单优先于 allow白名单加载 两个名单中不可重复
        initParams.put("deny","192.168.10.21");

        bean.setInitParameters(initParams);
        return bean;
    }


    /**
     * 配置一个web监控的filter
     * @return
     */
    @Bean
    public FilterRegistrationBean webStatFilter(){
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new WebStatFilter());

        Map<String, String> initParams = new HashMap<>(16);
        initParams.put("exclusions","*.js,*.css,/druid/*");

        bean.setInitParameters(initParams);

        bean.setUrlPatterns(Arrays.asList("/*"));

        return  bean;
    }
}
