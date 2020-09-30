package com.demo01.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

/**
 * @author liu dong 2020/9/30 10:35
 */
@Data
public class User extends Model<User> {

    private int id;

    private String userName;

    private String passWord;
}
