package com.yupi.yupao.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author dongdong
 * @Date 2023/2/10 22:09
 * 返回用户视图对象
 */
@Data
public class UserVO {
    /**
     *
     */

    private Long id;

    /**
     * 用户昵称
     */

    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */

    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态0-正常
     */
    private Integer userStatus;

    /**
     * 电话
     */
    private String phone;

    /**
     * 创建时间
     */
    private Date creatTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 用户角色：0 普通用户 1管理员
     */
    private Integer userRole;

    /**
     * 星球编号
     */
    private String planetCode;

    /**
     * 标签 json列表
     */
    private String tags;

    /**
     * 个人简介
     */
    private String profile;
}
