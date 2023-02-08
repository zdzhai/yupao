package com.yupi.yupao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author dongdong
 * @Date 2022/12/27 16:26
 */
@SpringBootApplication
@EnableScheduling   //任务调度
public class YuPaoApplication {
    public static void main(String[] args) {
        SpringApplication.run(YuPaoApplication.class,args);
    }
}
