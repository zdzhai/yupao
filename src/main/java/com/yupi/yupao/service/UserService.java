package com.yupi.yupao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.yupao.model.domain.User;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    /**
     * 根据标签查询用户
     * @param tagList
     * @return
     */
    List<User> searchUsersByTags(List<String> tagList);

    List<User> searchUsersBySQL(List<String> tagNameList);

    /**
     * 更新用户信息
     * @param user
     * @param loginUser
     * @return
     */
    int updateUser(User user, User loginUser);

    /**
     * 当前用户是否是管理员
     * @param loginUser
     * @return
     */
    boolean isAdmin(User loginUser);

    /**
     * 是否是管理员
     * @param request
     * @return
     */
     boolean isAdmin(HttpServletRequest request);

    /**
     * 获得登录用户信息
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);
}
