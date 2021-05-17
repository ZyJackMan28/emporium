package com.emporium.config;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WXPayConfiguration {


    /*
    * 取yml配置
    * */
    @Bean
    @ConfigurationProperties(prefix = "ep.pay")
    public PayConfig payConfig(){
        return new PayConfig();
    }

    /*
    * 将paycfg注入到wxPay----交给Spring管理
    * */
    @Bean
    public WXPay wxPay(PayConfig payConfig){
        return new WXPay(payConfig, WXPayConstants.SignType.HMACSHA256);
    }
}
