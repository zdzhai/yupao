package com.yupi.yupao.service.once;

import com.yupi.yupao.mapper.UserMapper;
import com.yupi.yupao.model.domain.User;
import com.yupi.yupao.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author Zhai Zhidong
 * @version 1.0
 * @Date 2023/2/2 19:54
 */
@SpringBootTest
public class InsertUsersTest {
    @Resource
    private UserService userService;

    //可以自定义线程池
    private ExecutorService executorService = new ThreadPoolExecutor(
            60, 1000, 10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));
    /**
     * 批量插入用户
     */
    @Test
    public void doInsertUsers(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final  int INSERT_NUM = 100000;
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("假鱼皮");
            user.setUserAccount("fakeyupi");
            user.setAvatarUrl("https://636f-codenav-8grj8px727565176-1256524210.tcb.qcloud.la/img/logo.png");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setEmail("123@qq.com");
            user.setUserStatus(0);
            user.setPhone("123");
            user.setUserRole(0);
            user.setPlanetCode("1111111111");
            user.setTags("[]");
            user.setProfile("");
            userList.add(user);
        }
        //11秒10万条
        userService.saveBatch(userList,1000);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
    /**
     * 并发批量插入用户
     */
    @Test
    public void doConcurrencyInsertUsers(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<CompletableFuture> futureList = new ArrayList<>();
        final  int INSERT_NUM = 100000;
        int batchSize = 5000;
        int j = 0;
        for (int i = 0; i < 20; i++) {
        List<User> userList = new ArrayList<>();
            while (true){
                j++;
                User user = new User();
                user.setUsername("假鱼皮");
                user.setUserAccount("fakeyupi");
                user.setAvatarUrl("https://636f-codenav-8grj8px727565176-1256524210.tcb.qcloud.la/img/logo.png");
                user.setGender(0);
                user.setUserPassword("12345678");
                user.setEmail("123@qq.com");
                user.setUserStatus(0);
                user.setPhone("123");
                user.setUserRole(0);
                user.setPlanetCode("1111111111");
                user.setTags("[]");
                user.setProfile("");
                userList.add(user);
                if (j % batchSize == 0){
                    break;
                }
            }
            //异步执行
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println("threadName"+Thread.currentThread().getName());
                userService.saveBatch(userList,batchSize);
            },executorService);
            futureList.add(future);
        }
        //注意加join()
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        //11秒10万条
        //并发 2.5秒10万条
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
