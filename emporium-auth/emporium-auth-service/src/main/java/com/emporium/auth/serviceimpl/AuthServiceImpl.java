package com.emporium.auth.serviceimpl;

import com.emporium.auth.client.UserClient;
import com.emporium.auth.config.JwtProperties;
import com.emporium.auth.pojo.UserInfo;
import com.emporium.auth.service.AuthService;
import com.emporium.auth.utils.JwtUtils;
import com.emporium.common.enums.EnumsStatus;
import com.emporium.common.exception.EpException;
import com.emporium.crew.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@EnableConfigurationProperties(JwtProperties.class)
public class AuthServiceImpl implements AuthService {



    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtProperties prop;
    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    public String login(String username, String password) {
        try {
            //校验用户名和密码
            User user = userClient.login(username, password);
            if (user == null) {
                throw new EpException(EnumsStatus.USER_INVALID);
            }
            //生成token
            String token = JwtUtils.generateToken(new UserInfo(user.getId(), username), prop.getPrivateKey(), prop.getExpire());
            return token;
        }catch (Exception e){
            log.error("[授权中心] 用户名或密码有误,用户名称{}",username,e);
            throw new EpException(EnumsStatus.USER_INVALID);
        }
    }
}
