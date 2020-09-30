package com.demo01.filiter;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.client.naming.utils.StringUtils;
import com.demo01.entity.JWTToken;
import com.demo01.exception.BusinessException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liu dong 2020/9/29 17:39
 */
public class ShiroFiliter extends AccessControlFilter {


    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        System.out.println("isAccessA用户认证llowed");
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        String authorization = req.getHeader("Authorization");
        String token = req.getParameter("Authorization");
        if (StringUtils.isNotEmpty(authorization) || StringUtils.isNotEmpty(token)) {
            UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(authorization, authorization);
            try {
                Subject subject = getSubject(servletRequest, servletResponse);
                subject.login(usernamePasswordToken);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                responseError(e, servletResponse);
            }
        }
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        System.out.println("onAccessDenied");
//        AuthenticationToken token = createToken(servletRequest, servletResponse);
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        String authorization = req.getHeader("Authorization");
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(authorization, authorization);
        if (null == usernamePasswordToken) {
            throw new BusinessException(401, "登录信息不正确或已过期");
        }
        try {
            Subject subject = getSubject(servletRequest, servletResponse);
            subject.login(usernamePasswordToken);
            return true;
        } catch (AuthenticationException e) { //认证失败，发送401状态并附带异常信息
            e.printStackTrace();
            responseError(e, servletResponse);
        }
        return false;
    }


    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        String authorization = req.getHeader("Authorization");
        if (StringUtils.isEmpty(authorization)) {
            return null;
        }
        return new JWTToken(authorization);
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
