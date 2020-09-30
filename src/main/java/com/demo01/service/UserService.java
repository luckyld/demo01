package com.demo01.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.demo01.entity.User;

/**
 * @author liu dong 2020/9/30 10:29
 */
public interface UserService extends IService<User> {

    User checkUser(User user);
}
