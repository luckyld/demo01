package com.demo01.realm;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.demo01.entity.User;
import com.demo01.service.UserService;
import com.demo01.utils.JWTUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author liu dong 2020/9/29 15:54
 */
public class MyRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        // 获取用户名
        String username = JWTUtil.getUsername(principalCollection.toString());
        System.out.printf("用户授权", username);
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        // 给该用户设置角色，角色信息存在 t_role 表中取
        Set<String> set = new HashSet<>();
        set.add("admin");
        set.add(" 给该用户设置角色");
        authorizationInfo.setRoles(set);
        // 给该用户设置权限，权限信息存在 t_permission 表中取
        Set<String> userSet = new HashSet<>();
        set.add("给该用户设置权限");
        authorizationInfo.setStringPermissions(userSet);
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String token = (String) authenticationToken.getPrincipal();
        String username = JWTUtil.getUsername(token);
        Integer userId = JWTUtil.getUserId(token);
        if (StringUtils.isEmpty(username)) {
            throw new AuthenticationException("token invalid");
        }
        // 根据用户名从数据库中查询该用户
        System.out.printf("用户认证", username);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(User::getUserName, username);
        User one = userService.getOne(wrapper);
        if (null==one){
            throw new AuthenticationException("User didn't existed!");
        }
        if (!JWTUtil.verify(token, username, one.getPassWord())) {
            throw new AuthenticationException("Username or password error");
        }
        return new SimpleAuthenticationInfo(token, token, "authorizingRealm");

    }
}
