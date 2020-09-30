package com.demo01.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo01.entity.User;
import com.demo01.mapper.UserMapper;
import com.demo01.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author liu dong 2020/9/30 10:29
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User checkUser(User user) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(User::getUserName, user.getUserName());
        User oldUser = userMapper.selectOne(wrapper);
        if (null != oldUser && DigestUtils.md5Hex(user.getPassWord()).equals(oldUser.getPassWord())) {
            return oldUser;
        }
        return null;
    }
}
