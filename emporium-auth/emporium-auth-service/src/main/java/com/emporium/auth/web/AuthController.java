package com.emporium.auth.web;

import com.emporium.auth.config.JwtProperties;
import com.emporium.auth.pojo.UserInfo;
import com.emporium.auth.service.AuthService;
import com.emporium.auth.utils.JwtUtils;
import com.emporium.common.enums.EnumsStatus;
import com.emporium.common.exception.EpException;
import com.emporium.common.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {
    @Autowired
    private AuthService authService;

    @Value("${ep.jwt.cookieName}")
    private String cookieName;


    @Autowired
    private JwtProperties prop;
    /**
     * 用户登录
     * @param username
     * @param password
     * @param response
     * @param request
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @RequestParam("username")String username, @RequestParam("password")String password,
            HttpServletResponse response,HttpServletRequest request){
        //登录
        String token = authService.login(username, password);
        //写入cookie
        CookieUtils.newBuilder(response).httpOnly().request(request)
                    .build(cookieName,token);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    /*
    *  验证用户信息
    *  （让前端拿到user）
    * */
    @GetMapping("verify")
    public ResponseEntity<UserInfo> verifyUser(@CookieValue("EMP_TOKEN") String token,
                                               HttpServletResponse response,HttpServletRequest request){
        //需要从cookie中取
        if (StringUtils.isBlank(token)){
            //如果没有登录,
            throw new EpException(EnumsStatus.USER_NOT_LOGIN);
        }
        //解析
        try {
            UserInfo info = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
            //用户一直操作，不能让用户还要登录，后台需要刷新token
            //时机就是用户需要验证个人信息的时候就需要重新生成token,刷新cookie
            String pToken = JwtUtils.generateToken(info, prop.getPrivateKey(), prop.getExpire());
            //写回cookie
            CookieUtils.newBuilder(response).httpOnly().request(request)
                    .build(cookieName,pToken);
            //前端验证verify-->可以拿到info
            return ResponseEntity.ok(info);
        } catch (Exception e){
            //运行异常--说明token失效或者错误，都是未登录,
            throw new EpException(EnumsStatus.USER_NOT_LOGIN);
        }

    }

}
