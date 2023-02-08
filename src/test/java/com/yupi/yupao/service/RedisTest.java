package com.yupi.yupao.service;
import java.util.Date;

import com.yupi.yupao.model.domain.User;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

/**
 * @author dongdong
 * @Date 2023/2/3 19:41
 */
@SpringBootTest
public class RedisTest {

    @Resource
    private RedisTemplate redisTemplate;


    @Test
    void test(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //增
        valueOperations.set("yupiString","dog");
        valueOperations.set("yupiInt",1);
        valueOperations.set("yupiDouble",1.0);
        User user = new User();
        user.setId(0L);
        user.setUsername("yupi");
        valueOperations.set("yupiUser",user);
        //查
        Object yupi= valueOperations.get("yupiString");
        Assertions.assertTrue("dog".equals((String)yupi));
        yupi = valueOperations.get("yupiInt");
        Assertions.assertTrue(1 == ((Integer)yupi));
        yupi = valueOperations.get("yupiDouble");
        Assertions.assertTrue(1.0 == ((Double)yupi));
        System.out.println(valueOperations.get("yupiUser"));
    }
}
