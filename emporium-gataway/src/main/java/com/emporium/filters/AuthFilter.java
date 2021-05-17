package com.emporium.filters;

import com.emporium.auth.pojo.UserInfo;
import com.emporium.auth.utils.JwtUtils;
import com.emporium.common.utils.CookieUtils;
import com.emporium.config.FilterProperties;
import com.emporium.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
@Component
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class AuthFilter extends ZuulFilter {

    @Autowired
    private JwtProperties prop;

    @Autowired
    private FilterProperties filterProp;

    @Override
    public String filterType() {
        //前置
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        //数字越小优先级越高
        return FilterConstants.PRE_DECORATION_FILTER_ORDER-1;
    }

    @Override
    public boolean shouldFilter() {
        //上下文
        //request-->之前需要拿上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        //获取request
        HttpServletRequest request = ctx.getRequest();
        //获取请求url--->从端口后开始算
        String path = request.getRequestURI();
        //是否是白名单
        boolean isAllowPath = isAllowPath(path);
        //即不是白名单的都应该拦截
        //根据其接口定义，return true invoke run(),所以拦截逻辑是非白名单作用run()
        return !isAllowPath;
    }

    private boolean isAllowPath(String path) {
        //遍历白名单
        for (String allowPath : filterProp.getAllowPaths()) {
            //只要判断前缀
            if(path.startsWith(allowPath)){
                //放行
                return true;
            }
        }
        // 不放行
        return false;
    }

    /*
    * 拦截逻辑
    * */
    @Override
    public Object run() throws ZuulException {
        //request-->之前需要拿上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        //获取cookie中的token
        String token = CookieUtils.getCookieValue(request,prop.getCookieName());


        try {
            //解析token 校验通过什么都不做，即放行
            UserInfo user = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
            // TODO 校验权限（管理员（非管理员））

        }catch (Exception e){
            //解析token失败的话，未登录，需要拦截
            ctx.setSendZuulResponse(false);
            //返回状态码--未授权，未登录
            ctx.setResponseStatusCode(403);
        }


        return null;
    }
}
