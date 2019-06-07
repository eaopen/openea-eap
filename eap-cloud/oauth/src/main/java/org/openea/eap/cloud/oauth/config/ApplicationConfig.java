package org.openea.eap.cloud.oauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(-1)
public class ApplicationConfig extends WebSecurityConfigurerAdapter {

    @Value("${spring.application.name}")
    private String appName;
    @Autowired
    private ApplicationProperties applicationProperties;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.requestMatchers()
//                .antMatchers("/login", "/oauth/authorize")
//                .antMatchers(HttpMethod.OPTIONS, "/oauth/token")
                .antMatchers(HttpMethod.OPTIONS, "/oauth/*")
                .antMatchers("/auth/login", applicationProperties.getLoginProcessUrl(), "/oauth/authorize")
                .and().authorizeRequests()
                .antMatchers("/auth/login", applicationProperties.getLoginProcessUrl()).permitAll()
                .anyRequest().authenticated()
                .and()
                .cors()
                .and()
                .csrf().disable();

        // 表单登录
        http.formLogin()
                // 登录页面
                .loginPage("/auth/login")
                // 登录处理url
                .loginProcessingUrl(applicationProperties.getLoginProcessUrl());
    }


    @Bean
    public StaticApplicationContext staticApplicationContext() {
        return new StaticApplicationContext();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
