package com.yupi.yupao.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yupao.model.domain.User;
import com.yupi.yupao.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author dongdong
 * @Date 2023/2/4 19:28
 * 缓存预热
 */
@Slf4j
@Component
public class PreCacheJob {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private RedissonClient redissonClient;

    List<Long> mainUserList = Arrays.asList(1L);

    //每天执行，预热推荐用户
    /**
     * 定时任务推荐用户
     */
    @Scheduled(cron = "30 50 19 * * *")
    public void doCacheRecommendUser(){
        for (Long userId: mainUserList) {
            RLock lock = redissonClient.getLock("yupao:preCacheJob:doCache:lock");
            try {
                // 只有一个线程能获取到锁
                if (lock.tryLock(0,-1,TimeUnit.MILLISECONDS)){
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
                    String redisKey = String.format("yupao:user:recommend:%s",userId) ;
                    ValueOperations<String,Object> valueOperations = redisTemplate.opsForValue();
                    //无缓存，去查数据库
                    //写缓存
                    try {
                        valueOperations.set(redisKey,userPage,30000, TimeUnit.MILLISECONDS);
                    } catch (Exception exception) {
                        log.error("redis key error",exception);
                    }
                }
            } catch (InterruptedException e){
                log.error("doCacheRecommendUser error", e);
            } finally {
                //只能释放自己的锁
                if (lock.isHeldByCurrentThread()){
                    System.out.println("unLock: " + Thread.currentThread().getId());
                    lock.unlock();
                }
            }
        }

    }
}
