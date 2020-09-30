package com.demo01.controller;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.demo01.entity.JWTToken;
import com.demo01.entity.User;
import com.demo01.service.UserService;
import com.demo01.utils.JWTUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liu dong 2020/9/29 9:18
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @NacosValue(value = "${nacos.test:propertie.123}", autoRefreshed = true)
    private String testProperties;

    @Autowired
    private UserService userService;

    @RequiresRoles("admin")
    @GetMapping("/query")
    public Object query() {
        return testProperties;
    }


    @GetMapping("/login")
    public Object login(User user) {
        Map<String, String> map = new HashMap<>();
        User checkUser = userService.checkUser(user);
//        if (null != checkUser) {
            String token = JWTUtil.sign(checkUser);
            map.put("token",token);
//        }
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(token, token);
        // 获取 subject 认证主体
        Subject subject = SecurityUtils.getSubject();
//        try {
            // 开始认证，这一步会跳到我们自定义的 Realm 中
            subject.login(usernamePasswordToken);
//            map.put("user", "用户");
//            map.put("code", "success");
//            return map;
//        } catch (Exception e) {
//            e.printStackTrace();
//            map.put("user", "用户");
//            map.put("code", "error");
            return map;
//        }
    }

    @GetMapping("/registerUser")
    public Object registerUser(User user) {
        user.setPassWord(DigestUtils.md5Hex(user.getPassWord()));
        boolean save = userService.save(user);
        if (save) {
            return true;
        }
        return false;
    }


}
