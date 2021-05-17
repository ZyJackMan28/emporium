package com.emporium.config;

import com.emporium.auth.utils.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

@Data
@ConfigurationProperties(prefix = "ep.jwt")
public class JwtProperties {


    private String pubKeyPath;// 公钥



    private String cookieName;
    private PublicKey publicKey;


    //网关先读取到公钥---然后网关进行拦截(用过滤器对用户请求进行逻辑判断)
    @PostConstruct //构造函数执行完成后执行
    public void init() throws Exception {
        //读取公钥和私钥
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);

    }
}