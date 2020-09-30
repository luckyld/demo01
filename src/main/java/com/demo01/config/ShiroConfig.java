package com.demo01.config;

import com.demo01.filiter.ShiroFiliter;
import com.demo01.realm.MyRealm;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author liu dong 2020/9/29 16:12
 */
@Configuration
public class ShiroConfig {

    /**
     * 注入自定义的 Realm
     *
     * @return MyRealm
     */
    @Bean
    public MyRealm myRealm() {
        MyRealm myRealm = new MyRealm();
        System.out.println("====myRealm注册完成=====");
        return myRealm;
    }

    /**
     * 注入安全管理器
     *
     * @return SecurityManager
     */
    @Bean(name = "securityManager")
    public SecurityManager securityManager(@Qualifier("myRealm") MyRealm myRealm) {
        // 将自定义 Realm 加进来
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        System.out.println("====securityManager注册完成====");
        securityManager.setRealm(myRealm);
        return securityManager;
    }


    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(@Qualifier("securityManager") SecurityManager securityManager) {
        // 定义 shiroFactoryBean
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();

        // 设置自定义的 securityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        //设置自定义filter------------- (改动1)
        Map<String, Filter> filterMap = new LinkedHashMap<>();
        filterMap.put("authc", new ShiroFiliter());
        shiroFilterFactoryBean.setFilters(filterMap);

        // 设置默认登录的 URL，身份认证失败会访问该 URL
//        shiroFilterFactoryBean.setLoginUrl("/login");
        // 设置成功之后要跳转的链接
//        shiroFilterFactoryBean.setSuccessUrl("/success");
        // 设置未授权界面，权限认证失败会访问该 URL
//        shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized");

        // LinkedHashMap 是有序的，进行顺序拦截器配置
        Map<String, String> filterChainMap = new LinkedHashMap<>();

        // 配置可以匿名访问的地址，可以根据实际情况自己添加，放行一些静态资源等，anon 表示放行
//        filterChainMap.put("/css/**", "anon");
//        filterChainMap.put("/imgs/**", "anon");
//        filterChainMap.put("/js/**", "anon");
//        filterChainMap.put("/swagger-*/**", "anon");
//        filterChainMap.put("/swagger-ui.html/**", "anon");

//        // 登录 URL 放行
        filterChainMap.put("/user/login", "anon");
        filterChainMap.put("/user/registerUser", "anon");
        // 以“/user/admin” 开头的用户需要身份认证，authc 表示要进行身份认证
        filterChainMap.put("/**", "authc");

        // “/user/student” 开头的用户需要角色认证，是“admin”才允许
//        filterChainMap.put("/user/student*/**", "roles[admin]");
        // “/user/teacher” 开头的用户需要权限认证，是“user:create”才允许
//        filterChainMap.put("/user/teacher*/**", "perms[\"user:create\"]");

        // 配置 logout 过滤器
//        filterChainMap.put("/logout", "logout");

        // 设置 shiroFilterFactoryBean 的 FilterChainDefinitionMap
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainMap);
        System.out.println("====shiroFilterFactoryBean注册完成====");
        return shiroFilterFactoryBean;
    }

    /**
     *  开启Shiro的注解(如@RequiresRoles,@RequiresPermissions),需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证
     * 配置以下两个bean(DefaultAdvisorAutoProxyCreator和AuthorizationAttributeSourceAdvisor)即可实现此功能
     * @return
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    /**
     * 开启aop注解支持
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

}
