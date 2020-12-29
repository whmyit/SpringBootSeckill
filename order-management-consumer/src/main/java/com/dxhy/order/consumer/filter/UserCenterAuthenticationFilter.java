package com.dxhy.order.consumer.filter;

import cn.hutool.core.util.ObjectUtil;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.config.UserCenterConfig;
import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import com.dxhy.order.consumer.utils.matcher.AntPathMatcher;
import com.dxhy.order.model.R;
import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户中心认证：HTTP请求过滤器
 *
 * @author: <a href="tivenninesongs@163.com">yaoxuguang</a>
 * @createDate: Created in 2020/4/8
 */
@Slf4j
public class UserCenterAuthenticationFilter implements Filter {
    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();
    private FilterConfig filterConfig;
    private UserInfoService userInfoService;
    @Override
    public void init(FilterConfig config) throws ServletException {
    
        filterConfig = config;
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String servletPath = req.getServletPath();
        String link;

        //判断当前请求的URL是否在{Filter不拦截URL集合}中
        if (UserCenterConfig.excludedPaths != null && UserCenterConfig.excludedPaths.trim().length() > 0) {
            log.info("排除路径：{}",UserCenterConfig.excludedPaths);
            log.info("请求路径：{}",servletPath);
            String[] excludedPathArray = UserCenterConfig.excludedPaths.split(",");
            int length = excludedPathArray.length;
            for (String excludedPath : excludedPathArray) {
                link = excludedPath.trim();
                if (ANT_PATH_MATCHER.match(link, servletPath)) {
                    chain.doFilter(request, response);
                    return;
                }
            }
        }
        //获取请求参数中的token信息
        String token = req.getHeader(ConfigureConstant.TOKEN);
        String dxhySsoSessionId = req.getHeader(ConfigureConstant.DXHY_SSO_SESSION_ID);
        String fzyyToken = req.getHeader(ConfigureConstant.FZYYTOKEN);
        log.debug("权限拦截器，请求头token:{},请求头dxhySsoSessionId:{},请求头fzyyToken:{},请求地址为:{}", token, dxhySsoSessionId, fzyyToken, servletPath);
        //如果当前请求为退出登录，则请求用户中心使token失效
        if (UserCenterConfig.logoutPath != null && UserCenterConfig.logoutPath.trim().length() > 0 && UserCenterConfig.logoutPath.equals(servletPath)) {
            //todo 调用用户中心，使当前的token失效
            res.sendRedirect(UserCenterConfig.redirectUrl);
        } else {
        
            if (StringUtils.isBlank(token)) {
                log.warn("token为空，非法访问!,ip:{}", request.getRemoteAddr());
            }
            //验证当前请求的token是否失效，调用查询用户信息接口
            ServletContext sc = filterConfig.getServletContext();
    
            WebApplicationContext cxt = WebApplicationContextUtils.getWebApplicationContext(sc);

            if (cxt != null) {
    
    
                if (userInfoService == null) {
        
                    userInfoService = cxt.getBean(UserInfoService.class);
        
                }
                /**
                 * 用户信息获取拦截,如果用户信息不等于空
                 */
                R userInfo = userInfoService.getUserInfo();
                if (ObjectUtil.isNotEmpty(userInfo) && ConfigureConstant.STRING_0000.equals(userInfo.get(OrderManagementConstant.CODE))) {
                    chain.doFilter(request, response);
                } else {
    
                    log.warn("toekn无效，token:{}", token);
                    Map<String, String> paramMap = new HashMap<String, String>(3);
                    paramMap.put("code", "401");
                    paramMap.put("msg", "token valid");
                    /**
                     * 由于门户未做token拦截,业务系统如果做了会出现嵌套登录页问题,新用户中心先不做,自建项目可以考虑添加
                     *
                     */
//                    paramMap.put("redirectUrl", UserCenterConfig.redirectUrl);
                    PrintWriter writer = res.getWriter();
                    writer.write(JsonUtils.getInstance().toJsonString(paramMap));
                    writer.close();
                }
            }
    
        }
        
    }
    
    @Override
    public void destroy() {
    }
}
