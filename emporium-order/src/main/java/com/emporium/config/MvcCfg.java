package com.emporium.config;

import com.emporium.interceptor.UserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class MvcCfg implements WebMvcConfigurer {

    @Autowired
    private JwtProperties prop;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //添加拦截器--通过registry添加自己的拦截器，并添加路径（）
        //由于spring管理,拦截器new,拦截器里面有spring注入prop,所以只能用spring自己注入
        registry.addInterceptor(new UserInterceptor(prop)).addPathPatterns("/order/**");
    }
}
