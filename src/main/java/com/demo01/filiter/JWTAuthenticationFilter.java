package com.demo01.filiter;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.client.naming.utils.StringUtils;
import com.demo01.entity.JWTToken;
import com.demo01.exception.BusinessException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * created by wangzelong 2019/1/8 14:17
 */
public class JWTAuthenticationFilter extends BasicHttpAuthenticationFilter {
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest req = (HttpServletRequest) request;
        String authorization = req.getHeader("Authorization");
        String token = req.getParameter("Authorization");
        if (StringUtils.isNotEmpty(authorization) || StringUtils.isNotEmpty(token)) {
            try {
                return executeLogin(request, response);
            } catch (Exception e) {
                e.printStackTrace();
                responseError(e, response);
            }
        }
        return false;
    }

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String authorization = httpServletRequest.getHeader("Authorization");
        if (StringUtils.isEmpty(authorization)) {
            authorization = httpServletRequest.getParameter("Authorization");
        }
        JWTToken token = new JWTToken(authorization);
        try {
            Subject subject = SecurityUtils.getSubject();
            subject.login(token);
            return true;
        } catch (AuthenticationException e) {
            System.out.println("JWTAuthenticationFilter executeLogin:" + e.getLocalizedMessage());
            responseError(e, response);
        }
        return false;
    }

    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        String authorization = req.getHeader("Authorization");
        return authorization != null;
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        return super.onLoginFailure(token, e, request, response);
    }

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        String authorization = req.getHeader("Authorization");
        if (StringUtils.isEmpty(authorization)) {
            return null;
        }
        return new JWTToken(authorization);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        //创建令牌
        AuthenticationToken token = createToken(request, response);
        if (null == token) {
//            HttpUtil.setApiResponse((HttpServletResponse) response, CodeConstant.UNAUTHORIZED, MsgUtil.getMessage("illegal.request", null), null);
            responseError(new BusinessException(401, "illegal.request"), response);
            return false;
        }
        try {
            Subject subject = SecurityUtils.getSubject();
            subject.login(token);
            return true;
        } catch (AuthenticationException e) { //认证失败，发送401状态并附带异常信息
            System.out.println("onAccessDenied:" + e.getMessage());
            WebUtils.toHttp(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
        return false;
    }

    /**
     * 非法url返回身份错误信息
     */
    private void responseError(Exception ex, ServletResponse response) {
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("utf-8");
            out = response.getWriter();
            response.setContentType("application/json; charset=utf-8");
            Map<String, Object> map = new HashMap<>();
            map.put("code", 401);
            if (ex instanceof AuthenticationException) {
                AuthenticationException authenticationException = (AuthenticationException) ex;
                map.put("message", authenticationException.getMessage());
                out.print(JSONObject.toJSONString(map));
            } else {
                out.print(JSONObject.toJSONString(map));
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
