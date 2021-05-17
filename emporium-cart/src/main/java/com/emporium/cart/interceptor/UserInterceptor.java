package com.emporium.cart.interceptor;

import com.emporium.auth.pojo.UserInfo;
import com.emporium.auth.utils.JwtUtils;
import com.emporium.cart.config.JwtProperties;
import com.emporium.common.utils.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class UserInterceptor implements HandlerInterceptor {


    private JwtProperties prop;

    private static final ThreadLocal<UserInfo> tl = new ThreadLocal<>();

    public UserInterceptor(JwtProperties prop) {
        this.prop = prop;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
             {

        // 先拿到cookie
        // tomcat有个线程池--获取线程[这是共享的]，--[]
        String token = CookieUtils.getCookieValue(request, prop.getCookieName());
        try{
            //解析
            UserInfo user = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
            //已登录放行---需要把用户传递给controller  共享request(同一个tomcat)
            //request.setAttribute("user",user);
            //让controller层去取---SpringMvc不建议使用request域---Spring容器是的单例的，所有共享， 线程共享
            //interceptor ---- controller----service----dom (一次请求共享同一个线程) threadlocal是一个map线程池
            //因为线程可以直接取，而不是get(""),当前线程取
            //Thread.currentThread();
            tl.set(user);
            return true;
        }catch (Exception e){
            //没有登录
            log.error("购物车服务解析用户认证失败！",e);
            return false;
        }

    }

    //用完设置后需要删除，线程池线程并没有结束，下次线程继续处理下一个请求,但ThreadLocal会被回收
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                 @Nullable Exception ex) throws Exception {
        //这个是视图渲染之后,拦截器生效，需要实现webmvc
        tl.remove();
    }

    public static UserInfo getUser(){
        return  tl.get();
    }
}
