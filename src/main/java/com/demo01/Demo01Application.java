package com.demo01;

import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author liu dong 2020/9/29 9:18
 */
@MapperScan("com.demo01.mapper")
@NacosPropertySource(dataId = "demo01-config", autoRefreshed = true)
@SpringBootApplication
public class Demo01Application {

    public static void main(String[] args) {
        SpringApplication.run(Demo01Application.class);
    }
}
