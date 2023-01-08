package com.yupi.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.usercenter.model.domain.User;

import javax.servlet.http.HttpServletRequest;

/**
* @author 62618
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2022-12-30 11:10:59
*/
public interface UserService extends IService<User> {

    /**
     *
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 二次密码
     * @param planetCode 星球编号
     * @return
     */
    long userRegister(String userAccount,String userPassword,String checkPassword,String planetCode);

    /**
     * 用户登录
     * @param username
     * @param userPassword
     * @param request
     * @return
     */
    User userLogin(String username, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 用户注销
     * @return
     */
    int userLogout(HttpServletRequest request);
}
